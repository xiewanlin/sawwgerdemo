package com.xwl.learn.service.impl;

import com.xwl.learn.aop.lock.DistributedLock;
import com.xwl.learn.service.TestServer;
import com.xwl.learn.vo.innerVo.UserVo;

/**
 * @Author: xiewanlin
 * @Date: 2019/11/8
 */
public class TestServerImpl implements TestServer {

  @Override
  @DistributedLock(value = "name", key = "#userVo.getUsername()")
  public void test(UserVo userVo){
  }

}
