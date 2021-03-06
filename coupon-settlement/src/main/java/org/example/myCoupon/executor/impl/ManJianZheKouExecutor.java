package org.example.myCoupon.executor.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.example.myCoupon.constant.Constant;
import org.example.myCoupon.constant.CouponCategory;
import org.example.myCoupon.constant.CouponStatus;
import org.example.myCoupon.constant.RuleFlag;
import org.example.myCoupon.exception.CouponException;
import org.example.myCoupon.executor.AbstractExecutor;
import org.example.myCoupon.executor.RuleExecutor;
import org.example.myCoupon.vo.CouponKafkaMessage;
import org.example.myCoupon.vo.GoodsInfo;
import org.example.myCoupon.vo.SettlementInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/*
 * 满减 + 折扣优惠券结算规则执行器
 */
@Slf4j
@Component
public class ManJianZheKouExecutor extends AbstractExecutor implements RuleExecutor {

    /*
     * 规则类型标记
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN_ZHEKOU;
    }

    /*
     * 校验商品类型与优惠券是否匹配
     * 需要注意:
     * 1. 这里实现的满减 + 折扣优惠券的校验
     * 2. 如果想要使用多类优惠券, 则必须要所有的商品类型都包含在内, 即差集为空
     */
    @Override
    @SuppressWarnings("all")
    public SettlementInfo computeRule(SettlementInfo settlement) {
        //原价
        double goodsSum = retain2Decimals(goodsCostSum(
                settlement.getGoodsInfos()
        ));
        // 商品类型的校验
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsSum
        );
        if (null != probability) {
            log.debug("ManJian And ZheKou Template Is Not Match To GoodsType!");
            return probability;
        }

        SettlementInfo.CouponAndTemplateInfo manJian = null;
        SettlementInfo.CouponAndTemplateInfo zheKou = null;

        for (SettlementInfo.CouponAndTemplateInfo ct :
                settlement.getCouponAndTemplateInfos()) {
            if (CouponCategory.of(ct.getTemplate().getCategory()) == CouponCategory.MANJIAN) {
                manJian = ct;
            } else {
                zheKou = ct;
            }
        }

        assert null != manJian;
        assert null != zheKou;

        // 当前的折扣优惠券和满减券如果不能共用(一起使用), 清空优惠券, 返回商品原价
        if (!isTemplateCanShared(manJian, zheKou)) {
            log.debug("Current ManJian And ZheKou Can Not Shared!");
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }

        List<SettlementInfo.CouponAndTemplateInfo> ctInfos = new ArrayList<>();

        double manJianBase = (double) manJian.getTemplate().getRule()
                .getDiscount().getBase();
        double manJianQuota = (double) manJian.getTemplate().getRule()
                .getDiscount().getQuota();

        // 最终的价格
        double targetSum = goodsSum;

        // 先计算满减
        if (targetSum >= manJianBase) {
            targetSum -= manJianQuota;
            ctInfos.add(manJian);
        }

        // 再计算折扣
        double zheKouQuota = (double) zheKou.getTemplate().getRule()
                .getDiscount().getQuota();
        targetSum *= zheKouQuota * 1.0 / 100;
        ctInfos.add(zheKou);

        settlement.setCouponAndTemplateInfos(ctInfos);
        settlement.setCost(retain2Decimals(
                targetSum > minCost() ? targetSum : minCost()
        ));

        log.debug("Use ManJian And ZheKou Coupon Make Goods Cost From {} To {}",
                goodsSum, settlement.getCost());

        //核销
        if(settlement.getEmploy()){
            processEmploy(settlement);
        }

        return settlement;
    }


    /*
     * 校验商品类型与优惠券是否匹配
     * 需要注意:
     * 1. 这里实现的满减 + 折扣优惠券的校验
     * 2. 如果想要使用多类优惠券, 则必须要所有的商品类型都包含在内, 即差集为空
     */
    @Override
    @SuppressWarnings("all")
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlement) {
        log.debug("Check ManJian And ZheKou Is Match Or Not!");

        List<Integer> goodsType = settlement.getGoodsInfos()
                .stream().map(GoodsInfo::getType).collect(Collectors.toList());

        //两张券
        List<Integer> templateGoodsType = new ArrayList<>();
        settlement.getCouponAndTemplateInfos().forEach(ct -> {
            templateGoodsType.addAll(JSON.parseObject(
                    ct.getTemplate().getRule().getUsage().getGoodsType(),
                    List.class
            ));
        });

        // 如果想要使用多类优惠券, 则必须要所有的商品类型都包含在内, 即差集为空
        return CollectionUtils.isEmpty(CollectionUtils.subtract(
                goodsType, templateGoodsType
        ));
    }


    /*
     * 当前的两张优惠券（模板）是否可以共用
     * 即校验 TemplateRule 中的 weight 是否满足条件
     */
    @SuppressWarnings("all")
    private boolean
    isTemplateCanShared(SettlementInfo.CouponAndTemplateInfo manJian,
                        SettlementInfo.CouponAndTemplateInfo zheKou) {


        String manjianKey = manJian.getTemplate().getKey()
                + String.format("%04d", manJian.getTemplate().getId());
        List<String> allSharedKeysForManjian = new ArrayList<>();
        allSharedKeysForManjian.add(manjianKey);
        allSharedKeysForManjian.addAll(JSON.parseObject(
                manJian.getTemplate().getRule().getWeight(),
                List.class
        ));


        String zhekouKey = zheKou.getTemplate().getKey()
                + String.format("%04d", zheKou.getTemplate().getId());
        List<String> allSharedKeysForZhekou = new ArrayList<>();
        allSharedKeysForZhekou.add(zhekouKey);
        allSharedKeysForZhekou.addAll(JSON.parseObject(
                zheKou.getTemplate().getRule().getWeight(),
                List.class
        ));

        log.info("allSharedKeysForManJian = {}",allSharedKeysForManjian);
        log.info("allSharedKeysForZhekou = {}",allSharedKeysForZhekou);
        log.info("Arrays.asList(manjianKey, zhekouKey) = {}",Arrays.asList(manjianKey, zhekouKey));

        return CollectionUtils.isSubCollection(Arrays.asList(manjianKey, zhekouKey), allSharedKeysForManjian)
                ||
                CollectionUtils.isSubCollection(Arrays.asList(manjianKey, zhekouKey), allSharedKeysForZhekou);
    }

    /*
     *核销后，更新coupon状态
     */
    @Override
    public void processEmploy(SettlementInfo settlement) {

        List<Integer> couponIds = settlement.getCouponAndTemplateInfos()
                .stream().map(SettlementInfo.CouponAndTemplateInfo::getId).collect(Collectors.toList());

        //更新缓存
        try {
            redisService.addCouponToCache(
                    settlement.getUserId(),
                    couponDao.findAllById(couponIds),
                    CouponStatus.USED.getCode()
            );
        } catch (CouponException e) {
            e.printStackTrace();
        }


        //通过kafka,更新coupon状态为used
        //发送到 kafka 中做异步处理, 更新db状态
        kafkaTemplate.send(
                Constant.TOPIC,
                JSON.toJSONString(new CouponKafkaMessage(
                        CouponStatus.USED.getCode(),
                        couponIds
                ))
        );
    }

}
