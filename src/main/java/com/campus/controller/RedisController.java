package com.campus.controller;

import com.campus.dto.Result;
import com.campus.entity.Coupon;
import com.campus.service.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/redis")
public class RedisController {
    private final SignInService signInService;
    private final CacheService cacheService;
    private final SeckillService seckillService;

    public RedisController(SignInService signInService, CacheService cacheService, SeckillService seckillService) {
        this.signInService = signInService;
        this.cacheService = cacheService;
        this.seckillService = seckillService;
    }

    @PostMapping("/sign")
    public Result<Map<String, Object>> sign(@RequestParam Long userId) {
        signInService.sign(userId);
        return Result.ok(signInService.signStatus(userId));
    }

    @GetMapping("/sign/status")
    public Result<Map<String, Object>> signStatus(@RequestParam Long userId) {
        return Result.ok(signInService.signStatus(userId));
    }

    @GetMapping("/sign/month")
    public Result<List<Integer>> monthSignDates(@RequestParam Long userId) {
        return Result.ok(signInService.monthSignDates(userId));
    }

    @GetMapping("/hot")
    public Result<List<Coupon>> hotCoupons() {
        return Result.ok(cacheService.getHotCoupons());
    }

    @PostMapping("/seckill")
    public Result<String> seckill(@RequestParam Long userId, @RequestParam Long couponId) {
        return Result.ok(seckillService.seckill(userId, couponId));
    }

    @GetMapping("/seckill/stock")
    public Result<Long> seckillStock(@RequestParam Long couponId) {
        return Result.ok(seckillService.getRedisStock(couponId));
    }

    @PostMapping("/seckill/init")
    public Result<String> initSeckill(@RequestParam Long couponId, @RequestParam int stock) {
        seckillService.initStock(couponId, stock);
        return Result.ok("秒杀库存已初始化: " + stock);
    }

    @PostMapping("/seckill/simulate")
    public Result<String> simulateOverselling(@RequestParam Long couponId, @RequestParam(defaultValue = "50") int concurrent) {
        String result = seckillService.simulateOverselling(concurrent, couponId);
        return Result.ok(result);
    }
}
