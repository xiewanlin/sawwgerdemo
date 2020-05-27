package com.xwl.learn.aop.idempotent;

/**
 * @author xiewanlin
 * @version 1.0
 * @className BaseIdempotent
 * @description 从其他存储层提取幂等数据
 * @date 2019/7/30 11:45
 **/
public interface BaseIdempotent<T> {

	T data(Object[] args);

}
