package org.example.myCoupon.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.myCoupon.entity.Coupon;
import org.example.myCoupon.exception.CouponException;
import org.example.myCoupon.service.IUserService;
import org.example.myCoupon.vo.AcquireTemplateRequest;
import org.example.myCoupon.vo.CouponTemplateSDK;
import org.example.myCoupon.vo.SettlementInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * 优惠券分发相关的功能控制器
 */
@Slf4j
@RestController
public class CouponDistributionController {

    private final IUserService userService;

    @Autowired
    public CouponDistributionController(IUserService userService) {
        this.userService = userService;
    }

    /*
     * 根据用户 id 和状态查询优惠券记录
     * localhost:9000/icoupon/coupon-distribution/distribution/findByStatus?userId=?&status=?
     */
    @GetMapping("/distribution/findByStatus")
    public List<Coupon> findCouponsByStatus(@RequestParam("userId") Long userId, @RequestParam("status") Integer status)
            throws CouponException{
        log.info("findCouponsByStatus: userId = {} , status = {} ",userId,status);
        return userService.findCouponsByStatus(userId,status);
    }

    /*
     * 根据用户 id 查找当前可以领取的优惠券模板
     * localhost:9000/icoupon/coupon-distribution/distribution/findAllAvailable
     */
    @GetMapping("/distribution/findAllAvailable")
    public List<CouponTemplateSDK> findAvailableTemplate(@RequestParam("userId") Long userId)
            throws CouponException{
        log.info("findAvailableTemplate: userId = {}",userId);
        return userService.findAvailableTemplate(userId);
    }

    /*
     * 用户领取优惠券
     * localhost:9000/icoupon/coupon-distribution/distribution/acquireTemplate
     */
    @PostMapping("/distribution/acquireTemplate")
    public Coupon acquireTemplate(@RequestBody AcquireTemplateRequest request)
            throws CouponException{
        log.info("acquireTemplate, request = {}", JSON.toJSONString(request));
        return userService.acquireTemplate(request);
    }

    /*
     * 结算(核销)优惠券
     */
    @PostMapping("/distribution/settlement")
    public SettlementInfo settlement(SettlementInfo info) throws CouponException{
        log.info("settlement info = {}", JSON.toJSONString(info));
        return userService.settlement(info);
    }


}
