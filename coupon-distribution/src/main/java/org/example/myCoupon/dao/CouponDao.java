package org.example.myCoupon.dao;


import org.example.myCoupon.constant.CouponStatus;
import org.example.myCoupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/*
 * Coupon Dao 接口定义
 */
public interface CouponDao extends JpaRepository<Coupon, Integer> { // JpaRepository<实体类，主键类型>

    /*
     * 根据 userId + 状态寻找优惠券记录
     * where userId = ... and status = ...
     */
    List<Coupon> findAllByUserIdAndStatus(Long userId, CouponStatus status);
}
