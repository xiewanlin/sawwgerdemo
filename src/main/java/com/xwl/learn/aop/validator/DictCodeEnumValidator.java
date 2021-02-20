package com.xwl.learn.aop.validator;


import com.xwl.learn.aop.validator.annotations.DictCode;
import com.xwl.learn.enums.BaseDictCode;
import java.lang.reflect.Method;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xiewanlin
 * @version 1.0
 * @description 字典器校验器
 * @date 2019/12/30 11:47
 **/
public class DictCodeEnumValidator implements ConstraintValidator<DictCode, Integer> {
	private static final Logger LOGGER = LoggerFactory.getLogger(DictCodeEnumValidator.class);
	private Class<? extends BaseDictCode> dictCodeClazz;
	private String message;

	public DictCodeEnumValidator() {
	}

	@Override
	public void initialize(DictCode constraintAnnotation) {
		this.setDictCode(constraintAnnotation.value());
		this.setMessage(constraintAnnotation.message());
	}

	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		} else {
			try {
				Method valuesMethod = this.dictCodeClazz.getMethod("values");
				BaseDictCode[] var4 = (BaseDictCode[])((BaseDictCode[])valuesMethod.invoke(this.dictCodeClazz));
				int var5 = var4.length;

				for(int var6 = 0; var6 < var5; ++var6) {
					BaseDictCode dictCode = var4[var6];
					if (value.equals(dictCode.getType())) {
						return true;
					}
				}

				return false;
			} catch (Exception var8) {
				LOGGER.error("数据字典校验类发生异常", var8);
				return true;
			}
		}
	}

	public Class<? extends BaseDictCode> getDictCode() {
		return this.dictCodeClazz;
	}

	public void setDictCode(Class<? extends BaseDictCode> dictCode) {
		this.dictCodeClazz = dictCode;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
