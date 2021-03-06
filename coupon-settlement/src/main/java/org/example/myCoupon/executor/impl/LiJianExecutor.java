package org.example.myCoupon.executor.impl;


import lombok.extern.slf4j.Slf4j;
import org.example.myCoupon.constant.RuleFlag;
import org.example.myCoupon.executor.AbstractExecutor;
import org.example.myCoupon.executor.RuleExecutor;
import org.example.myCoupon.vo.CouponTemplateSDK;
import org.example.myCoupon.vo.SettlementInfo;
import org.springframework.stereotype.Component;

/*
 * 立减优惠券结算规则执行器
 */
@Slf4j
@Component
public class LiJianExecutor extends AbstractExecutor implements RuleExecutor {

    /*
     * 规则类型标记
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.LIJIAN;
    }

    /*
     * 优惠券规则的计算
     */
    @Override
    @SuppressWarnings("all")
    public SettlementInfo computeRule(SettlementInfo settlement) {
        double goodsSum = retain2Decimals(goodsCostSum(
                settlement.getGoodsInfos()
        ));
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsSum
        );
        if (null != probability) {
            log.debug("LiJian Template Is Not Match To GoodsType!");
            return probability;
        }

        // 立减优惠券直接使用, 没有门槛
        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfos()
                .get(0).getTemplate();
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();

        // 计算使用优惠券之后的价格 - 结算
        settlement.setCost(
                retain2Decimals(goodsSum - quota) > minCost() ?
                        retain2Decimals(goodsSum - quota) : minCost()
        );

        log.debug("Use LiJian Coupon Make Goods Cost From {} To {}",
                goodsSum, settlement.getCost());

        return settlement;

    }
}
