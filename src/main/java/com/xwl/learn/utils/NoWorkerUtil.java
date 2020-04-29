package com.xwl.learn.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Random;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

/**
 * @author wunian
 * @description 编号id生成器
 * @apiNote 支持最多30台机器ip
 * 日期+17位（当天时间秒数）+5位ip定位+5位序列号
 */
public class NoWorkerUtil {
    private long workerId;
    private long sequence = 0L;
    private long workerIdBits = 5L;
    private long timeSecondBits = 17L;
    private long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private long sequenceBits = 5L;
    private long workerIdShift = sequenceBits;
    private long timeSecondShift = sequenceBits + workerIdBits;
    private long timeDateLeftShift = sequenceBits + workerIdBits + timeSecondBits;
    private long sequenceMask = -1L ^ (-1L << sequenceBits);
    private long lastTimestamp = -1L;

    public static final NoWorkerUtil noWorker = new NoWorkerUtil();

    private static Logger logger = LoggerFactory.getLogger(NoWorkerUtil.class);

    private NoWorkerUtil() {
        long workerId = this.getWorkerId();
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        this.workerId = workerId;
    }

    private synchronized long nextId() {
        TimeIdPO timeIdPO = timeGen();
        long timeId = timeIdPO.getTimeId();
        if (timeId < lastTimestamp) {
            logger.error(String.format("clock is moving backwards. Rejecting requests until %d.", lastTimestamp));
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timeId));
        }
        if (lastTimestamp == timeId) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                 timeIdPO = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timeIdPO.getTimeId();
        long id = (timeIdPO.getSecondOfDay() << timeSecondShift) | (workerId << workerIdShift) | sequence;
        DecimalFormat format = new DecimalFormat("00000000");
        return Long.valueOf(String.format("%d%s",timeIdPO.getToday(),format.format(id)));
    }

    private long getToday(){
        return Long.valueOf(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
    }
    private long getSecondOfDay(){
        return LocalTime.now().toSecondOfDay();
    }

    private TimeIdPO tilNextMillis(long lastTimestamp) {
        TimeIdPO timeIdPO = timeGen();
        long timestamp = timeIdPO.getTimeId();
        while (timestamp <= lastTimestamp) {
            timeIdPO = timeGen();
            timestamp = timeIdPO.getTimeId();
        }
        return timeIdPO;
    }

    private TimeIdPO timeGen() {
        long today = this.getToday();
        long secondOfDay = this.getSecondOfDay();
        long timeId = (today << timeDateLeftShift) | (secondOfDay << timeSecondShift);
        return new TimeIdPO(today,secondOfDay,timeId);
    }

    public static Long getNextNo(){
        return noWorker.nextId();
    }

    /**
     * 用来生成序列号最小时间粒度
     */
    @Data
    @AllArgsConstructor
    private class TimeIdPO{
        private long today;
        private long secondOfDay;
        private long timeId;
    }

    /**
     * 生成机器序号
     * 算法：根据ip从redis中分配机器序号
     * 支持最多30个机器ip
     * @return
     */
    private long  getWorkerId(){
        String cachePrefix = SpringContextHolder.getBean(Environment.class)
            .getProperty("spring.application.name", "public");
        RedissonClient redissonClient = SpringContextHolder.getBean(RedissonClient.class);
        String localIP = this.getLocalIP();
        RMap<String, Long> map = redissonClient.getMap(cachePrefix + ":work-id");
        if(map.containsKey(localIP)){
            return map.get(localIP);
        }else{
            long workerId = this.generateWorkerId(redissonClient, cachePrefix);
            Long result = map.putIfAbsent(localIP, workerId);
            return Objects.isNull(result)?workerId:result;
        }
    }

    /**
     * 生成唯一的机器序号
     * @param redissonClient
     * @param cachePrefix
     * @return
     */
    private  long generateWorkerId(RedissonClient redissonClient,String cachePrefix){
        RAtomicLong workId = redissonClient.getAtomicLong(cachePrefix + ":work-id:value");
        long curWorkId = workId.incrementAndGet();
        while(curWorkId >= maxWorkerId){
            if(workId.compareAndSet(curWorkId,1)){
                curWorkId = 1;
            }else{
                curWorkId = workId.incrementAndGet();
            }
        }
        return curWorkId;
    }

    /**
     * 提取本机IP
     * @return
     */
    private String getLocalIP(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.error("无法提取本地机器IP",e);
        }
        return String.valueOf(new Random().nextInt());
    }


}
