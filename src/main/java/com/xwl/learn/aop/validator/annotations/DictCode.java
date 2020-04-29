package com.xwl.learn.aop.validator.annotations;

import com.xwl.learn.aop.validator.DictCodeEnumValidator;
import com.xwl.learn.enums.BaseDictCode;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @author wunian
 * @version 1.0
 * @description 字典项校验器
 * @date 2019/12/30 11:47
 **/
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
		validatedBy = {DictCodeEnumValidator.class}
)
@Documented
public @interface DictCode {
	Class<? extends BaseDictCode> value();

	String message() default "数据字典校验失败";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
