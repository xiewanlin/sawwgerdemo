package com.xwl.learn.strategy;

import com.xwl.learn.params.Coupon;

/**
 * @Author: xiewanlin
 * @Date: 2019/6/24
 */
public interface ICouponUseStrategy {

    boolean useCoupon(Coupon coupon);
}
