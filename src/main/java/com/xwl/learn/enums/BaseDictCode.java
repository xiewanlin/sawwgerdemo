package com.xwl.learn.enums;

import java.util.Arrays;

/**
 * 所有数据字典的接口
 * @author wunian
 */
public interface BaseDictCode {

  /**
   * 字典值，实际存储在数据库的值
   * @return Integer 数字
   */
  Integer getType();

  /**
   * 字典描述信息，前端页面展示的值
   * @return String 字符串
   */
  String getName();

  /**
   * 根据类型提取枚举
   * @param type
   * @return
   */
  static <T extends BaseDictCode> T getEnumByType(Class<T> dictCode, Integer type){
    T[] dictCodes = null;
    try {
      dictCodes = (T[])dictCode.getMethod("values").invoke(dictCode);
    } catch (Exception e) {
      return null;
    }
    return Arrays.stream(dictCodes).filter(x -> x.getType().equals(type))
        .findFirst().orElse(null);
  }

}
