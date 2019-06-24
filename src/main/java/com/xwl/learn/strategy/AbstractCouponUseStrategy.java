package com.xwl.learn.strategy;

import com.xwl.learn.params.Coupon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: xiewanlin
 * @Date: 2019/6/24
 */
public abstract class AbstractCouponUseStrategy implements ICouponUseStrategy{

    private static final Logger logger = LoggerFactory.getLogger(AbstractCouponUseStrategy.class);

    public final boolean useCoupon(Coupon coupon){

        /** 标记状态失败*/
        setCouponStatus(coupon);

        realUseCoupon(coupon);

        /** 优惠券使用后的操作*/
        afterUseCoupon();

        return Boolean.TRUE;
    }

    private final boolean setCouponStatus(Coupon coupon){

        return true;

    }

    abstract void realUseCoupon(Coupon coupon);

    private final void afterUseCoupon(){
    }


}
