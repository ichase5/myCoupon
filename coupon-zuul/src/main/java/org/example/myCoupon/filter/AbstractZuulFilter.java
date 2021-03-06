package org.example.myCoupon.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

public abstract class AbstractZuulFilter extends ZuulFilter {

    private final static String NEXT = "next";

    // 用于在过滤器之间传递消息, 数据保存在每个请求的 ThreadLocal中,线程安全
    // 继承自ConcurrentHashMap
    RequestContext ctx;

    @Override
    public boolean shouldFilter(){
        RequestContext ctx = RequestContext.getCurrentContext();
        return (boolean) ctx.getOrDefault(NEXT, true);
    }

    @Override
    public Object run() throws ZuulException{
        ctx = RequestContext.getCurrentContext();
        return cRun();
    }

    protected abstract Object cRun();

    Object fail(int code, String msg) {

        ctx.set(NEXT, false);
        ctx.setSendZuulResponse(false);
        ctx.getResponse().setContentType("text/html;charset=UTF-8");
        ctx.setResponseStatusCode(code);
        ctx.setResponseBody(String.format("{\"result\": \"%s!\"}", msg));

        return null;
    }

    Object success() {

        ctx.set(NEXT, true);

        return null;
    }

}
