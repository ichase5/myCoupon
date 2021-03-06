package org.example.myCoupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/*
token校验器
 */
//@Component
@Slf4j
public class TokenFilter extends AbstractPreZuulFilter{

    @Override
    protected Object cRun() {
        HttpServletRequest request = ctx.getRequest();

        log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
        Object token = request.getParameter("token");
        if (null == token) {
            log.error("error: token is empty");
            return fail(401, "error: token is empty");
        }

        //todo 应该进一步校验token和session

        return success();
    }

    @Override
    public int filterOrder() {
        return 1;
    }
}
