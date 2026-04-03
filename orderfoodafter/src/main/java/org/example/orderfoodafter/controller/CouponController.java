// 定义当前类的包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入MyBatis-Plus的查询包装器类，用于构建查询条件
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入MyBatis-Plus的更新包装器类，用于构建更新条件
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
// 导入PageHelper分页工具类，用于分页查询
import com.github.pagehelper.PageHelper;
// 导入PageInfo分页信息类，用于存储分页结果
import com.github.pagehelper.PageInfo;
// 导入统一响应类R，用于封装API响应
import org.example.orderfoodafter.common.R;
// 导入优惠券实体类
import org.example.orderfoodafter.entity.Coupon;
// 导入优惠券服务接口
import org.example.orderfoodafter.service.CouponService;
// 导入Spring的自动装配注解，用于依赖注入
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring MVC的注解集合，包括@GetMapping、@PostMapping等
import org.springframework.web.bind.annotation.*;

// 导入BigDecimal类，用于精确的十进制计算
import java.math.BigDecimal;
// 导入Date类，用于表示日期时间
import java.util.Date;
// 导入List集合接口，用于列表操作
import java.util.List;
// 导入Map接口，用于映射操作
import java.util.Map;

/**
 * 优惠券控制器
 * 负责管理系统的优惠券功能，包括优惠券的创建、领取、使用、查询等操作
 *
 * @author 李梦瑶
 * @date 2025-11-25
 */
// 使用@RequestMapping注解设置该控制器的所有接口基础路径为/coupon
@RequestMapping("/coupon")
// 使用@RestController注解标记该类为REST控制器，所有方法返回JSON数据
@RestController
// 定义CouponController类，继承BaseController基类，泛型为Coupon和CouponService
/**
 * CouponController类
 *
 * @author 李梦瑶
 * @date 2026-03-18
 */

public class CouponController extends BaseController<Coupon, CouponService> {
    // 使用@Autowired注解自动注入CouponService服务实例
    @Autowired
    // 声明优惠券服务接口的私有成员变量
    private CouponService couponService;

    // 定义带参构造函数，接收CouponService服务实例参数
/**
 * CouponController方法
 *
 * @author 李梦瑶
 */
    public CouponController(CouponService couponService) {  // 优惠券服务实例
        // 调用父类构造函数，传入服务实例
        super(couponService);
        // 将传入的服务实例赋值给成员变量
        this.couponService = couponService;
    }

    /**
     * 获取可用的优惠券列表
     * 作者:李梦瑶
     * @return 可用优惠券列表
     */
    // 使用@GetMapping注解映射GET请求到/coupon/available路径
    @GetMapping("/available")
    // 定义获取可用优惠券列表的方法，返回类型为R
        /**
     * 获取 getAvailableCoupons
     * 
     * @return getAvailableCoupons
     * @author 李梦瑶
     */
    public R getAvailableCoupons() {
        // 创建查询包装器对象
        QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
        // 添加用户ID为null的条件（未领取的优惠券）
        queryWrapper.isNull("user_id");
        // 添加结束时间大于当前时间的条件（未过期）
            /**
     * gt
     * 
     * @author 李梦瑶
     */
        queryWrapper.gt("end_time",new Date());
        // 调用服务层方法查询优惠券列表
        List<Coupon> couponList = couponService.list(queryWrapper);
        // 返回包含优惠券列表的响应
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("couponList", couponList);
    }

    /**
     * 获取可领取的优惠券模板列表
     */
    // 使用@GetMapping注解映射GET请求到/coupon/templates路径
    @GetMapping("/templates")
    // 定义获取优惠券模板列表的方法，返回类型为R
        /**
     * 获取 getCouponTemplates
     * 
     * @return getCouponTemplates
     * @author 李梦瑶
     */
    public R getCouponTemplates() {
        // 使用try-catch捕获可能的异常
        try {
            // 获取优惠券模板（user_id为null的记录作为模板）
            // 创建查询包装器对象
            QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
            // 添加用户ID为null的条件（优惠券模板）
            queryWrapper.isNull("user_id"); // 优惠券模板
            // 添加开始时间小于等于当前时间的条件（已开始）
                /**
     * le
     * 
     * @author 李梦瑶
     */
            queryWrapper.le("start_time", new Date()); // 已开始
            // 添加结束时间大于等于当前时间的条件（未过期）
                /**
     * ge
     * 
     * @author 李梦瑶
     */
            queryWrapper.ge("end_time", new Date()); // 未过期
            // 添加状态等于1的条件（可用状态）
            queryWrapper.eq("status", 1); // 可用状态

            // 按最低金额升序排列
            queryWrapper.orderByAsc("min_amount");
            // 按优惠值降序排列
            queryWrapper.orderByDesc("discount_value");

            // 调用服务层方法查询优惠券模板列表
            List<Coupon> couponTemplates = couponService.list(queryWrapper);
            // 返回包含优惠券模板列表的响应
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("couponList", couponTemplates);
        } catch (Exception e) {  // 捕获异常
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("获取优惠券模板失败: " + e.getMessage());
        }
    }

