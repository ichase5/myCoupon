package org.example.myCoupon;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.example.myCoupon.dao.CouponTemplateDao;
import org.example.myCoupon.entity.CouponTemplate;
import org.example.myCoupon.exception.CouponException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;


@SpringBootTest
@RunWith(SpringRunner.class)
public class TemplateApplicationTests {

    @Autowired
    private CouponTemplateDao templateDao;

    @Test
    public void contextLoad() {
        Optional<CouponTemplate> template = templateDao.findById(1);

        System.out.println(template.get());
    }

}
