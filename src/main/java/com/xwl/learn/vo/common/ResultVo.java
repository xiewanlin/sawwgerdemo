package com.xwl.learn.vo.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: xiewanlin
 * @Date: 2020/3/30
 */
@Data
public class ResultVo<T> {

  @ApiModelProperty(value = "响应码", dataType = "String")
  private Integer code;
  @ApiModelProperty(value = "响应内容", dataType = "String")
  private String msg;
  @ApiModelProperty(value = "响应体", dataType = "String")
  private T data;

  public ResultVo(Integer code, String msg, T data) {
    this.code = code;
    this.msg = msg;
    this.data = data;
  }

}
