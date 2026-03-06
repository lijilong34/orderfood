package org.example.orderfoodafter.service;

import org.example.orderfoodafter.entity.Coupon;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CouponService extends IService<Coupon> {

    /**
     * 检查用户是否已经领取过该优惠券
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return true表示已领取，false表示未领取
     */
    boolean hasUserClaimedCoupon(Long userId, Long couponId);

    /**
     * 获取用户的所有优惠券（已领取的）
     * @param userId 用户ID
     * @return 优惠券列表
     */
    List<Coupon> getUserCoupons(Long userId);

    /**
     * 获取可用的优惠券（用户可以使用的）
     * @param userId 用户ID
     * @param totalAmount 总金额（用于筛选满足条件的优惠券）
     * @param shopId 店铺ID
     * @return 可用优惠券列表
     */
    List<Coupon> getAvailableCoupons(Long userId, java.math.BigDecimal totalAmount, Long shopId);

    /**
     * 领取优惠券
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 是否领取成功
     */
    boolean claimCoupon(Long userId, Long couponId);

    /**
     * 使用优惠券
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 是否使用成功
     */
    boolean useCoupon(Long userId, Long couponId);

    /**
     * 获取今日已领取的优惠券
     * @param userId 用户ID
     * @return 今日已领取的优惠券列表
     */
    List<Coupon> getTodayClaimedCoupons(Long userId);
}
