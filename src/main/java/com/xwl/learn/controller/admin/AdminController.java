package com.xwl.learn.controller.admin;

import com.xwl.learn.aop.log.AdminOperationLog;
import com.xwl.learn.vo.common.ResultVo;
import com.xwl.learn.vo.innerVo.LoginDto;
import com.xwl.learn.vo.innerVo.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xiewanlin
 * @Date: 2019/4/24
 */
@RestController
@RequestMapping(value = "/xwl-server/admin-api/")
@Api(tags = "admin管理中心", description = "admin管理中心")
public class AdminController {

    @ApiOperation(value = "登录管理中心")
    @PostMapping(value = "/login/v1", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @AdminOperationLog
    public ResultVo login(@RequestBody @Valid UserVo userVo) {
        if ("xwl".equals(userVo.getUsername()) && "123456".equals(userVo.getPassword())) {
            return new ResultVo(0, "调用成功", null);
        }
        return new ResultVo(700301, "用户名或密码错误", null);
    }

    @ApiOperation(value = "获取用户登录列表")
    @GetMapping(value = "/getLoginTimes/v1", produces = MediaType.APPLICATION_JSON_VALUE)
    @AdminOperationLog
    public List<LoginDto> getLoginTimes(@RequestParam String username) {
        List<LoginDto> retList = new ArrayList<>();
        if ("xwl".equals(username)) {
            LoginDto dto = new LoginDto();
            dto.setLoginLocat("shenzhen");
            dto.setLoginTime("2019-04-22 19:00:00");
            retList.add(dto);
        }
        return retList;
    }
}
