package com.campus.service;

import com.campus.entity.Coupon;
import com.campus.mapper.CouponMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponService {
    private final CouponMapper couponMapper;

    public CouponService(CouponMapper couponMapper) {
        this.couponMapper = couponMapper;
    }

    public List<Coupon> list() {
        return couponMapper.findAll();
    }

    public Coupon detail(Long id) {
        return couponMapper.findById(id);
    }

    public Coupon create(Coupon coupon) {
        couponMapper.insert(coupon);
        return coupon;
    }

    public boolean delete(Long id) {
        return couponMapper.deleteById(id) > 0;
    }
}
