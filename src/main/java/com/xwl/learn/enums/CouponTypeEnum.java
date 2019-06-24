package com.xwl.learn.enums;

/**
 * @Author: xiewanlin
 * @Date: 2019/6/24
 */
public enum CouponTypeEnum {

    CASH(1, "C", "返现券"),

    STOCK(2, "S", "送股券"),

    FEE_COMMISSION(3, "F", "免佣券"),

    MARKET(4, "M", "行情卡"),

    GIFT_CARD(5, "L", "礼品卡");

    private Integer couponType;
    private String prefix;
    private String desc;

    CouponTypeEnum(Integer couponType, String prefix, String desc) {
        this.couponType = couponType;
        this.prefix = prefix;
        this.desc = desc;
    }

    public Integer getCouponType() {
        return couponType;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 是否是支持的优惠券类型
     * @param couponType
     * @return
     */
    public static boolean isSupportCouponType(Integer couponType) {
        for (CouponTypeEnum couponTypeEnum : CouponTypeEnum.values()) {
            if (couponTypeEnum.getCouponType().equals(couponType)) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    /**
     * 根据优惠券类型获取优惠券前缀字母
     * @param couponType
     * @return
     */
    public static String getCouponPrefix(Integer couponType) {
        for (CouponTypeEnum couponTypeEnum : CouponTypeEnum.values()) {
            if (couponTypeEnum.getCouponType().equals(couponType)) {
                return couponTypeEnum.getPrefix();
            }
        }

        return null;
    }


    /**
     * 是不是行情卡
     * @param couponType
     * @return
     */
    public static boolean isMarketCard(Integer couponType) {
        return CouponTypeEnum.MARKET.getCouponType().equals(couponType);
    }

    /**
     * 是不是免佣券
     * @param couponType
     * @return
     */
    public static boolean isFeeCommission(Integer couponType) {
        return CouponTypeEnum.FEE_COMMISSION.getCouponType().equals(couponType);
    }

    /**
     * 是不是送股券
     * @param couponType
     * @return
     */
    public static boolean isStock(Integer couponType) {
        return CouponTypeEnum.STOCK.getCouponType().equals(couponType);
    }

    /**
     * 是不是礼品卡
     * @param couponType
     * @return
     */
    public static boolean isGiftCard(Integer couponType) {
        return CouponTypeEnum.GIFT_CARD.getCouponType().equals(couponType);
    }
}
