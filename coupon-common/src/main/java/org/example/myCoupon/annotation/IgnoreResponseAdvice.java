package org.example.myCoupon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * 忽略统一响应
 */
@Target({ElementType.TYPE, ElementType.METHOD}) //类或方法
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreResponseAdvice {
}
