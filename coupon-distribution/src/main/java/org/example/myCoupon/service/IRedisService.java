package org.example.myCoupon.service;

import org.example.myCoupon.entity.Coupon;
import org.example.myCoupon.exception.CouponException;

import java.util.List;

/*
 * Redis 相关的操作服务接口定义
 * 第一类： 用户的三个状态优惠券 Cache 相关操作
 * 第二类： 优惠券模板生成的优惠券码 Cache 操作
 */
public interface IRedisService {


    //根据 userId 和状态找到缓存的优惠券列表数据
    // 可能会返回 null, 代表从没有过记录
    List<Coupon> getCachedCoupons(Long userId, Integer status);

    //保存空的优惠券列表到缓存中(避免缓存穿透，缓存空值）
    void saveEmptyCouponListToCache(Long userId, List<Integer> status);

    //将优惠券保存到 Cache 中
    Integer addCouponToCache(Long userId, List<Coupon> coupons,
                             Integer status) throws CouponException;


    //尝试从 Cache 中获取一个优惠券码
    // 可能会返回 null
    String tryToAcquireCouponCodeFromCache(Integer templateId);


}
