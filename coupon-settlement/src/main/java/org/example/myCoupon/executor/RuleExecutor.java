package org.example.myCoupon.executor;


import org.example.myCoupon.constant.RuleFlag;
import org.example.myCoupon.vo.SettlementInfo;

/*
 * 优惠券模板规则处理器定义
 */
public interface RuleExecutor {

    /*
     * 规则类型标记
     */
    RuleFlag ruleConfig();

    /*
     * 优惠券规则的计算
     */
    SettlementInfo computeRule(SettlementInfo settlement);

}
