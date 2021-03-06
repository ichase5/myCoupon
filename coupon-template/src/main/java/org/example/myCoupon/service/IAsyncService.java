package org.example.myCoupon.service;


import org.example.myCoupon.entity.CouponTemplate;

/**
 * 异步服务接口定义
 */
public interface IAsyncService {

    /*
     * 根据模板异步的创建优惠券码
     */
    void asyncConstructCouponByTemplate(CouponTemplate template);
}
