package org.example.myCoupon.executor.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.myCoupon.constant.RuleFlag;
import org.example.myCoupon.executor.AbstractExecutor;
import org.example.myCoupon.executor.RuleExecutor;
import org.example.myCoupon.vo.CouponTemplateSDK;
import org.example.myCoupon.vo.SettlementInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;


/*
 * 满减优惠券结算规则执行器
 */
@Slf4j
@Component
public class ManJianExecutor extends AbstractExecutor implements RuleExecutor {

    /*
     * 规则类型标记
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN;
    }

    /*
     * 优惠券规则的计算
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {

        double goodsSum = retain2Decimals(
                goodsCostSum(settlement.getGoodsInfos())
        );
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsSum
        );

        if (null != probability) {
            log.debug("ManJian Template Is Not Match To GoodsType!");
            return probability;
        }

        // 判断满减是否符合折扣标准
        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfos()
                .get(0).getTemplate();
        double base = (double) templateSDK.getRule().getDiscount().getBase(); //满多少才能减
        double quota = (double) templateSDK.getRule().getDiscount().getQuota(); //减多少

        // 如果不符合标准, 则直接返回商品总价
        if (goodsSum < base) {
            log.debug("Current Goods Cost Sum < ManJian Coupon Base!");
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList()); //清空优惠券
            return settlement;
        }

        // 计算使用优惠券之后的价格 - 结算
        settlement.setCost(retain2Decimals(
                Math.max((goodsSum - quota), minCost())
                )
        );
        log.debug("Use ManJian Coupon Make Goods Cost From {} To {}",
                goodsSum, settlement.getCost());

        return settlement;
    }
}
