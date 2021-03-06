package org.example.myCoupon.service;

import org.example.myCoupon.entity.CouponTemplate;
import org.example.myCoupon.exception.CouponException;
import org.example.myCoupon.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/*
 * 优惠券模板基础(view, delete...)服务定义
 */
public interface ITemplateBaseService {

    /*
     * 根据优惠券模板 id 获取优惠券模板信息(主要是运营人员自用）
     */
    CouponTemplate buildTemplateInfo(Integer id) throws CouponException;

    /*
     * 查找所有可用的优惠券模板
     */
    List<CouponTemplateSDK> findAllUsableTemplate();

    /*
     * 获取模板 ids 到 CouponTemplateSDK 的映射
     */
    Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids);
}
