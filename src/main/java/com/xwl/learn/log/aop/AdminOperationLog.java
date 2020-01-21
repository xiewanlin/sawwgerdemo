package com.xwl.learn.log.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AdminOperationLog {

  /**
   * 服务代码
   */
  String moduleCode() default "";

  /**
   * 接口功能
   */
  String actionCode() default "";
}
