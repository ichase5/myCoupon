package org.example.myCoupon.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.myCoupon.dao.CouponTemplateDao;
import org.example.myCoupon.entity.CouponTemplate;
import org.example.myCoupon.exception.CouponException;
import org.example.myCoupon.service.ITemplateBaseService;
import org.example.myCoupon.vo.CouponTemplateSDK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 优惠券模板基础服务接口实现
 */
@Slf4j
@Service
public class TemplateBaseServiceImpl implements ITemplateBaseService {

    /* CouponTemplate Dao */
    private final CouponTemplateDao templateDao;

    @Autowired
    public TemplateBaseServiceImpl(CouponTemplateDao templateDao) {
        this.templateDao = templateDao;
    }

    /*
     * 根据优惠券模板 id 获取优惠券模板信息
     */
    @Override
    public CouponTemplate buildTemplateInfo(Integer id) throws CouponException {

        Optional<CouponTemplate> template = templateDao.findById(id);
        if (!template.isPresent()) {
            throw new CouponException("Template Is Not Exist: " + id);
        }

        return template.get();
    }

    /*
     * 查找所有可用的优惠券模板
     */
    @Override
    public List<CouponTemplateSDK> findAllUsableTemplate() {

        List<CouponTemplate> templates =
                templateDao.findAllByAvailableAndExpired(
                        true, false);

        return templates.stream()
                .map(this::template2TemplateSDK).collect(Collectors.toList());
    }

    /*
     * 获取模板 ids 到 CouponTemplateSDK 的映射
     */
    @Override
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(
            Collection<Integer> ids) {

        List<CouponTemplate> templates = templateDao.findAllById(ids);

        return templates.stream().map(this::template2TemplateSDK)
                .collect(Collectors.toMap(
                        CouponTemplateSDK::getId, Function.identity()
                ));
    }

    /*
     * 将 CouponTemplate 转换为 CouponTemplateSDK
     */
    private CouponTemplateSDK template2TemplateSDK(CouponTemplate template) {

        return new CouponTemplateSDK(
                template.getId(),
                template.getName(),
                template.getLogo(),
                template.getDesc(),
                template.getCategory().getCode(),
                template.getProductLine().getCode(),
                template.getKey(),  // 并不是拼装好的 Template Key
                template.getTarget().getCode(),
                template.getRule()
        );
    }
}