    /**
     * 使用优惠券
     * 作者:李梦瑶
     * @param token 用户令牌
     * @param couponId 优惠券ID
     * @return 使用结果
     */
    // 使用@PostMapping注解映射POST请求到/coupon/use路径
    @PostMapping("/use")
    // 定义使用优惠券的方法，返回类型为R，接收请求头token和请求参数couponId
        /**
     * useCoupon
     * 
     * @author 李梦瑶
     */
    public R useCoupon(@RequestHeader("Authorization") String token, @RequestParam Long couponId) {  // 用户令牌和优惠券ID
        // 使用try-catch捕获可能的异常
        try {
            // 去除token的前缀"Bearer "
            token = token.substring(7);
            // 从token中获取用户信息
            String subject = jwtUtils.getSubject(token);
            // 将用户信息转换为长整型
            Long userId = Long.parseLong(subject);

            // 根据优惠券ID查询优惠券信息
            Coupon coupon = couponService.getById(couponId);
            // 判断优惠券是否存在
            if (coupon == null) {
                // 抛出运行时异常，提示优惠券不存在
                    /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
                throw new RuntimeException("优惠券不存在");
            }
            // 判断优惠券是否属于当前用户
            if (!coupon.getUserId().equals(userId)) {
                // 抛出运行时异常，提示无权使用此优惠券
                    /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
                throw new RuntimeException("无权使用此优惠券");
            }
            // 判断优惠券状态是否可用
            if (coupon.getStatus() != 1) {
                // 抛出运行时异常，提示优惠券不可用
                    /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
                throw new RuntimeException("优惠券不可用");
            }

            // 设置优惠券状态为已使用
            coupon.setStatus((byte) 0);
            // 更新优惠券信息
            boolean flag = couponService.updateById(coupon);
            // 判断更新是否成功
            if (flag) {
                // 返回成功响应
                    /**
     * R
     * 
     * @author 李梦瑶
     */
                return new R().addData("status", "优惠券使用成功");
            }
            // 抛出运行时异常，提示优惠券使用失败
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("优惠券使用失败");
        } catch (Exception e) {  // 捕获异常
            // 抛出运行时异常，包含错误信息
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("使用优惠券失败: " + e.getMessage());
        }
    }

    /**
     * 领取优惠券
     */
    // 使用@PostMapping注解映射POST请求到/coupon/claim路径
    @PostMapping("/claim")
    // 定义领取优惠券的方法，返回类型为R，接收请求头token和请求体couponData
        /**
     * claimCoupon
     * 
     * @author 李梦瑶
     */
    public R claimCoupon(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> couponData) {  // 用户令牌和优惠券数据
        // 使用try-catch捕获可能的异常
        try {
            // 去除token的前缀"Bearer "
            token = token.substring(7);
            // 从token中获取用户信息
            String subject = jwtUtils.getSubject(token);
            // 将用户信息转换为长整型
            Long userId = Long.parseLong(subject);

            // 获取优惠券ID
            Long couponId = Long.valueOf(couponData.get("couponId").toString());
            // 创建更新包装器对象
            UpdateWrapper<Coupon> updateWrapper = new UpdateWrapper<>();
            // 设置更新条件：ID等于优惠券ID，并设置用户ID
            updateWrapper.eq("id", couponId).set("user_id", userId);

            // 领取优惠券
            // 调用服务层方法更新优惠券信息
            boolean success = couponService.update(null,updateWrapper);
            // 判断更新是否成功
            if (success) {
                // 返回成功响应
                    /**
     * R
     * 
     * @author 李梦瑶
     */
                return new R().addData("status", "优惠券领取成功");
            } else {
                // 抛出运行时异常，提示优惠券领取失败
                    /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
                throw new RuntimeException("优惠券领取失败");
            }
        } catch (Exception e) {  // 捕获异常
            // 抛出运行时异常，包含错误信息
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("领取优惠券时发生错误: " + e.getMessage());
        }
    }

