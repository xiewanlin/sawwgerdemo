package com.xwl.learn.vo.innerVo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @Author: xiewanlin
 * @Date: 2019/4/24
 */
@Data
@ApiModel(description = "登录信息")
public class UserVo {

    @ApiModelProperty(value = "账号", example = "xwl", required = true)
    @NotBlank(message = "username is not null")
    @Size(max = 24, message = "username is over 24")
    private String username;

    @ApiModelProperty(value = "密码", example = "123456", required = true)
    @NotBlank(message = "password is not null")
    @Size(max = 16, message = "password is over 16")
    private String password;
}
