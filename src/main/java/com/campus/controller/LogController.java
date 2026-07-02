package com.campus.controller;

import com.campus.dto.Result;
import com.campus.entity.OperationLog;
import com.campus.mapper.OperationLogMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/log")
public class LogController {
    private final OperationLogMapper logMapper;

    public LogController(OperationLogMapper logMapper) {
        this.logMapper = logMapper;
    }

    @GetMapping("/list")
    public Result<List<OperationLog>> list(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return Result.ok(logMapper.findByUserId(userId));
        }
        return Result.ok(logMapper.findAll(200));
    }
}
