package com.campus.config;

import com.campus.dto.Result;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RedisConnectionFailureException.class)
    public Result<Void> handleRedisDown(RedisConnectionFailureException e) {
        return Result.fail("Redis服务未连接，请先启动Redis");
    }
}
