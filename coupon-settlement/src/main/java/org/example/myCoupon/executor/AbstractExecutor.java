package org.example.myCoupon.executor;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.example.myCoupon.constant.Constant;
import org.example.myCoupon.constant.CouponStatus;
import org.example.myCoupon.dao.CouponDao;
import org.example.myCoupon.exception.CouponException;
import org.example.myCoupon.service.IRedisService;
import org.example.myCoupon.vo.CouponKafkaMessage;
import org.example.myCoupon.vo.GoodsInfo;
import org.example.myCoupon.vo.SettlementInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * 规则执行器抽象类
 */
public abstract class AbstractExecutor {

    /** Kafka 客户端 */
    @Autowired
    protected KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    protected IRedisService redisService;

    @Autowired
    protected CouponDao couponDao;

    /*
     * 校验商品类型与优惠券是否匹配
     * 需要注意:
     * 1. 这里实现的单品类优惠券的校验, 多品类优惠券重载此方法
     * 2. 商品只需要有一个优惠券要求的商品类型去匹配就可以
     */
    @SuppressWarnings("all")
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlement) {

        List<Integer> goodsType = settlement.getGoodsInfos()
                .stream().map(GoodsInfo::getType)
                .collect(Collectors.toList());

        //就一张优惠券
        List<Integer> templateGoodsType = JSON.parseObject(
                settlement.getCouponAndTemplateInfos().get(0).getTemplate()
                        .getRule().getUsage().getGoodsType(),
                List.class
        );

        // 存在交集即可
        return CollectionUtils
                .isNotEmpty(CollectionUtils.intersection(goodsType, templateGoodsType)
                );

    }


    /*
     * 处理商品类型与优惠券限制不匹配的情况
     */
    protected SettlementInfo processGoodsTypeNotSatisfy(
            SettlementInfo settlement, double goodsSum) {

        boolean isGoodsTypeSatisfy = isGoodsTypeSatisfy(settlement);

        // 当商品类型不满足时, 直接返回总价, 并清空优惠券
        if (!isGoodsTypeSatisfy) {
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }

        return null;
    }


    /*
     * 商品总价
     */
    protected double goodsCostSum(List<GoodsInfo> goodsInfos) {

        return goodsInfos.stream().mapToDouble(
                g -> g.getPrice() * g.getCount()
        ).sum();
    }


    /*
     * 保留两位小数
     */
    protected double retain2Decimals(double value) {

        return new BigDecimal(value).setScale(
                2, BigDecimal.ROUND_HALF_UP
        ).doubleValue();
    }


    /*
     * 最小支付费用
     */
    protected double minCost() {
        return 0.1;
    }

    /*
     *核销后，更新coupon状态
     */
    public void processEmploy(SettlementInfo settlement) {

        Integer couponId = settlement.getCouponAndTemplateInfos().get(0).getId();

        //更新缓存
        try {
            redisService.addCouponToCache(
                    settlement.getUserId(),
                    couponDao.findAllById(Collections.singletonList(couponId)),
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
                        Collections.singletonList(couponId)
                ))
        );
    }


}
