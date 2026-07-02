package com.campus.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class SignInService {
    private final StringRedisTemplate redisTemplate;
    private final AsyncLogService asyncLogService;

    public SignInService(StringRedisTemplate redisTemplate, AsyncLogService asyncLogService) {
        this.redisTemplate = redisTemplate;
        this.asyncLogService = asyncLogService;
    }

    private String key(Long userId, int year) {
        return "sign:" + userId + ":" + year;
    }

    public boolean sign(Long userId) {
        LocalDate now = LocalDate.now();
        int dayOfYear = now.getDayOfYear();
        boolean ok = Boolean.TRUE.equals(
                redisTemplate.opsForValue().setBit(key(userId, now.getYear()), dayOfYear - 1, true));
        if (ok) {
            asyncLogService.logOperation(userId, "签到打卡", "第" + dayOfYear + "天签到 (" + now.getMonthValue() + "月" + now.getDayOfMonth() + "日)");
        }
        return ok;
    }

    public Map<String, Object> signStatus(Long userId) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        String k = key(userId, year);
        int dayOfYear = now.getDayOfYear();

        // Count continuous days by checking backwards from today
        int continuousDays = 0;
        for (int i = dayOfYear - 1; i >= 0; i--) {
            Boolean bit = redisTemplate.opsForValue().getBit(k, i);
            if (Boolean.TRUE.equals(bit)) continuousDays++;
            else break;
        }

        // Count this month's sign-in days
        int month = now.getMonthValue();
        int monthStartDay = LocalDate.of(year, month, 1).getDayOfYear();
        int monthDays = 0;
        for (int i = monthStartDay - 1; i < dayOfYear; i++) {
            if (Boolean.TRUE.equals(redisTemplate.opsForValue().getBit(k, i))) monthDays++;
        }

        // Count total sign-in days for the year
        int totalDays = 0;
        int totalDaysInYear = now.lengthOfYear();
        for (int i = 0; i < dayOfYear; i++) {
            if (Boolean.TRUE.equals(redisTemplate.opsForValue().getBit(k, i))) totalDays++;
        }

        boolean todaySigned = Boolean.TRUE.equals(redisTemplate.opsForValue().getBit(k, dayOfYear - 1));

        Map<String, Object> result = new HashMap<>();
        result.put("todaySigned", todaySigned);
        result.put("continuousDays", continuousDays);
        result.put("monthDays", monthDays);
        result.put("totalDays", totalDays);
        return result;
    }

    public List<Integer> monthSignDates(Long userId) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        String k = key(userId, year);
        int monthStartDay = LocalDate.of(year, month, 1).getDayOfYear();
        int dayOfYear = now.getDayOfYear();

        List<Integer> dates = new ArrayList<>();
        for (int i = monthStartDay - 1; i < dayOfYear; i++) {
            if (Boolean.TRUE.equals(redisTemplate.opsForValue().getBit(k, i))) {
                dates.add(i - monthStartDay + 2); // Convert to 1-based day-of-month
            }
        }
        return dates;
    }
}
