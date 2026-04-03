// 定义当前类的包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入MyBatis-Plus的查询包装器类，用于构建查询条件
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入PageHelper分页工具类，用于分页查询
import com.github.pagehelper.PageHelper;
// 导入PageInfo分页信息类，用于存储分页结果
import com.github.pagehelper.PageInfo;
// 导入统一响应类R，用于封装API响应
import org.example.orderfoodafter.common.R;
// 导入WhereEntity查询条件实体类，用于构建查询条件
import org.example.orderfoodafter.common.WhereEntity;
// 导入浏览历史实体类
import org.example.orderfoodafter.entity.BrowseHistory;
// 导入浏览历史服务接口
import org.example.orderfoodafter.service.BrowseHistoryService;
// 导入Spring的自动装配注解，用于依赖注入
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring MVC的注解集合，包括@GetMapping、@PostMapping等
import org.springframework.web.bind.annotation.*;

// 导入ArrayList集合类，用于动态数组
import java.util.ArrayList;
// 导入List集合接口，用于列表操作
import java.util.List;
// 导入Map接口，用于映射操作
import java.util.Map;

/**
 * 浏览历史控制器
 * 负责管理用户的浏览历史记录，包括添加、查询、删除浏览历史等操作
 *
 * @author 李梦瑶
 * @date 2025-11-24
 */
// 使用@RestController注解标记该类为REST控制器，所有方法返回JSON数据
@RestController
// 使用@RequestMapping注解设置该控制器的所有接口基础路径为/history
@RequestMapping("/history")
// 定义BrowseHistoryController类，继承BaseController基类，泛型为BrowseHistory和BrowseHistoryService
/**
 * BrowseHistoryController类
 *
 * @author 李梦瑶
 * @date 2026-03-18
 */

public class BrowseHistoryController extends BaseController<BrowseHistory, BrowseHistoryService> {
    // 使用@Autowired注解自动注入BrowseHistoryService服务实例
    @Autowired
    // 声明浏览历史服务接口的私有成员变量
    private BrowseHistoryService browseHistoryService;
    // 定义带参构造函数，接收BrowseHistoryService服务实例参数
/**
 * BrowseHistoryController方法
 *
 * @author 李梦瑶
 */
    public BrowseHistoryController(BrowseHistoryService browseHistoryService) {  // 浏览历史服务实例
        // 调用父类构造函数，传入服务实例
        super(browseHistoryService);
        // 将传入的服务实例赋值给成员变量
        this.browseHistoryService = browseHistoryService;
    }
    /**
     * 根据用户ID查询浏览历史
     * 作者:赵康乐
     * @param token 用户令牌
     * @param selectwhere 查询条件
     * @return 浏览历史分页数据
     * @throws Exception
     */
    // 使用@PostMapping注解映射POST请求到/history/selecthistorybyuserId路径
    @PostMapping("/selecthistorybyuserId")
    // 定义根据用户ID查询浏览历史的方法，返回类型为R，接收请求头token和请求体selectwhere
        /**
     * selecthistorybyUserId
     * 
     * @author 李梦瑶
     */
    public R selecthistorybyUserId(@RequestHeader("Authorization") String token,@RequestBody Map<String, Object> selectwhere) throws Exception{  // 用户令牌和查询条件
        // 去除token的前缀"Bearer "
        token=token.substring(7);
        // 从token中获取用户ID
        String userid = jwtUtils.getSubject(token);
        // 从查询条件映射中获取where条件列表
        List where = (List) selectwhere.get("where");

        // 如果where为null，创建一个新的列表
        if (where == null) {
            // 创建新的ArrayList
                /**
     * ArrayList
     * 
     * @author 李梦瑶
     */
            where = new ArrayList();
        }

        // 添加用户ID过滤条件
        // 创建WhereEntity对象
            /**
     * WhereEntity
     * 
     * @author 李梦瑶
     */
        WhereEntity whereEntity = new WhereEntity();
        // 设置列名为user_id
        whereEntity.setColumn("user_id");
        // 设置条件类型为等于
        whereEntity.setType("eq");
        // 设置条件值为用户ID
        whereEntity.setValue(userid);
        // 将条件添加到where列表
        where.add(whereEntity);

        // 调用commontUtil的getWhere方法构建查询包装器
        QueryWrapper queryWrapper = commontUtil.getWhere(where);
        // 判断是否需要分页
        if (selectwhere.get("page") != null) {
            // 获取页码并转换为整数
            int page = Integer.parseInt(selectwhere.get("page").toString());
            // 启动分页，每页6条记录
            PageHelper.startPage(page, 6);
        }

        // 调用服务层方法查询浏览历史列表
        List<BrowseHistory> browseHistoryList=browseHistoryService.selecthistorybyuserId(queryWrapper);
        // 创建分页信息对象
        PageInfo<BrowseHistory> pageInfo=new PageInfo<>(browseHistoryList);
        // 返回包含分页信息的响应
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("pageInfo", pageInfo);
    }

