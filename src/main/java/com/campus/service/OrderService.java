package com.campus.service;

import com.campus.entity.Coupon;
import com.campus.entity.Order;
import com.campus.mapper.CouponMapper;
import com.campus.mapper.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {
    private final OrderMapper orderMapper;
    private final CouponMapper couponMapper;
    private final AsyncLogService asyncLogService;

    public OrderService(OrderMapper orderMapper, CouponMapper couponMapper, AsyncLogService asyncLogService) {
        this.orderMapper = orderMapper;
        this.couponMapper = couponMapper;
        this.asyncLogService = asyncLogService;
    }

    public List<Order> listByUser(Long userId) {
        return orderMapper.findByUserId(userId);
    }

    @Transactional
    public String claim(Long userId, Long couponId) {
        int count = orderMapper.countByUserAndCoupon(userId, couponId);
        if (count > 0) {
            return "已领取过该优惠券";
        }
        int affected = couponMapper.decreaseStock(couponId);
        if (affected == 0) {
            return "优惠券库存不足";
        }
        Order order = new Order();
        order.setUserId(userId);
        order.setCouponId(couponId);
        orderMapper.insert(order);

        Coupon coupon = couponMapper.findById(couponId);
        String couponTitle = coupon != null ? coupon.getTitle() : "未知优惠券";
        asyncLogService.logOperation(userId, "领取优惠券", "领取了「" + couponTitle + "」");

        return null;
    }

    public boolean useCoupon(Long orderId) {
        int affected = orderMapper.updateStatus(orderId, 2);
        if (affected > 0) {
            Order order = orderMapper.findById(orderId);
            if (order != null) {
                asyncLogService.logOperation(order.getUserId(), "使用优惠券", "使用了「" + order.getCouponTitle() + "」");
            }
        }
        return affected > 0;
    }

    public List<Order> listAllWithCoupon(Long userId) {
        return orderMapper.findAllWithCoupon(userId);
    }
}
