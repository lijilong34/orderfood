// 定义包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入MyBatis Plus的QueryWrapper类，用于构建查询条件
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入项目通用的R类，用于统一响应格式
import org.example.orderfoodafter.common.R;
// 导入UserFavorites实体类
import org.example.orderfoodafter.entity.UserFavorites;
// 导入UserFavoritesService服务接口
import org.example.orderfoodafter.service.UserFavoritesService;
// 导入Spring的Autowired注解，用于自动装配
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring MVC的映射注解
import org.springframework.web.bind.annotation.*;

// 导入Java集合类HashMap
import java.util.HashMap;
// 导入Java集合类List
import java.util.List;
// 导入Java集合类Map
import java.util.Map;

/**
 * 用户收藏控制器
 * 负责管理用户的收藏功能，包括添加收藏、查询收藏列表、取消收藏等操作
 *
 * @author 李吉隆
 * @date 2025-12-20
 */
// 使用RestController注解标记该类为RESTful控制器，所有方法返回JSON
@RestController
// 使用RequestMapping注解设置该控制器的基础请求路径为/favorites
@RequestMapping("/favorites")
// 定义UserFavoritesController类，继承自BaseController基类，泛型为UserFavorites和UserFavoritesService
/**
 * UserFavoritesController类
 *
 * @author 李吉隆
 * @date 2026-03-18
 */

public class UserFavoritesController extends BaseController<UserFavorites, UserFavoritesService> {
    // 使用Autowired注解自动注入UserFavoritesService服务实例
    @Autowired
    // 声明UserFavoritesService服务对象
    private UserFavoritesService userFavoritesService;
    // 定义UserFavoritesController的构造函数，接收UserFavoritesService参数
/**
 * UserFavoritesController方法
 *
 * @author 李吉隆
 */
    public UserFavoritesController(UserFavoritesService userFavoritesService) {
        // 调用父类BaseController的构造函数，传入userFavoritesService
        super(userFavoritesService);
        // 将传入的userFavoritesService赋值给当前类的userFavoritesService属性
        this.userFavoritesService = userFavoritesService;
    }

    // 使用PostMapping注解映射POST请求到/selectFavoritesByUserId路径
    @PostMapping("/selectFavoritesByUserId")
    // 定义查询用户收藏列表的方法，接收Authorization请求头和Map参数，返回R响应对象
/**
 * selectFavoritesByUserId方法
 *
 * @author 李吉隆
 */
    public R selectFavoritesByUserId(@RequestHeader("Authorization") String token,@RequestBody Map<String, Object> selectwhere) throws Exception{
        // 去掉token前缀"Bearer "
        token=token.substring(7);
        // 从token中获取用户ID
        String userid = jwtUtils.getSubject(token);

        // 调用service层查询用户的收藏列表
        List<UserFavorites> userFavoritesList=userFavoritesService.selectFavoritesByUserId(Long.parseLong(userid));

        // 创建结果Map
        Map<String, Object> result = new HashMap<>();
        // 将收藏列表添加到结果中
        result.put("favoriteList", userFavoritesList);
        // 将收藏数量添加到结果中
        result.put("total", userFavoritesList.size());

        // 返回成功响应，包含收藏列表
            /**
     * R
     * 
     * @author 李吉隆
     */
        return new R().addData("favoriteList", userFavoritesList);
    }

    // 使用PostMapping注解映射POST请求到/addFavorite路径
    @PostMapping("/addFavorite")
    // 定义添加收藏的方法，接收Authorization请求头和Object参数，返回R响应对象
/**
 * addFavorite方法
 *
 * @author 李吉隆
 */
    public R addFavorite(@RequestHeader("Authorization") String token,@RequestBody Object productId){
        // 去掉token前缀"Bearer "
        token=token.substring(7);
        // 从token中获取用户ID
        String userid = jwtUtils.getSubject(token);
        // 将用户ID转换为long类型
        long userId=Long.parseLong(userid);
        // 将productId转换为long类型
        long productid = Long.parseLong(productId.toString());

        // 检查是否已收藏
        // 创建查询条件构建器
        QueryWrapper<UserFavorites> checkWrapper = new QueryWrapper<>();
        // 设置查询条件：用户ID等于当前用户ID
        checkWrapper.eq("user_id", userId);
        // 设置查询条件：商品ID等于传入的商品ID
        checkWrapper.eq("product_id", productid);
        // 查询单个收藏记录
        UserFavorites existing = userFavoritesService.getOne(checkWrapper);

        // 判断收藏记录是否存在
        if(existing != null){
            // 如果已收藏，返回已收藏状态
                /**
     * R
     * 
     * @author 李吉隆
     */
            return new R().addData("status","已经收藏过了");
        }

        // 创建新的UserFavorites对象
            /**
     * UserFavorites
     * 
     * @author 李吉隆
     */
        UserFavorites userFavorites=new UserFavorites();
        // 设置用户ID
        userFavorites.setUserId(userId);
        // 设置商品ID
        userFavorites.setProductId(productid);
        // 调用service层保存收藏记录
      boolean flag =userFavoritesService.save(userFavorites);
      // 判断保存是否成功
      if(!flag){
          // 如果保存失败，返回失败状态
              /**
     * R
     * 
     * @author 李吉隆
     */
          return new R().addData("status","收藏失败");
      }
      // 返回成功状态
          /**
     * R
     * 
     * @author 李吉隆
     */
      return new R().addData("status","收藏成功");
    }

    // 使用PostMapping注解映射POST请求到/removeFavorite路径
    @PostMapping("/removeFavorite")
    // 定义取消收藏的方法，接收Authorization请求头和Object参数，返回R响应对象
/**
 * removeFavorite方法
 *
 * @author 李吉隆
 */
    public R removeFavorite(@RequestHeader("Authorization") String token,@RequestBody Object params){
        // 去掉token前缀"Bearer "
        token=token.substring(7);
        // 从token中获取用户ID
        String userid = jwtUtils.getSubject(token);
        // 将用户ID转换为long类型
        long userId=Long.parseLong(userid);

        // 判断params是否为空
        if(params == null){
            // 全部取消收藏
            // 创建查询条件构建器
            QueryWrapper<UserFavorites> queryWrapper = new QueryWrapper<>();
            // 设置查询条件：用户ID等于当前用户ID
            queryWrapper.eq("user_id",userId);
            // 删除该用户的所有收藏记录
            boolean flag = userFavoritesService.remove(queryWrapper);
            // 根据删除结果返回相应状态
                /**
     * R
     * 
     * @author 李吉隆
     */
            return new R().addData("status",flag?"全部取消成功":"全部取消失败");
        }else{
            // 取消单个收藏
            // 将params转换为long类型
            long favoriteid = Long.parseLong(params.toString());
            // 创建查询条件构建器
            QueryWrapper<UserFavorites> queryWrapper = new QueryWrapper<>();
            // 设置查询条件：用户ID等于当前用户ID
            queryWrapper.eq("user_id",userId);
            // 设置查询条件：收藏ID等于传入的收藏ID
            queryWrapper.eq("id",favoriteid);
            // 删除指定的收藏记录
            boolean flag = userFavoritesService.remove(queryWrapper);
            // 根据删除结果返回相应状态
                /**
     * R
     * 
     * @author 李吉隆
     */
            return new R().addData("status",flag?"取消成功":"取消失败");
        }
    }
}