package org.example.myCoupon.feign.hystrix;

import lombok.extern.slf4j.Slf4j;
import org.example.myCoupon.feign.TemplateClient;
import org.example.myCoupon.vo.CommonResponse;
import org.example.myCoupon.vo.CouponTemplateSDK;
import org.springframework.stereotype.Component;

import java.util.*;

/*
 * 优惠券模板 Feign 接口的熔断降级策略
 */
@Slf4j
@Component
public class TemplateClientHystrix implements TemplateClient {

    /*
     * 查找所有可用的优惠券模板
     */
    public CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate() {

        log.error("[coupon-template] findAllUsableTemplate " +
                "request error");

        return new CommonResponse<>(
                -1,
                "[coupon-template] request error",
                Collections.emptyList()
        );
    }

    /*
     * 获取模板 ids 到 CouponTemplateSDK 的映射
     */
    public CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(Collection<Integer> ids) {

        log.error("[coupon-template] findIds2TemplateSDK" +
                "request error");

        return new CommonResponse<>(
                -1,
                "[coupon-template] request error",
                new HashMap<>()
        );
    }
}