    /**
     * 获取用户已领取的优惠券列表
     */
    /**
     * 获取用户已领取的优惠券列表
     * 作者:李梦瑶
     * @param token 用户令牌
     * @return 用户优惠券列表
     */
    // 使用@GetMapping注解映射GET请求到/coupon/couponlist路径
    @GetMapping("/couponlist")
    // 定义获取用户优惠券列表的方法，返回类型为R，接收请求头token
        /**
     * 获取 getUserCoupons
     * 
     * @return getUserCoupons
     * @author 李梦瑶
     */
    public R getUserCoupons(@RequestHeader("Authorization") String token) {  // 用户令牌
        // 使用try-catch捕获可能的异常
        try {
            // 去除token的前缀"Bearer "
            token = token.substring(7);
            // 从token中获取用户信息
            String subject = jwtUtils.getSubject(token);
            // 将用户信息转换为长整型
            Long userId = Long.parseLong(subject);
            // 创建查询包装器对象
            QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
            // 添加用户ID等于条件
            queryWrapper.eq("user_id",userId);
            // 添加结束时间大于当前时间的条件（未过期）
                /**
     * gt
     * 
     * @author 李梦瑶
     */
            queryWrapper.gt("end_time",new Date());
            // 调用服务层方法查询优惠券列表
            List<Coupon> couponList = couponService.list(queryWrapper);

            // 返回包含优惠券列表的响应
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("couponList", couponList);
        } catch (Exception e) {  // 捕获异常
            // 抛出运行时异常，包含错误信息
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("获取用户优惠券列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取今日已领取的优惠券
     */
    // 使用@GetMapping注解映射GET请求到/coupon/today-claimed路径
    @GetMapping("/today-claimed")
    // 定义获取今日已领取优惠券的方法，返回类型为R，接收请求头token
        /**
     * 获取 getTodayClaimedCoupons
     * 
     * @return getTodayClaimedCoupons
     * @author 李梦瑶
     */
    public R getTodayClaimedCoupons(@RequestHeader("Authorization") String token) {  // 用户令牌
        // 使用try-catch捕获可能的异常
        try {
            // 去除token的前缀"Bearer "
            token = token.substring(7);
            // 从token中获取用户信息
            String subject = jwtUtils.getSubject(token);
            // 将用户信息转换为长整型
            Long userId = Long.parseLong(subject);

            // 调用服务层方法获取今日已领取的优惠券列表
            List<Coupon> couponList = couponService.getTodayClaimedCoupons(userId);

            // 返回包含优惠券列表的响应
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("couponList", couponList);
        } catch (Exception e) {  // 捕获异常
            // 抛出运行时异常，包含错误信息
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("获取今日已领取优惠券失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户所有已领取的优惠券（包括所有时间的）
     */
    // 使用@GetMapping注解映射GET请求到/coupon/all-claimed路径
    @GetMapping("/all-claimed")
    // 定义获取所有已领取优惠券的方法，返回类型为R，接收请求头token
        /**
     * 获取 getAllClaimedCoupons
     * 
     * @return getAllClaimedCoupons
     * @author 李梦瑶
     */
    public R getAllClaimedCoupons(@RequestHeader("Authorization") String token) {  // 用户令牌
        // 使用try-catch捕获可能的异常
        try {
            // 去除token的前缀"Bearer "
            token = token.substring(7);
            // 从token中获取用户信息
            String subject = jwtUtils.getSubject(token);
            // 将用户信息转换为长整型
            Long userId = Long.parseLong(subject);

            // 调用服务层方法获取用户所有已领取的优惠券列表
            List<Coupon> couponList = couponService.getUserCoupons(userId);

            // 返回包含优惠券列表的响应
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("couponList", couponList);
        } catch (Exception e) {  // 捕获异常
            // 抛出运行时异常，包含错误信息
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("获取所有已领取优惠券失败: " + e.getMessage());
        }
    }

    // ==================== 管理员接口 ====================

    /**
     * 管理员获取优惠券模板列表（分页）
     */
    // 使用@PostMapping注解映射POST请求到/coupon/admin/list路径
    @PostMapping("/admin/list")
    // 定义管理员获取优惠券列表的方法，返回类型为R，接收查询参数映射
        /**
     * 获取 getAdminCouponList
     * 
     * @return getAdminCouponList
     * @author 李梦瑶
     */
    public R getAdminCouponList(@RequestBody Map<String, Object> params) {  // 查询参数映射
        // 使用try-catch捕获可能的异常
        try {
            // 获取页码，默认为1
            int page = params.containsKey("page") ? Integer.parseInt(params.get("page").toString()) : 1;
            // 获取每页大小，默认为10
            int pageSize = params.containsKey("pageSize") ? Integer.parseInt(params.get("pageSize").toString()) : 10;

            // 启动分页
            PageHelper.startPage(page, pageSize);

            // 创建查询包装器对象
            QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
//            queryWrapper.isNull("user_id"); // 只获取模板
              // 按创建时间降序排列
              queryWrapper.orderByDesc("create_time");
            // 搜索关键词
            // 判断是否包含搜索关键词
            if (params.containsKey("keyword") && params.get("keyword") != null) {
                // 获取关键词并去除首尾空格
                String keyword = params.get("keyword").toString().trim();
                // 判断关键词是否不为空
                if (!keyword.isEmpty()) {
                    // 添加标题模糊查询条件
                    queryWrapper.like("title", keyword);
                }
            }

            // 优惠类型筛选
            // 判断是否包含优惠类型
            if (params.containsKey("discountType") && params.get("discountType") != null) {
                // 获取优惠类型字符串
                String discountTypeStr = params.get("discountType").toString();
                // 判断优惠类型字符串是否不为空
                if (!discountTypeStr.isEmpty()) {
                    // 添加优惠类型等于条件
                    queryWrapper.eq("discount_type", Integer.parseInt(discountTypeStr));
                }
            }

            // 状态筛选
            // 判断是否包含状态
            if (params.containsKey("status") && params.get("status") != null) {
                // 获取状态字符串
                String statusStr = params.get("status").toString();
                // 判断状态字符串是否不为空
                if (!statusStr.isEmpty()) {
                    // 添加状态等于条件
                    queryWrapper.eq("status", Integer.parseInt(statusStr));
                }
            }

            // 按创建时间降序排列
            queryWrapper.orderByDesc("create_time");

            // 调用服务层方法查询优惠券列表
            List<Coupon> couponList = couponService.list(queryWrapper);
            // 创建分页信息对象
            PageInfo<Coupon> pageInfo = new PageInfo<>(couponList);

            // 返回包含分页信息的响应
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("pageInfo", pageInfo);
        } catch (Exception e) {  // 捕获异常
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("获取优惠券列表失败: " + e.getMessage());
        }
    }

    /**
     * 管理员创建优惠券模板
     */
    // 使用@PostMapping注解映射POST请求到/coupon/admin/create路径
    @PostMapping("/admin/create")
    // 定义管理员创建优惠券模板的方法，返回类型为R，接收优惠券参数映射
        /**
     * createCouponTemplate
     * 
     * @author 李梦瑶
     */
    public R createCouponTemplate(@RequestBody Map<String, Object> params) {  // 优惠券参数映射
        // 使用try-catch捕获可能的异常
        try {
            // 创建优惠券对象
                /**
     * Coupon
     * 
     * @author 李梦瑶
     */
            Coupon coupon = new Coupon();

            // 设置优惠券标题
            coupon.setTitle(params.get("title").toString());

            // 优惠类型: 1-满减 2-折扣
            // 设置优惠类型
            coupon.setDiscountType(Byte.parseByte(params.get("discountType").toString()));

            // 优惠值
            // 判断是否包含优惠值
            if (params.get("discountValue") != null) {
                // 设置优惠值
                    /**
     * 设置 setDiscountValue
     * 
     * @param setDiscountValue setDiscountValue
     * @author 李梦瑶
     */
                coupon.setDiscountValue(new BigDecimal(params.get("discountValue").toString()));
            }

            // 最低使用金额
            // 判断是否包含最低使用金额
            if (params.get("minAmount") != null) {
                // 设置最低使用金额
                    /**
     * 设置 setMinAmount
     * 
     * @param setMinAmount setMinAmount
     * @author 李梦瑶
     */
                coupon.setMinAmount(new BigDecimal(params.get("minAmount").toString()));
            } else {
                // 设置最低使用金额为0
                coupon.setMinAmount(BigDecimal.ZERO);
            }

            // 店铺ID (0表示全店通用)
            // 判断是否包含店铺ID
            if (params.get("shopId") != null) {
                // 设置店铺ID
                coupon.setShopId(Long.parseLong(params.get("shopId").toString()));
            } else {
                // 设置店铺ID为0（全店通用）
                coupon.setShopId(0L);
            }

            // 生效时间
            // 判断是否包含生效时间
            if (params.get("startTime") != null) {
                // 设置生效时间
                    /**
     * 设置 setStartTime
     * 
     * @param setStartTime setStartTime
     * @author 李梦瑶
     */
                coupon.setStartTime(new Date(Long.parseLong(params.get("startTime").toString())));
            }

            // 过期时间
            // 判断是否包含过期时间
            if (params.get("endTime") != null) {
                // 设置过期时间
                    /**
     * 设置 setEndTime
     * 
     * @param setEndTime setEndTime
     * @author 李梦瑶
     */
                coupon.setEndTime(new Date(Long.parseLong(params.get("endTime").toString())));
            }

            // 状态: 1-可用
            // 设置优惠券状态，默认为1
            coupon.setStatus(Byte.parseByte(params.getOrDefault("status", "1").toString()));

            // 创建时间
            // 设置创建时间为当前时间
                /**
     * 设置 setCreateTime
     * 
     * @param setCreateTime setCreateTime
     * @author 李梦瑶
     */
            coupon.setCreateTime(new Date());
            // 设置更新时间为当前时间
                /**
     * 设置 setUpdateTime
     * 
     * @param setUpdateTime setUpdateTime
     * @author 李梦瑶
     */
            coupon.setUpdateTime(new Date());

            // user_id为null表示这是模板
            // 调用服务层方法保存优惠券
            boolean result = couponService.save(coupon);

            // 判断保存是否成功
            if (result) {
                // 返回成功响应，包含优惠券对象和成功消息
                    /**
     * R
     * 
     * @author 李梦瑶
     */
                return new R().addData("coupon", coupon).addData("message", "优惠券创建成功");
            } else {
                // 返回错误响应，提示优惠券创建失败
                return R.error("优惠券创建失败");
            }
        } catch (Exception e) {  // 捕获异常
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("优惠券创建失败: " + e.getMessage());
        }
    }

    /**
     * 管理员更新优惠券模板
     */
    // 使用@PutMapping注解映射PUT请求到/coupon/admin/update路径
    @PutMapping("/admin/update")
    // 定义管理员更新优惠券模板的方法，返回类型为R，接收优惠券参数映射
        /**
     * updateCouponTemplate
     * 
     * @author 李梦瑶
     */
    public R updateCouponTemplate(@RequestBody Map<String, Object> params) {  // 优惠券参数映射
        // 使用try-catch捕获可能的异常
        try {
            // 获取优惠券ID
            Long id = Long.parseLong(params.get("id").toString());

            // 根据ID查询优惠券信息
            Coupon coupon = couponService.getById(id);
            // 判断优惠券是否存在
            if (coupon == null) {
                // 返回错误响应，提示优惠券不存在
                return R.error("优惠券不存在");
            }

            // 更新字段
            // 判断是否包含标题
            if (params.containsKey("title")) {
                // 设置优惠券标题
                coupon.setTitle(params.get("title").toString());
            }

            // 判断是否包含优惠类型
            if (params.containsKey("discountType")) {
                // 设置优惠类型
                coupon.setDiscountType(Byte.parseByte(params.get("discountType").toString()));
            }

            // 判断是否包含优惠值
            if (params.containsKey("discountValue")) {
                // 设置优惠值
                    /**
     * 设置 setDiscountValue
     * 
     * @param setDiscountValue setDiscountValue
     * @author 李梦瑶
     */
                coupon.setDiscountValue(new BigDecimal(params.get("discountValue").toString()));
            }

            // 判断是否包含最低使用金额
            if (params.containsKey("minAmount")) {
                // 设置最低使用金额
                    /**
     * 设置 setMinAmount
     * 
     * @param setMinAmount setMinAmount
     * @author 李梦瑶
     */
                coupon.setMinAmount(new BigDecimal(params.get("minAmount").toString()));
            }

            // 判断是否包含店铺ID
            if (params.containsKey("shopId")) {
                // 设置店铺ID
                coupon.setShopId(Long.parseLong(params.get("shopId").toString()));
            }

            // 判断是否包含生效时间
            if (params.containsKey("startTime")) {
                // 设置生效时间
                    /**
     * 设置 setStartTime
     * 
     * @param setStartTime setStartTime
     * @author 李梦瑶
     */
                coupon.setStartTime(new Date(Long.parseLong(params.get("startTime").toString())));
            }

            // 判断是否包含过期时间
            if (params.containsKey("endTime")) {
                // 设置过期时间
                    /**
     * 设置 setEndTime
     * 
     * @param setEndTime setEndTime
     * @author 李梦瑶
     */
                coupon.setEndTime(new Date(Long.parseLong(params.get("endTime").toString())));
            }

            // 判断是否包含状态
            if (params.containsKey("status")) {
                // 设置优惠券状态
                coupon.setStatus(Byte.parseByte(params.get("status").toString()));
            }

            // 设置更新时间为当前时间
                /**
     * 设置 setUpdateTime
     * 
     * @param setUpdateTime setUpdateTime
     * @author 李梦瑶
     */
            coupon.setUpdateTime(new Date());

            // 更新优惠券信息
            boolean result = couponService.updateById(coupon);

            // 判断更新是否成功
            if (result) {
                // 返回成功响应，包含优惠券对象和成功消息
                    /**
     * R
     * 
     * @author 李梦瑶
     */
                return new R().addData("coupon", coupon).addData("message", "优惠券更新成功");
            } else {
                // 返回错误响应，提示优惠券更新失败
                return R.error("优惠券更新失败");
            }
        } catch (Exception e) {  // 捕获异常
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("优惠券更新失败: " + e.getMessage());
        }
    }

    /**
     * 管理员删除优惠券模板
     */
    // 使用@DeleteMapping注解映射DELETE请求到/coupon/admin/delete/{id}路径
    @DeleteMapping("/admin/delete/{id}")
    // 定义管理员删除优惠券模板的方法，返回类型为R，接收路径参数id
/**
 * deleteCouponTemplate方法
 *
 * @author 李梦瑶
 */
    public R deleteCouponTemplate(@PathVariable Long id) {  // 优惠券ID
        // 使用try-catch捕获可能的异常
        try {
            // 根据ID查询优惠券信息
            Coupon coupon = couponService.getById(id);
            // 判断优惠券是否存在
            if (coupon == null) {
                // 返回错误响应，提示优惠券不存在
                return R.error("优惠券不存在");
            }

            // 只能删除模板，不能删除用户已领取的优惠券
            // 判断优惠券是否已被用户领取
            if (coupon.getUserId() != null) {
                // 返回错误响应，提示不能删除用户已领取的优惠券
                return R.error("不能删除用户已领取的优惠券");
            }

            // 根据ID删除优惠券
            boolean result = couponService.removeById(id);

            // 判断删除是否成功
            if (result) {
                // 返回成功响应，包含成功消息
                    /**
     * R
     * 
     * @author 李梦瑶
     */
                return new R().addData("message", "优惠券删除成功");
            } else {
                // 返回错误响应，提示优惠券删除失败
                return R.error("优惠券删除失败");
            }
        } catch (Exception e) {  // 捕获异常
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("优惠券删除失败: " + e.getMessage());
        }
    }

    /**
     * 管理员获取优惠券详情
     */
    // 使用@GetMapping注解映射GET请求到/coupon/admin/detail/{id}路径
    @GetMapping("/admin/detail/{id}")
    // 定义管理员获取优惠券详情的方法，返回类型为R，接收路径参数id
/**
 * getCouponDetail方法
 *
 * @author 李梦瑶
 */
    public R getCouponDetail(@PathVariable Long id) {  // 优惠券ID
        // 使用try-catch捕获可能的异常
        try {
            // 根据ID查询优惠券信息
            Coupon coupon = couponService.getById(id);
            // 判断优惠券是否存在
            if (coupon == null) {
                // 返回错误响应，提示优惠券不存在
                return R.error("优惠券不存在");
            }

            // 返回包含优惠券对象的响应
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("coupon", coupon);
        } catch (Exception e) {  // 捕获异常
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("获取优惠券详情失败: " + e.getMessage());
        }
    }
// CouponController类定义结束
}
