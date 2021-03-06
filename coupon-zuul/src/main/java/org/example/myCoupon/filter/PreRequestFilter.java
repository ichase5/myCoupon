package org.example.myCoupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 记录客户端发起请求的时间戳
 */
@Slf4j
@Component
public class PreRequestFilter extends AbstractPreZuulFilter {

    @Override
    protected Object cRun() {
        ctx.set("startTime", System.currentTimeMillis());
        return success();
    }

    @Override
    public int filterOrder() {
        return 0;
    }
}
