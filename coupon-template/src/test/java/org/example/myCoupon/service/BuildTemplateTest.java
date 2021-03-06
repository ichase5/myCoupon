package org.example.myCoupon.service;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.time.DateUtils;
import org.example.myCoupon.constant.CouponCategory;
import org.example.myCoupon.constant.DistributeTarget;
import org.example.myCoupon.constant.PeriodType;
import org.example.myCoupon.constant.ProductLine;
import org.example.myCoupon.vo.TemplateRequest;
import org.example.myCoupon.vo.TemplateRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/*
构造优惠券模板服务测试
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BuildTemplateTest {

    @Autowired
    private IBuildTemplateService buildTemplateService;

    @Test
    public void testBuildTemplate() throws Exception {

        System.out.println(JSON.toJSONString(
                buildTemplateService.buildTemplate(fakeTemplateRequest())
        ));
        //优惠券码是异步生成的，防止测试用例关闭导致异步服务停止
        Thread.sleep(5000);
    }

    /*
     * fake TemplateRequest
     */
    private TemplateRequest fakeTemplateRequest() {

        TemplateRequest request = new TemplateRequest();
        request.setName("优惠券模板-" + new Date().getTime());
        request.setLogo("http://www.xyz.com");
        request.setDesc("这是一张优惠券模板");
        request.setCategory(CouponCategory.MANJIAN.getCode());
        request.setProductLine(ProductLine.DAMAO.getCode());
        request.setCount(10000);
        request.setUserId(10001L);  // fake user id
        request.setTarget(DistributeTarget.SINGLE.getCode());

        TemplateRule rule = new TemplateRule();
        rule.setExpiration(new TemplateRule.Expiration(
                PeriodType.SHIFT.getCode(),
                60, DateUtils.addDays(new Date(), 60).getTime()
        ));
        rule.setDiscount(new TemplateRule.Discount(5, 10)); //满10元减五元
        rule.setLimitation(1);
        rule.setUsage(new TemplateRule.Usage(
                "陕西省", "西安市",
                JSON.toJSONString(Arrays.asList("文娱", "家居"))
        ));
        rule.setWeight(JSON.toJSONString(Collections.EMPTY_LIST)); //不可叠加使用

        request.setRule(rule);

        return request;
    }
}
