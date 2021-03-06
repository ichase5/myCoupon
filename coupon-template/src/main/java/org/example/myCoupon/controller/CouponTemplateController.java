package org.example.myCoupon.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.myCoupon.entity.CouponTemplate;
import org.example.myCoupon.exception.CouponException;
import org.example.myCoupon.service.IBuildTemplateService;
import org.example.myCoupon.service.ITemplateBaseService;
import org.example.myCoupon.vo.CouponTemplateSDK;
import org.example.myCoupon.vo.TemplateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/*
 * 优惠券模板相关的功能控制器
 */
@Slf4j
@RestController
public class CouponTemplateController {

    /** 构建优惠券模板服务 */
    private final IBuildTemplateService buildTemplateService;

    /** 优惠券模板基础服务 */
    private final ITemplateBaseService templateBaseService;

    @Autowired
    public CouponTemplateController(IBuildTemplateService buildTemplateService,
                                    ITemplateBaseService templateBaseService) {
        this.buildTemplateService = buildTemplateService;
        this.templateBaseService = templateBaseService;
    }

    /*
     * 构建优惠券模板
     * 127.0.0.1:7001/coupon-template/template/build
     * 127.0.0.1:9000/icoupon/coupon-template/template/build
     */
    @PostMapping("/template/build")
    public CouponTemplate buildTemplate(@RequestBody TemplateRequest request)
            throws CouponException {
        log.info("Build Template: {}", JSON.toJSONString(request));
        return buildTemplateService.buildTemplate(request);
    }

    /*
     * 获取优惠券模板详情
     * 127.0.0.1:7001/coupon-template/template/info?id=1
     */
    @GetMapping("/template/info")
    public CouponTemplate buildTemplateInfo(@RequestParam("id") Integer id)
            throws CouponException {
        log.info("Build Template Info For: {}", id);
        return templateBaseService.buildTemplateInfo(id);
    }

    /*
     * 查找所有可用的优惠券模板
     * 127.0.0.1:7001/coupon-template/template/sdk/all
     * */
    @GetMapping("/template/sdk/all")
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        log.info("Find All Usable Template.");
        return templateBaseService.findAllUsableTemplate();
    }

    /*
     * 获取模板 ids 到 CouponTemplateSDK 的映射
     * 127.0.0.1:7001/coupon-template/template/sdk/infos
     */
    @GetMapping("/template/sdk/infos")
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(
            @RequestParam("ids") Collection<Integer> ids
    ) {
        log.info("FindIds2TemplateSDK: {}", JSON.toJSONString(ids));
        return templateBaseService.findIds2TemplateSDK(ids);
    }
}
