package org.example.myCoupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class AccessLogFilter extends AbstractPostZuulFilter {

    @Override
    protected Object cRun() {

        HttpServletRequest request = ctx.getRequest();

        //耗时统计
        Long startTime = (Long) ctx.get("startTime");
        long duration = System.currentTimeMillis() - startTime;

        String uri = request.getRequestURI();
        // 从网关通过的请求都会打印日志记录: uri + duration
        log.info("uri: {}, duration: {} ms", uri, duration);

        return success();
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
    }
}