    /**
     * 添加浏览历史记录
     * 作者:赵康乐
     * @param token 用户令牌
     * @param productId 商品ID
     * @return 添加结果
     */
    // 使用@GetMapping注解映射GET请求到/history/addhistory/{productId}路径
    @GetMapping("/addhistory/{productId}")
    // 定义添加浏览历史的方法，返回类型为R，接收请求头token和路径参数productId
/**
 * addhistory方法
 *
 * @author 李梦瑶
 */
    public R addhistory(@RequestHeader("Authorization") String token,@PathVariable("productId") long productId){  // 用户令牌和商品ID
        // 去除token的前缀"Bearer "
        token=token.substring(7);
        // 从token中获取用户ID
        String userid = jwtUtils.getSubject(token);
        // 将用户ID字符串转换为长整型
        long userId=Long.parseLong(userid);
        // 创建BrowseHistory对象
            /**
     * BrowseHistory
     * 
     * @author 李梦瑶
     */
        BrowseHistory browseHistory=new BrowseHistory();
        // 设置用户ID
        browseHistory.setUserId(userId);
        // 设置商品ID
        browseHistory.setProductId(productId);
      // 调用服务层的save方法保存浏览历史
      boolean flag =browseHistoryService.save(browseHistory);
      // 判断保存是否失败
      if(!flag){
          // 抛出运行时异常，提示添加历史记录失败
              /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
          throw new RuntimeException("添加历史记录失败");
      }
      // 返回成功响应
          /**
     * R
     * 
     * @author 李梦瑶
     */
      return new R().addData("status","添加历史记录成功");
    }

    /**
     * 删除浏览历史
     * 作者:赵康乐
     * @param token 用户令牌
     * @param historyid 历史记录ID（为0时删除全部）
     * @return 删除结果
     */
    // 使用@PostMapping注解映射POST请求到/history/delhistory路径
    @PostMapping("/delhistory")
    // 定义删除浏览历史的方法，返回类型为R，接收请求头token和请求体historyid
        /**
     * delhistory
     * 
     * @author 李梦瑶
     */
    public R delhistory(@RequestHeader("Authorization") String token,@RequestBody long historyid){  // 用户令牌和历史记录ID
        // 去除token的前缀"Bearer "
        token=token.substring(7);
        // 从token中获取用户ID
        String userid = jwtUtils.getSubject(token);
        // 将用户ID字符串转换为长整型
        long userId=Long.parseLong(userid);

        // 判断历史记录ID是否为0
        if(historyid == 0){
            // 全部清除
            // 创建查询包装器对象
                /**
     * QueryWrapper
     * 
     * @author 李梦瑶
     */
            QueryWrapper queryWrapper = new QueryWrapper();
            // 添加用户ID等于条件
            queryWrapper.eq("user_id",userId);
            // 调用服务层的remove方法删除所有符合条件的记录
            boolean flag = browseHistoryService.remove(queryWrapper);
            // 返回清除结果
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("status",flag?"全部清除成功":"全部清除失败");
        }else{
            // 删除单个
            // 创建查询包装器对象
                /**
     * QueryWrapper
     * 
     * @author 李梦瑶
     */
            QueryWrapper queryWrapper = new QueryWrapper();
            // 添加用户ID等于条件
            queryWrapper.eq("user_id",userId);
            // 添加ID等于条件
            queryWrapper.eq("id",historyid);
            // 调用服务层的remove方法删除符合条件的记录
            boolean flag = browseHistoryService.remove(queryWrapper);
            // 返回删除结果
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("status",flag?"删除成功":"删除失败");
        }
    }
// BrowseHistoryController类定义结束
}
