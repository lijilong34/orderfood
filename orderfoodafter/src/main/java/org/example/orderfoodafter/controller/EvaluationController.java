// 定义评价控制器的包路径
package org.example.orderfoodafter.controller;
// 导入MyBatis Plus的查询条件包装器类
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入PageHelper分页插件
import com.github.pagehelper.PageHelper;
// 导入PageInfo分页信息类
import com.github.pagehelper.PageInfo;
// 导入通用工具类
import org.example.orderfoodafter.common.CommontUtil;
// 导入JWT工具类
import org.example.orderfoodafter.common.JwtUtils;
// 导入通用响应结果类
import org.example.orderfoodafter.common.R;
// 导入评价实体类
import org.example.orderfoodafter.entity.Evaluation;
// 导入订单实体类
import org.example.orderfoodafter.entity.Order;
// 导入评价服务接口
import org.example.orderfoodafter.service.EvaluationService;
// 导入订单项服务接口
import org.example.orderfoodafter.service.OrderItemService;
// 导入订单服务接口
import org.example.orderfoodafter.service.OrderService;
// 导入Spring的自动装配注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring的请求映射注解集合
import org.springframework.web.bind.annotation.*;
// 导入Spring的文件上传处理类
import org.springframework.web.multipart.MultipartFile;
// 导入Java的List集合类
import java.util.List;
// 导入Java的Map接口
import java.util.Map;

/**
 * 评价控制器
 * 负责管理用户对商品和服务的评价功能，包括评价的添加、查询、统计等操作
 * 
 * @author 李梦瑶
 * @date 2026-01-05
 */
// 标记该类为REST控制器，所有方法返回JSON格式数据
@RestController
// 定义该控制器的请求路径前缀为/evaluation
@RequestMapping("/evaluation")
// 定义评价控制器类，继承基础控制器，指定实体类型为Evaluation，服务类型为EvaluationService
/**
 * EvaluationController类
 *
 * @author 李梦瑶
 * @date 2026-03-18
 */

public class EvaluationController extends BaseController<Evaluation, EvaluationService> {
    // 自动注入评价服务，使用依赖注入方式获取实例
    @Autowired
    private EvaluationService evaluationService;

    // 自动注入订单服务，使用依赖注入方式获取实例
    @Autowired
    private OrderService orderService;

    // 自动注入JWT工具类，使用依赖注入方式获取实例
    @Autowired
    JwtUtils jwtUtils;

    // 自动注入通用工具类，使用依赖注入方式获取实例
    @Autowired
    private CommontUtil commontUtil;

    
    // 构造函数，接收评价服务作为参数
/**
 * EvaluationController方法
 *
 * @author 熊杨博
 */
    public EvaluationController(EvaluationService evaluationService) {
        // 调用父类构造函数，传入服务实例
        super(evaluationService);


        // 将传入的服务实例赋值给本地变量
        this.evaluationService = evaluationService;
    }


    // 定义处理根据产品ID查询评价的GET请求接口，路径为/selectevaluationProductId/{ProductId}
    @GetMapping("/selectevaluationProductId/{ProductId}")


    // 定义根据产品ID查询评价的方法，接收路径参数ProductId
    public R selectevaluationProductId(@PathVariable("ProductId") long productId){

        // 调用评价服务根据产品ID查询评价列表
        List<Evaluation> evaluationList=evaluationService.selectevaluationbyProductId(productId);


        // 返回评价列表数据
        return new R().addData("evaluationList", evaluationList);
    }

    // 定义处理根据店铺ID查询评价的GET请求接口，路径为/selectevaluationShopId/{ShopId}
    @GetMapping("/selectevaluationShopId/{ShopId}")
    // 定义根据店铺ID查询评价的方法，接收路径参数ShopId

    public R selectevaluationShopId(@PathVariable("ShopId") long shopId){

        // 调用评价服务根据店铺ID查询评价列表
        List<Evaluation> evaluationList=evaluationService.selectevaluationbyShopId(shopId);

        // 返回评价列表数据

        return new R().addData("evaluationList", evaluationList);
    }



