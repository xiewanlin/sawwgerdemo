package com.xwl.learn.aop.log;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiOperation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@Order(1)
public class AdminOperationLogAspect {

  private static final String SERVLET_PATH_TEMPLATE = "/([\\w\\-]+?)/([\\w\\-]+?)/([\\w\\-]+?)/";
  private static final Pattern SERVLET_PATTERN = Pattern.compile(SERVLET_PATH_TEMPLATE);

  @Pointcut("@annotation(com.xwl.learn.aop.log.AdminOperationLog)")
  private void adminLogPoint() {

  }

  @AfterReturning("adminLogPoint()")
  public void doAfterReturning(JoinPoint pjp) throws Throwable {
    Method method = ((MethodSignature) pjp.getSignature()).getMethod();
    ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
        .getRequestAttributes();
    AdminOperationLog adminOperationLog = method.getAnnotation(AdminOperationLog.class);
    if (adminOperationLog == null) {
      return;
    }
    String moduleCode = adminOperationLog.moduleCode();
    String actionCode = adminOperationLog.actionCode();
    if (StringUtils.isEmpty(moduleCode) || StringUtils.isEmpty(actionCode)) {
      Matcher servletPathMather = SERVLET_PATTERN.matcher(servletRequestAttributes.getRequest().getServletPath());
      if (servletPathMather.find()) {
        moduleCode = servletPathMather.group(1);
        actionCode = servletPathMather.group(3);
      }
    }
    ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
    String operation = "";
    if (apiOperation != null) {
      operation = apiOperation.value();
    }
    LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
    Object[] args = pjp.getArgs();
    if (args == null || parameterNames == null || args.length == 0 || parameterNames.length == 0) {
      System.out.println("");
    } else if (args.length != parameterNames.length) {
      Map<String, String> updateInfo = Maps.newHashMap();
      updateInfo.put("parameterNames", JSON.toJSONString(parameterNames));
      updateInfo.put("args", JSON.toJSONString(args));
      System.out.println(moduleCode+actionCode+JSON.toJSONString(updateInfo)+operation);
    } else {
      Map<String, String> updateInfo = Maps.newHashMap();
      Stream.iterate(0, i -> i + 1).limit(parameterNames.length)
          .forEach(i -> updateInfo.put(parameterNames[i], JSON.toJSONString(args[i])));
      System.out.println(moduleCode+actionCode+JSON.toJSONString(updateInfo)+operation);
    }
  }
}
