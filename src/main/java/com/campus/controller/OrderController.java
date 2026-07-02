package com.campus.controller;

import com.campus.dto.Result;
import com.campus.entity.Order;
import com.campus.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/claim")
    public Result<String> claim(@RequestParam Long userId, @RequestParam Long couponId) {
        String err = orderService.claim(userId, couponId);
        if (err != null) {
            return Result.fail(err);
        }
        return Result.ok("领取成功");
    }

    @GetMapping("/list")
    public Result<List<Order>> list(@RequestParam Long userId) {
        return Result.ok(orderService.listByUser(userId));
    }

    @PutMapping("/use/{id}")
    public Result<Void> use(@PathVariable Long id) {
        return orderService.useCoupon(id) ? Result.ok() : Result.fail("操作失败");
    }
}
