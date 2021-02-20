package com.xwl.learn.aop.lock.server;

/**
 * distributed lock
 *
 * @author xiewanlin
 */
public interface DistributedLockService {

  /**
   * add lock with default expire and sleep time and retry times
   *
   * @param key key
   * @return lock success
   */
  boolean lock(String key);

  /**
   * add lock with default expire and sleep time
   *
   * @param key        key
   * @param retryTimes retry times
   * @return lock success
   */
  boolean lock(String key, int retryTimes);

  /**
   * add lock with default expire time
   *
   * @param key         key
   * @param retryTimes  retry times
   * @param sleepMillis sleep millis
   * @return lock success
   */
  boolean lock(String key, int retryTimes, long sleepMillis);

  /**
   * add lock  with default sleep and retry times
   *
   * @param key    key
   * @param expire expire
   * @return lock success
   */
  boolean lock(String key, long expire);

  /**
   * add lock  with default sleep time
   *
   * @param key        key
   * @param expire     expire
   * @param retryTimes retry times
   * @return lock success
   */
  boolean lock(String key, long expire, int retryTimes);

  /**
   * add lock
   *
   * @param key         key
   * @param expire      expire
   * @param retryTimes  retry times
   * @param sleepMillis sleep millis
   * @return lock success
   */
  boolean lock(String key, long expire, int retryTimes, long sleepMillis);

  /**
   * releaseLock
   *
   * @param key key
   * @return lock success
   */
  boolean releaseLock(String key);
}
