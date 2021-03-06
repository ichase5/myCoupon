package org.example.myCoupon.filter;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;

/*
限流过滤器
对进入网关的所有请求都限流
 */
@Slf4j
@Component
public class RateLimiterFilter extends AbstractPreZuulFilter {

    //令牌桶，每秒两个令牌
    RateLimiter rateLimiter = RateLimiter.create(2.0);

    @Override
    protected Object cRun() {

        HttpServletRequest request = ctx.getRequest();

        if (rateLimiter.tryAcquire()) {
            log.info("get rate token success");
            return success();
        } else {
            log.error("rate limit: {}", request.getRequestURI());
            return fail(402, "error: rate limit");
        }
    }

    @Override
    public int filterOrder() {
        return 2;
    }
}
