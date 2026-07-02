package com.campus.service;

import com.campus.mapper.CouponMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SeckillService {
    private final CouponMapper couponMapper;
    private final StringRedisTemplate redisTemplate;
    private final RedisLock redisLock;

    // Simulate stock in Redis
    private static final String STOCK_KEY = "seckill:stock:";

    public SeckillService(CouponMapper couponMapper, StringRedisTemplate redisTemplate, RedisLock redisLock) {
        this.couponMapper = couponMapper;
        this.redisTemplate = redisTemplate;
        this.redisLock = redisLock;
    }

    // Initialize Redis stock from DB
    public void initStock(Long couponId, int stock) {
        redisTemplate.opsForValue().set(STOCK_KEY + couponId, String.valueOf(stock));
    }

    public Long getRedisStock(Long couponId) {
        String v = redisTemplate.opsForValue().get(STOCK_KEY + couponId);
        return v == null ? -1L : Long.parseLong(v);
    }

    /**
     * Seckill using Redis distributed lock
     */
    public String seckill(Long userId, Long couponId) {
        String lockName = "coupon:" + couponId;
        if (!redisLock.tryLock(lockName, 5)) {
            return "系统繁忙，请稍后再试";
        }
        try {
            String stockStr = redisTemplate.opsForValue().get(STOCK_KEY + couponId);
            if (stockStr == null) {
                // lazy init from DB
                com.campus.entity.Coupon c = couponMapper.findById(couponId);
                if (c == null) return "优惠券不存在";
                redisTemplate.opsForValue().set(STOCK_KEY + couponId, String.valueOf(c.getStock()));
                stockStr = String.valueOf(c.getStock());
            }
            long stock = Long.parseLong(stockStr);
            if (stock <= 0) {
                return "库存不足，抢购失败";
            }
            Long decr = redisTemplate.opsForValue().decrement(STOCK_KEY + couponId);
            if (decr == null || decr < 0) {
                redisTemplate.opsForValue().increment(STOCK_KEY + couponId);
                return "库存不足，抢购失败";
            }
            // Actually deduct from DB
            couponMapper.decreaseStock(couponId);
            return "秒杀成功";
        } finally {
            redisLock.unlock(lockName);
        }
    }

    /**
     * Simulate overselling: regular deduction WITHOUT lock (concurrent unsafe)
     */
    private final AtomicInteger unsafeStock = new AtomicInteger(100);

    public String unsafeBuy() {
        if (unsafeStock.get() <= 0) return "库存不足";
        unsafeStock.decrementAndGet();
        return "购买成功";
    }

    public int getUnsafeStock() {
        return unsafeStock.get();
    }

    public void resetUnsafeStock(int stock) {
        unsafeStock.set(stock);
    }

    /**
     * Simulate overselling with concurrent requests (called from test endpoint)
     */
    public String simulateOverselling(int concurrent, Long couponId) {
        // Reset DB stock
        java.util.concurrent.atomic.AtomicInteger successCount = new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.concurrent.atomic.AtomicInteger failCount = new java.util.concurrent.atomic.AtomicInteger(0);

        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(concurrent);
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(concurrent);

        // Init Redis stock to 10
        redisTemplate.opsForValue().set(STOCK_KEY + couponId, "10");

        for (int i = 0; i < concurrent; i++) {
            executor.submit(() -> {
                try {
                    String stockStr = redisTemplate.opsForValue().get(STOCK_KEY + couponId);
                    if (stockStr != null && Long.parseLong(stockStr) > 0) {
                        redisTemplate.opsForValue().decrement(STOCK_KEY + couponId);
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {}
        executor.shutdown();

        long finalStock = Long.parseLong(redisTemplate.opsForValue().get(STOCK_KEY + couponId));
        return "并发请求:" + concurrent + " 成功:" + successCount.get() + " 失败:" + failCount.get() + " 剩余库存:" + finalStock + " (预期剩余0，超卖:" + (finalStock < 0 ? Math.abs(finalStock) : 0) + ")";
    }
}
