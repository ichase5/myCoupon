package org.example.myCoupon.advice;

import org.example.myCoupon.exception.CouponException;
import org.example.myCoupon.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/*
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {

    /*
     对 CouponException 进行统一处理
    */
    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handlerCouponException(HttpServletRequest req, CouponException ex) {
        CommonResponse<String> response = new CommonResponse<>(
                -1, "business error");
        response.setData(ex.getMessage());
        return response;
    }
}
