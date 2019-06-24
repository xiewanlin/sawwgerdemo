package com.xwl.learn.strategy;

import com.xwl.learn.enums.CouponTypeEnum;
import com.xwl.learn.params.Coupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xiewanlin
 * @Date: 2019/6/24
 */
@Service
public class CouponUseFactory {

    private static Map<Integer, ICouponUseStrategy> couponUseStrategyMap;

    @Autowired
    private CashCouponUseStrategy cashCouponUseStrategy;

    @Autowired
    private StockCouponUseStrategy stockCouponUseStrategy;

    @Autowired
    private FeeCommissionCouponUseStrategy feeCommissionCouponUseStrategy;

    @Autowired
    private MarketCouponUseStrategy marketCouponUseStrategy;

    @Autowired
    private GiftCardCouponUseStratgy giftCardCouponUseStratgy;


    private void fillConditionTypeStrategyMap(){
        couponUseStrategyMap = new HashMap<>();
        couponUseStrategyMap.put(CouponTypeEnum.CASH.getCouponType(), cashCouponUseStrategy);
        couponUseStrategyMap.put(CouponTypeEnum.STOCK.getCouponType(), stockCouponUseStrategy);
        couponUseStrategyMap.put(CouponTypeEnum.FEE_COMMISSION.getCouponType(), feeCommissionCouponUseStrategy);
        couponUseStrategyMap.put(CouponTypeEnum.MARKET.getCouponType(), marketCouponUseStrategy);
        couponUseStrategyMap.put(CouponTypeEnum.GIFT_CARD.getCouponType(), giftCardCouponUseStratgy);
    }


    public boolean useCoupon(Coupon coupon){

        if (couponUseStrategyMap == null){
            this.fillConditionTypeStrategyMap();
        }

        ICouponUseStrategy couponUseStrategy = couponUseStrategyMap.get(coupon.getType());

        return couponUseStrategy.useCoupon(coupon);

    }
}
