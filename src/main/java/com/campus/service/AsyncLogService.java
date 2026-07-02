package com.campus.service;

import com.campus.entity.OperationLog;
import com.campus.mapper.OperationLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AsyncLogService {
    private static final Logger log = LoggerFactory.getLogger(AsyncLogService.class);
    private final OperationLogMapper mapper;

    public AsyncLogService(OperationLogMapper mapper) {
        this.mapper = mapper;
    }

    @Async("asyncLogExecutor")
    public void logOperation(Long userId, String type, String content) {
        OperationLog ol = new OperationLog();
        ol.setUserId(userId);
        ol.setType(type);
        ol.setContent(content);
        ol.setCreateTime(LocalDateTime.now());
        mapper.insert(ol);
        log.info("[async-log-{}] userId={}, content={}", Thread.currentThread().getName(), userId, content);
    }
}
