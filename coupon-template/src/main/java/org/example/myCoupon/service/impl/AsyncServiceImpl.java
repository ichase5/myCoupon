package org.example.myCoupon.service.impl;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.myCoupon.constant.Constant;
import org.example.myCoupon.dao.CouponTemplateDao;
import org.example.myCoupon.entity.CouponTemplate;
import org.example.myCoupon.service.IAsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/*
 * 异步服务接口实现
 */
@Slf4j
@Service
public class AsyncServiceImpl implements IAsyncService {

    /* CouponTemplate Dao */
    private final CouponTemplateDao templateDao;

    /* 注入 Redis 模板类 */
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public AsyncServiceImpl(CouponTemplateDao templateDao,
                            StringRedisTemplate redisTemplate) {
        this.templateDao = templateDao;
        this.redisTemplate = redisTemplate;
    }

    /*
     * 根据模板异步的创建优惠券码
     */
    @Async("getAsyncExecutor")
    @Override
    public void asyncConstructCouponByTemplate(CouponTemplate template) {

        Stopwatch watch = Stopwatch.createStarted();

        Set<String> couponCodes = buildCouponCode(template);

        // e.g. coupon_template_code_1
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE, template.getId().toString());
        log.info("Push CouponCode To Redis: {}",
                redisTemplate.opsForList().rightPushAll(redisKey, couponCodes));

        template.setAvailable(true);
        templateDao.save(template);

        watch.stop();
        log.info("Construct CouponCode By Template Cost: {}ms",
                watch.elapsed(TimeUnit.MILLISECONDS));

        // TODO 发送短信或者邮件通知优惠券模板已经可用
        log.info("CouponTemplate({}) Is Available!", template.getId());
    }

    /*
     * 构造优惠券码
     * 优惠券码(对应于每一张优惠券, 18位)
     * 前四位: 产品线 + 类型
     * 中间六位: 日期随机(190101)
     * 后八位: 0 ~ 9 随机数构成
     */
    @SuppressWarnings("all")
    private Set<String> buildCouponCode(CouponTemplate template) {

        Stopwatch watch = Stopwatch.createStarted();

        Set<String> result = new HashSet<>(template.getCount());

        // 前四位
        String prefix4 = template.getProductLine().getCode().toString()
                + template.getCategory().getCode();
        String date = new SimpleDateFormat("yyMMdd")
                .format(template.getCreateTime());

        for (int i = 0; i != template.getCount(); ++i) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        //生成的CouponCode可能重复，可能数量不够
        while (result.size() < template.getCount()) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        assert result.size() == template.getCount();

        watch.stop();
        log.info("Build Coupon Code Cost: {}ms",
                watch.elapsed(TimeUnit.MILLISECONDS));

        return result;
    }

    /*
     * 构造优惠券码的后 14 位
     * @param date 创建优惠券的日期
     * @return 14 位优惠券码
     */
    private String buildCouponCodeSuffix14(String date) {

        char[] bases = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'};

        // 中间六位
        List<Character> chars = date.chars()
                .mapToObj(e -> (char) e).collect(Collectors.toList());
        Collections.shuffle(chars);
        String mid6 = chars.stream()
                .map(Object::toString).collect(Collectors.joining());

        // 后八位
        String suffix8 = RandomStringUtils.random(1, bases)
                + RandomStringUtils.randomNumeric(7);

        return mid6 + suffix8;
    }
}
