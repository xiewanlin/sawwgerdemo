package com.xwl.learn.controller.inner;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: xiewanlin
 * @Date: 2019/4/24
 */
@RestController
@Api(tags = "内部接口方法", description = "内部接口方法")
@RequestMapping(value = "/xwl-server/inner-api/")
public class InnerController {

    @ApiOperation(value = "输出字符串")
    @ApiImplicitParam(name = "Authorization", required = true, value = "Token", paramType = "header") //请求头
    @ApiResponses(value = {  //返回状态码
        @ApiResponse(code = 1110, message = "成功（看页面展示）")
    })
    @GetMapping("/inner/v1")  //接口
    public String inner(){
        return "inner";
    }
}
