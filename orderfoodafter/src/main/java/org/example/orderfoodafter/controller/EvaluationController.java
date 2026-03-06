package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.orderfoodafter.common.CommontUtil;
import org.example.orderfoodafter.common.JwtUtils;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.Evaluation;
import org.example.orderfoodafter.entity.Order;
import org.example.orderfoodafter.service.EvaluationService;
import org.example.orderfoodafter.service.OrderItemService;
import org.example.orderfoodafter.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/evaluation")
public class EvaluationController extends BaseController<Evaluation, EvaluationService> {
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private OrderService orderService;
    @Autowired
    JwtUtils jwtUtils;

    public EvaluationController(EvaluationService evaluationService) {
        super(evaluationService);
        this.evaluationService = evaluationService;
    }

    @GetMapping("/selectevaluationProductId/{ProductId}")
    public R selectevaluationProductId(@PathVariable("ProductId") long productId){
        List<Evaluation> evaluationList=evaluationService.selectevaluationbyProductId(productId);
        return new R().addData("evaluationList", evaluationList);
    }
    @PostMapping("/addevaluation")
    public R addEvaluation(@RequestHeader("Authorization") String token, @RequestBody Evaluation evaluation){
        try {
            token = token.substring(7);
            String subject = jwtUtils.getSubject(token);
            long userid = Long.parseLong(subject);
            evaluation.setUserId(userid);
            
            // 设置创建时间和更新时间
            evaluation.setCreateTime(new java.util.Date());
            evaluation.setUpdateTime(new java.util.Date());
            
            System.out.println("接收到的评价数据：");
            System.out.println("orderId: " + evaluation.getOrderId());
            System.out.println("shopId: " + evaluation.getShopId());
            System.out.println("dishScore: " + evaluation.getDishScore());
            System.out.println("serviceScore: " + evaluation.getServiceScore());
            System.out.println("environmentScore: " + evaluation.getEnvironmentScore());
            System.out.println("content: " + evaluation.getContent());
            System.out.println("imageUrls: " + evaluation.getImageUrls());
            
            boolean flag = evaluationService.save(evaluation);
            Order order=orderService.getById(evaluation.getOrderId());
            order.setStatus(4);
            boolean flag1=orderService.updateById(order);
            if(flag&&flag1){
                return new R().addData("status", "添加评论成功");
            }
            return R.error("添加评论失败");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("添加评论失败：" + e.getMessage());
        }
    }
    @Autowired
    private CommontUtil commontUtil;

    @PostMapping("/selectevaluation")
    public R selectevaluation(@RequestBody Map<String, Object> selectwhere) throws Exception{
        List where = (List) selectwhere.get("where");

        QueryWrapper queryWrapper = commontUtil.getWhere(where);
        
        // 添加店铺ID过滤（针对店铺管理员）
        Long shopId = null;
        if (selectwhere.get("shopId") != null) {
            shopId = Long.parseLong(selectwhere.get("shopId").toString());
            queryWrapper.eq("a.shop_id", shopId);
        }
        
        if (selectwhere.get("page") != null) {
            int page = Integer.parseInt(selectwhere.get("page").toString());
            PageHelper.startPage(page, 8);
        }
        
        // 使用店铺管理员的查询方法
        List<Evaluation> evaluationList = evaluationService.selectevaluationbyShopIdWithQuery(queryWrapper);
        PageInfo pageInfo = new PageInfo<>(evaluationList);

        return new R().addData("pageInfo", pageInfo);
    }
    @GetMapping("/selectevaluationShopId/{ShopId}")
    public R selectevaluationShopId(@PathVariable("ShopId") long shopId){
        List<Evaluation> evaluationList=evaluationService.selectevaluationbyShopId(shopId);
        return new R().addData("evaluationList", evaluationList);
            }
        
}
