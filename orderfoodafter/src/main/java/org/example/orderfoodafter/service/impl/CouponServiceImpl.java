package org.example.orderfoodafter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.Coupon;
import org.example.orderfoodafter.mapper.CouponMapper;
import org.example.orderfoodafter.service.CouponService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券Service实现类
 * 实现优惠券相关的业务逻辑处理功能
 * 
 * @author 李吉隆
 * @date 2025-11-29
 */
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {
/**
 * hasUserClaimedCoupon方法
 *
 * @author 李梦瑶
 */

    @Override
    public boolean hasUserClaimedCoupon(Long userId, Long couponId) {
        // 获取优惠券标题
        String title = null;
        switch (couponId.intValue()) {
            case 1:
                title = "新用户专享券";
                break;
            case 2:
                title = "每日签到券";
                break;
            case 3:
                title = "午餐专享券";
                break;
            case 4:
                title = "下午茶时光券";
                break;
            case 5:
                title = "晚餐优惠券";
                break;
            default:
                return false;
        }
        
        // 检查用户是否已领取过相同标题的优惠券
        QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("title", title);
        return this.count(queryWrapper) > 0;
    }
/**
 * getUserCoupons方法
 *
 * @author 李梦瑶
 */

    @Override
    public List<Coupon> getUserCoupons(Long userId) {
        QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("create_time");
        return this.list(queryWrapper);
    }
/**
 * getAvailableCoupons方法
 *
 * @author 李梦瑶
 */

    @Override
    public List<Coupon> getAvailableCoupons(Long userId, BigDecimal totalAmount, Long shopId) {
        QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("status", 1) // 未使用（1表示未使用）
                .le("min_amount", totalAmount) // 满足最低金额
                .ge("end_time", new java.util.Date()) // 未过期
                .orderByDesc("discount_value");
        return this.list(queryWrapper);
    }
/**
 * claimCoupon方法
 *
 * @author 李梦瑶
 */

    @Override
    public boolean claimCoupon(Long userId, Long couponId) {
        try {
            // 根据couponId匹配前端硬编码的优惠券模板
                /**
     * Coupon
     * 
     * @author 李吉隆
     */
            Coupon userCoupon = new Coupon();
            userCoupon.setUserId(userId);
            userCoupon.setShopId(0L); // 全店通用
            userCoupon.setStatus((byte) 1); // 未使用
            userCoupon.setCreateTime(new java.util.Date());
            userCoupon.setUpdateTime(new java.util.Date());
            
            // 设置开始时间和结束时间
            userCoupon.setStartTime(new java.util.Date());
            userCoupon.setEndTime(new java.util.Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)); // 30天后过期
            
            // 根据前端硬编码的couponId创建对应的优惠券
            switch (couponId.intValue()) {
                case 1:
                    userCoupon.setTitle("新用户专享券");
                    userCoupon.setDiscountType((byte) 1); // 满减类型
                    userCoupon.setDiscountValue(new java.math.BigDecimal("10"));
                    userCoupon.setMinAmount(new java.math.BigDecimal("50"));
                    break;
                case 2:
                    userCoupon.setTitle("每日签到券");
                    userCoupon.setDiscountType((byte) 1); // 满减类型
                    userCoupon.setDiscountValue(new java.math.BigDecimal("20"));
                    userCoupon.setMinAmount(new java.math.BigDecimal("100"));
                    break;
                case 3:
                    userCoupon.setTitle("午餐专享券");
                    userCoupon.setDiscountType((byte) 1); // 满减类型
                    userCoupon.setDiscountValue(new java.math.BigDecimal("5"));
                    userCoupon.setMinAmount(new java.math.BigDecimal("30"));
                    break;
                case 4:
                    userCoupon.setTitle("下午茶时光券");
                    userCoupon.setDiscountType((byte) 2); // 折扣类型
                    userCoupon.setDiscountValue(new java.math.BigDecimal("0.9")); // 9折
                    userCoupon.setMinAmount(new java.math.BigDecimal("25"));
                    break;
                case 5:
                    userCoupon.setTitle("晚餐优惠券");
                    userCoupon.setDiscountType((byte) 1); // 满减类型
                    userCoupon.setDiscountValue(new java.math.BigDecimal("15"));
                    userCoupon.setMinAmount(new java.math.BigDecimal("80"));
                    break;
                default:
                    return false; // 无效的优惠券ID
            }
            
            return this.save(userCoupon);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
/**
 * useCoupon方法
 *
 * @author 李梦瑶
 */

    @Override
    public boolean useCoupon(Long userId, Long couponId) {
        try {
            QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                    .eq("id", couponId)
                    .eq("status", 1); // 未使用

            Coupon coupon = this.getOne(queryWrapper);
            if (coupon == null) {
                return false;
            }

            coupon.setStatus((byte) 0); // 已使用
            coupon.setUpdateTime(new java.util.Date());

            return this.updateById(coupon);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
/**
 * getTodayClaimedCoupons方法
 *
 * @author 李梦瑶
 */

    @Override
    public List<Coupon> getTodayClaimedCoupons(Long userId) {
        QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .ge("create_time", java.sql.Date.valueOf(LocalDate.now()))
                .lt("create_time", java.sql.Date.valueOf(LocalDate.now().plusDays(1)));
        return this.list(queryWrapper);
    }
}