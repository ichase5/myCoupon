package org.example.myCoupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.example.myCoupon.constant.CouponStatus;
import org.example.myCoupon.constant.PeriodType;
import org.example.myCoupon.entity.Coupon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * 用户优惠券的分类, 根据优惠券状态
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponClassify {

    /* 可以使用的 */
    private List<Coupon> usable;

    /* 已使用的 */
    private List<Coupon> used;

    /* 已过期的 */
    private List<Coupon> expired;

    /*
     * 对当前的优惠券进行分类
     */
    public static CouponClassify classify(List<Coupon> coupons) {

        List<Coupon> usable = new ArrayList<>(coupons.size());
        List<Coupon> used = new ArrayList<>(coupons.size());
        List<Coupon> expired = new ArrayList<>(coupons.size());

        coupons.forEach(c -> {

            // 判断优惠券是否过期
            boolean isTimeExpire;
            long curTime = new Date().getTime();

            if (c.getTemplateSDK().getRule().getExpiration().getPeriod().equals(PeriodType.REGULAR.getCode())) {
                isTimeExpire = c.getTemplateSDK().getRule().getExpiration().getDeadline() <= curTime;
            }
            else {
                isTimeExpire = DateUtils.addDays(c.getAssignTime(),
                        c.getTemplateSDK().getRule().getExpiration().getGap()
                ).getTime() <= curTime;
            }

            if (c.getStatus() == CouponStatus.USED) {
                used.add(c);
            }
            // coupon的expire status是惰性过期策略，所以不能直接通过status判断
            else if (c.getStatus() == CouponStatus.EXPIRED || isTimeExpire) {
                expired.add(c);
            }
            else {
                usable.add(c);
            }
        });

        return new CouponClassify(usable, used, expired);
    }
}
