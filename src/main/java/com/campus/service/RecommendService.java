package com.campus.service;

import com.campus.entity.Coupon;
import com.campus.entity.Order;
import com.campus.mapper.CouponMapper;
import com.campus.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendService {
    private final OrderMapper orderMapper;
    private final CouponMapper couponMapper;

    public RecommendService(OrderMapper orderMapper, CouponMapper couponMapper) {
        this.orderMapper = orderMapper;
        this.couponMapper = couponMapper;
    }

    public List<Coupon> recommend(Long userId) {
        List<Order> orders = orderMapper.findByUserId(userId);
        if (orders.isEmpty()) {
            return couponMapper.findAll().stream().limit(4).collect(Collectors.toList());
        }
        // 1. 获取用户已领取优惠券的ID集合
        Set<Long> claimedIds = orders.stream().map(Order::getCouponId).collect(Collectors.toSet());
        // 2. 统计用户偏好类别
        Map<String, Long> categoryCount = new HashMap<>();
        for (Order order : orders) {
            Coupon c = couponMapper.findById(order.getCouponId());
            if (c != null) {
                categoryCount.merge(c.getCategory(), 1L, Long::sum);
            }
        }
        // 3. 获取偏好类别中未领取的优惠券
        String topCategory = categoryCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (topCategory != null) {
            List<Coupon> candidates = couponMapper.findByCategory(topCategory);
            List<Coupon> result = candidates.stream()
                    .filter(c -> !claimedIds.contains(c.getId()))
                    .limit(4)
                    .collect(Collectors.toList());
            if (!result.isEmpty()) {
                return result;
            }
        }
        // 4. 兜底：返回所有未领取的
        return couponMapper.findAll().stream()
                .filter(c -> !claimedIds.contains(c.getId()))
                .limit(4)
                .collect(Collectors.toList());
    }
}
