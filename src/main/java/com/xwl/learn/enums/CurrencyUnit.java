package com.xwl.learn.enums;

/**
 * @author xiewanlin
 * @version 1.0
 * @description 货币单位枚举
 * @date 2019/12/27 14:58
 **/
public enum CurrencyUnit implements BaseDictCode {

  CNH(0, "CNY"),
  USD(1, "USD"),
  HKD(2, "HKD");
  private Integer type;
  private String name;

  CurrencyUnit(Integer type, String name) {
    this.type = type;
    this.name = name;
  }

  @Override
  public Integer getType() {
    return type;
  }

  @Override
  public String getName() {
    return name;
  }


}
