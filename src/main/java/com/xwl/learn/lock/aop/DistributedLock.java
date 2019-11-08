package com.xwl.learn.lock.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DistributedLock {

  /**
   * namespace 命名域
   */
  String value() default "";

  /**
   * key 支持从参数中获取
   */
  String key() default "";

  long keepMills() default 30000;

  long sleepMills() default 200;

  int retryTimes() default 5;

  LockFailAction action() default LockFailAction.CONTINUE;

  enum LockFailAction {GIVE_UP, CONTINUE}
}
