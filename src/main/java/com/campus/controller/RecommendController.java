package com.campus.controller;

import com.campus.dto.Result;
import com.campus.entity.Coupon;
import com.campus.service.RecommendService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendController {
    private final RecommendService recommendService;

    public RecommendController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @GetMapping("/coupons")
    public Result<List<Coupon>> recommend(@RequestParam Long userId) {
        return Result.ok(recommendService.recommend(userId));
    }
}
