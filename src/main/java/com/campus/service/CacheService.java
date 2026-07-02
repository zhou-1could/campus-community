package com.campus.service;

import com.campus.entity.Coupon;
import com.campus.mapper.CouponMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final CouponMapper couponMapper;

    private static final String HOT_COUPONS_KEY = "hot:coupons";
    private static final String COUPON_DETAIL_KEY = "coupon:detail:";

    public CacheService(RedisTemplate<String, Object> redisTemplate, CouponMapper couponMapper) {
        this.redisTemplate = redisTemplate;
        this.couponMapper = couponMapper;
    }

    @SuppressWarnings("unchecked")
    public List<Coupon> getHotCoupons() {
        List<Coupon> cached = (List<Coupon>) redisTemplate.opsForValue().get(HOT_COUPONS_KEY);
        if (cached != null) return cached;
        return refreshHotCoupons();
    }

    public List<Coupon> refreshHotCoupons() {
        List<Coupon> coupons = couponMapper.findAll();
        redisTemplate.opsForValue().set(HOT_COUPONS_KEY, coupons, 10, TimeUnit.MINUTES);
        return coupons;
    }

    public Coupon getCouponDetail(Long id) {
        String key = COUPON_DETAIL_KEY + id;
        Coupon cached = (Coupon) redisTemplate.opsForValue().get(key);
        if (cached != null) return cached;
        Coupon coupon = couponMapper.findById(id);
        if (coupon != null) {
            redisTemplate.opsForValue().set(key, coupon, 5, TimeUnit.MINUTES);
        }
        return coupon;
    }

    @Scheduled(fixedRate = 300000)
    public void autoRefreshCache() {
        refreshHotCoupons();
    }
}
