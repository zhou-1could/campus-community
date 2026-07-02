package com.campus.mapper;

import com.campus.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    List<Order> findByUserId(@Param("userId") Long userId);
    int insert(Order order);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int countByUserAndCoupon(@Param("userId") Long userId, @Param("couponId") Long couponId);
    List<Order> findAllWithCoupon(@Param("userId") Long userId);
    Order findById(@Param("id") Long id);
}
