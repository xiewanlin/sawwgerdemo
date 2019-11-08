package com.xwl.learn.lock.server.impl;

import com.xwl.learn.lock.server.DistributedLockService;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;

/**
 * user redis base on redis
 *
 * @author chenhuahuan
 */
@Slf4j
@Service
public class RedisDistributedLockServiceImpl implements DistributedLockService {

  public static final long TIMEOUT_MILLIS = 30000;

  public static final int RETRY_TIMES = 5;

  public static final long SLEEP_MILLIS = 500;

  @Autowired
  private StringRedisTemplate redisTemplate;

  private ThreadLocal<String> lockFlag = new ThreadLocal<>();
  private static final String UNLOCK_LUA;

  static {
    StringBuilder sb = new StringBuilder();
    sb.append("if redis.call('GET', KEYS[1]) == ARGV[1]");
    sb.append("then ");
    sb.append("    return redis.call('del',KEYS[1]) ");
    sb.append("else ");
    sb.append("    return 0 ");
    sb.append("end ");
    UNLOCK_LUA = sb.toString();
  }

  @Override
  public boolean lock(String key) {
    return lock(key, TIMEOUT_MILLIS, RETRY_TIMES, SLEEP_MILLIS);
  }

  @Override
  public boolean lock(String key, int retryTimes) {
    return lock(key, TIMEOUT_MILLIS, retryTimes, SLEEP_MILLIS);
  }

  @Override
  public boolean lock(String key, int retryTimes, long sleepMillis) {

    return lock(key, TIMEOUT_MILLIS, retryTimes, sleepMillis);
  }

  @Override
  public boolean lock(String key, long expire) {
    return lock(key, expire, RETRY_TIMES, SLEEP_MILLIS);
  }

  @Override
  public boolean lock(String key, long expire, int retryTimes) {
    return lock(key, expire, retryTimes, SLEEP_MILLIS);
  }

  @Override
  public boolean lock(String key, long expire, int retryTimes, long sleepMillis) {
    boolean result = this.setRedis(key, expire);
    while ((!result) && retryTimes-- > 0) {
      try {
        Thread.sleep(sleepMillis);
        log.info("retry_to_lock key={},expire={},retryTimes={},sleepMillis={}", key, expire, retryTimes, sleepMillis);
      } catch (InterruptedException ed) {
        Thread.currentThread().interrupt();
        return false;
      }
      result = setRedis(key, expire);
    }
    return result;
  }

  private boolean setRedis(String key, long expire) {
    if (key == null || "".equals(key)) {
      return false;
    }
    try {
      return redisTemplate.execute((RedisCallback<Boolean>) connection -> {
        String uuid = UUID.randomUUID().toString();
        lockFlag.set(uuid);
        Boolean setResult = connection.set(key.getBytes(), uuid.getBytes(), Expiration.milliseconds(expire), SetOption.SET_IF_ABSENT);
        log.info("add_to_lock key={},value={},result={}", key, uuid, setResult);
        return setResult;
      });

    } catch (Exception e) {
      log.error("set redis  exception", e);
    }
    return false;
  }


  /**
   * 1.释放锁的时候，有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除 2.使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
   */
  @Override
  public boolean releaseLock(String key) {
    if (key == null || "".equals(key)) {
      return false;
    }
    try {
      return redisTemplate.execute((RedisCallback<Boolean>) connection -> {
        Long delResult = connection.eval(UNLOCK_LUA.getBytes(), ReturnType.INTEGER, 1, key.getBytes(), lockFlag.get().getBytes());
        log.info("release_lock key={},value={},result={}", key, lockFlag.get(), delResult);
        return delResult != null && delResult > 0;
      });
    } catch (Exception e) {
      log.error("release lock occur an exception", e);
    }
    return false;
  }

}