    // 定义处理查询评价的POST请求接口，路径为/selectevaluation
    @PostMapping("/selectevaluation")


    // 定义查询评价的方法，接收查询条件Map参数，可能抛出异常
    public R selectevaluation(@RequestBody Map<String, Object> selectwhere) throws Exception{
        // 从请求参数中获取查询条件列表
        List where = (List) selectwhere.get("where");

        // 使用通用工具类将查询条件列表转换为MyBatis Plus的查询包装器对象
        QueryWrapper queryWrapper = commontUtil.getWhere(where);
        
        // 添加店铺ID过滤（针对店铺管理员）
        // 初始化店铺ID为null
        Long shopId = null;
        // 如果请求参数中包含店铺ID
        if (selectwhere.get("shopId") != null) {
            // 将店铺ID转换为long类型
            shopId = Long.parseLong(selectwhere.get("shopId").toString());
            // 在查询条件中添加店铺ID过滤
            queryWrapper.eq("a.shop_id", shopId);
        }
        
        // 如果请求参数中包含页码
        if (selectwhere.get("page") != null) {
            // 将页码转换为int类型
            int page = Integer.parseInt(selectwhere.get("page").toString());
            // 启动分页，每页显示8条记录
            PageHelper.startPage(page, 8);
        }
        
        // 使用店铺管理员的查询方法，根据查询条件查询评价列表
        List<Evaluation> evaluationList = evaluationService.selectevaluationbyShopIdWithQuery(queryWrapper);
        // 将评价列表封装为分页信息对象
        PageInfo pageInfo = new PageInfo<>(evaluationList);

        // 返回分页信息数据
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("pageInfo", pageInfo);
    }
    // 定义处理添加评价的POST请求接口，路径为/addevaluation
    @PostMapping("/addevaluation")
    // 定义添加评价的方法，接收JWT令牌和评价实体对象
/**
 * addEvaluation方法
 *
 * @author 李梦瑶
 */
    public R addEvaluation(@RequestHeader("Authorization") String token, @RequestBody Evaluation evaluation){
        // 使用try-catch捕获可能的异常
        try {
            // 从JWT令牌中移除"Bearer "前缀
            token = token.substring(7);

            // 从JWT令牌中解析出用户ID
            String subject = jwtUtils.getSubject(token);

            // 将用户ID转换为long类型
            long userid = Long.parseLong(subject);

            // 设置评价对象的用户ID
            evaluation.setUserId(userid);

            // 设置创建时间和更新时间
            evaluation.setCreateTime(new java.util.Date());
            evaluation.setUpdateTime(new java.util.Date());


            // 在控制台输出接收到的评价数据，用于调试
            System.out.println("接收到的评价数据：");
            System.out.println("orderId: " + evaluation.getOrderId());
            System.out.println("shopId: " + evaluation.getShopId());
            System.out.println("dishScore: " + evaluation.getDishScore());
            System.out.println("serviceScore: " + evaluation.getServiceScore());
            System.out.println("environmentScore: " + evaluation.getEnvironmentScore());
            System.out.println("content: " + evaluation.getContent());
            System.out.println("imageUrls: " + evaluation.getImageUrls());


            // 调用评价服务保存评价信息
            boolean flag = evaluationService.save(evaluation);

            // 根据订单ID查询订单信息
            Order order=orderService.getById(evaluation.getOrderId());

            // 设置订单状态为4（已评价）
            order.setStatus(4);

            // 更新订单信息
            boolean flag1=orderService.updateById(order);
            // 判断评价保存和订单更新是否都成功
            if(flag&&flag1){
                // 如果都成功，返回成功状态消息
                /**
                 * R
                 *
                 * @author 李梦瑶
                 */
                return new R().addData("status", "添加评论成功");
            }
            // 如果失败，抛出运行时异常
            /**
             * RuntimeException
             *
             * @author 李梦瑶
             */
            throw new RuntimeException("添加评论失败");

            // 捕获异常并抛出运行时异常
        } catch (Exception e) {
            /**
             * RuntimeException
             *
             * @author 李梦瑶
             */
            throw new RuntimeException("添加评论失败：" + e.getMessage());
        }
    }
        
}
