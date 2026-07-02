package com.campus.mapper;

import com.campus.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OperationLogMapper {
    int insert(OperationLog log);
    List<OperationLog> findByUserId(@Param("userId") Long userId);
    List<OperationLog> findAll(@Param("limit") int limit);
}
