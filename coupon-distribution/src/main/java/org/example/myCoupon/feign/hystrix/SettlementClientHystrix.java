package org.example.myCoupon.feign.hystrix;


import lombok.extern.slf4j.Slf4j;
import org.example.myCoupon.exception.CouponException;
import org.example.myCoupon.feign.SettlementClient;
import org.example.myCoupon.vo.CommonResponse;
import org.example.myCoupon.vo.SettlementInfo;
import org.springframework.stereotype.Component;

import java.util.Set;

/*
 * 结算微服务熔断策略实现
 */
@Slf4j
@Component
public class SettlementClientHystrix implements SettlementClient {

    /*
     * 优惠券规则计算
     */
    public CommonResponse<SettlementInfo> computeRule(SettlementInfo settlement)
            throws CouponException {

        log.error("[coupon-settlement] computeRule" +
                "request error");
        settlement.setEmploy(false);
        settlement.setCost(-1.0);

        return new CommonResponse<>(
                -1,
                "[coupon-settlement] request error",
                settlement
        );
    }
}
