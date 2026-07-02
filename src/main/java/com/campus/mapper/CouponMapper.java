package com.campus.mapper;

import com.campus.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CouponMapper {
    List<Coupon> findAll();
    Coupon findById(@Param("id") Long id);
    List<Coupon> findByCategory(@Param("category") String category);
    int insert(Coupon coupon);
    int deleteById(@Param("id") Long id);
    int decreaseStock(@Param("id") Long id);
}
