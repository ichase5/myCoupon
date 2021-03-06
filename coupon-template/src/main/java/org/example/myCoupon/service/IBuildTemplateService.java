package org.example.myCoupon.service;


import org.example.myCoupon.entity.CouponTemplate;
import org.example.myCoupon.exception.CouponException;
import org.example.myCoupon.vo.TemplateRequest;

/*
 * 构建优惠券模板接口定义
 */
public interface IBuildTemplateService {

    /*
     * 创建优惠券模板
     */
    CouponTemplate buildTemplate(TemplateRequest request) throws CouponException;
}
