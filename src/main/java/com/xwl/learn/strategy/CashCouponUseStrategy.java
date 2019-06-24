package com.xwl.learn.strategy;

import com.xwl.learn.params.Coupon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: xiewanlin
 * @Date: 2019/6/24
 */
@Service
@Slf4j
public class CashCouponUseStrategy extends AbstractCouponUseStrategy{

    @Override
    void realUseCoupon(Coupon coupon) {
    }

}
