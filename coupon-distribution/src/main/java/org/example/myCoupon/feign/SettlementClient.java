package org.example.myCoupon.feign;


import org.example.myCoupon.exception.CouponException;
import org.example.myCoupon.feign.hystrix.SettlementClientHystrix;
import org.example.myCoupon.vo.CommonResponse;
import org.example.myCoupon.vo.SettlementInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/*
 * 优惠券结算微服务 Feign 接口定义
 */
@FeignClient(value = "coupon-settlement",
    fallback = SettlementClientHystrix.class)
@Component
public interface SettlementClient {

    /*
     * 优惠券规则计算
     */
    @RequestMapping(value = "/coupon-settlement/settlement/compute", method = RequestMethod.POST)
    CommonResponse<SettlementInfo> computeRule(@RequestBody SettlementInfo settlement)
            throws CouponException;

}
