package org.example.myCoupon.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.myCoupon.dao.CouponTemplateDao;
import org.example.myCoupon.entity.CouponTemplate;
import org.example.myCoupon.exception.CouponException;
import org.example.myCoupon.service.IAsyncService;
import org.example.myCoupon.service.IBuildTemplateService;
import org.example.myCoupon.vo.TemplateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/*
 * 构建优惠券模板接口实现
 */
@Slf4j
@Service
public class BuildTemplateServiceImpl implements IBuildTemplateService {

    /** 异步服务 */
    private final IAsyncService asyncService;

    /** CouponTemplate Dao */
    private final CouponTemplateDao templateDao;

    @Autowired
    public BuildTemplateServiceImpl(IAsyncService asyncService,
                                    CouponTemplateDao templateDao) {
        this.asyncService = asyncService;
        this.templateDao = templateDao;
    }

    /*
     * 创建优惠券模板
     */
    @Override
    public CouponTemplate buildTemplate(TemplateRequest request)
            throws CouponException {

        // 参数合法性校验
        if (!request.validate()) {
            throw new CouponException("BuildTemplate Param Is Not Valid!");
        }

        // 判断同名的优惠券模板是否存在
        if (null != templateDao.findByName(request.getName())) {
            throw new CouponException("Exist Same Name Template!");
        }

        // 构造 CouponTemplate 并保存到数据库中
        CouponTemplate template = requestToTemplate(request);
        template.setCreateTime(new Date()); //设置时间
        template = templateDao.save(template);//返回后带有自增id

        // 根据优惠券模板异步生成优惠券码
        asyncService.asyncConstructCouponByTemplate(template);

        return template;
    }

    /*
     * 将 TemplateRequest 转换为 CouponTemplate
     */
    private CouponTemplate requestToTemplate(TemplateRequest request) {

        return new CouponTemplate(
                request.getName(),
                request.getLogo(),
                request.getDesc(),
                request.getCategory(),
                request.getProductLine(),
                request.getCount(),
                request.getUserId(),
                request.getTarget(),
                request.getRule()
        );
    }
}
