package com.xwl.learn.service;

import java.util.Map;
import java.util.Random;
import org.springframework.stereotype.Service;

/**
 * 抽奖实现
 * @Author: xiewanlin
 * @Date: 2021/3/1
 */
@Service
public class LotteryService {

  public String random(Map<String, Double> stockProbabilityMap) {
    if (stockProbabilityMap == null || stockProbabilityMap.size() == 0) {
      throw new RuntimeException();
    }

    Random random = new Random();
    double randomDouble = random.nextDouble();
    for (Map.Entry<String, Double> entry : stockProbabilityMap.entrySet()) {
      randomDouble -= entry.getValue();
      // 选中
      if (randomDouble <= 0) {
        return entry.getKey();
      }
    }
    return null;
  }

}
