package com.campus.controller;

import com.campus.dto.Result;
import com.campus.entity.Coupon;
import com.campus.service.CouponService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {
    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/list")
    public Result<List<Coupon>> list() {
        return Result.ok(couponService.list());
    }

    @GetMapping("/{id}")
    public Result<Coupon> detail(@PathVariable Long id) {
        Coupon c = couponService.detail(id);
        return c != null ? Result.ok(c) : Result.fail("优惠券不存在");
    }

    @PostMapping
    public Result<Coupon> create(@RequestBody Coupon coupon) {
        return Result.ok(couponService.create(coupon));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return couponService.delete(id) ? Result.ok() : Result.fail("删除失败");
    }
}
