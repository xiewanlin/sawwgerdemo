package com.xwl.learn.param;

import com.xwl.learn.aop.validator.annotations.DictCode;
import com.xwl.learn.enums.CurrencyUnit;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;

/**
 * @Author: xiewanlin
 * @Date: 2020/4/29
 */
public class TestParam {

  @ApiModelProperty(value = "支付价格单位",required = true,example = "1",position = 4)
  @NotNull
  @DictCode(CurrencyUnit.class)
  private Integer currencyUnit;
}
