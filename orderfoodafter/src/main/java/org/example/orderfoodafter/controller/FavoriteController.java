// 定义收藏控制器的包路径
package org.example.orderfoodafter.controller;
// 导入MyBatis Plus的查询条件包装器类
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入JWT工具类
import org.example.orderfoodafter.common.JwtUtils;
// 导入通用响应结果类
import org.example.orderfoodafter.common.R;
// 导入用户收藏实体类
import org.example.orderfoodafter.entity.UserFavorites;
// 导入用户收藏服务接口
import org.example.orderfoodafter.service.UserFavoritesService;
// 导入Spring的自动装配注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring的请求映射注解集合
import org.springframework.web.bind.annotation.*;
// 导入Java的HashMap类
import java.util.HashMap;
// 导入Java的List集合类
import java.util.List;
// 导入Java的Map接口
import java.util.Map;

/**
 * 收藏控制器
 * 负责管理用户的收藏功能，包括添加收藏、查询收藏列表、取消收藏等操作
 * 
 * @author 李梦瑶
 * @date 2025-11-23
 */
// 定义该控制器的请求路径前缀为/Favorite
@RequestMapping("/Favorite")
// 标记该类为REST控制器，所有方法返回JSON格式数据
@RestController
// 定义收藏控制器类，继承基础控制器，指定实体类型为UserFavorites，服务类型为UserFavoritesService
/**
 * FavoriteController类
 *
 * @author 李梦瑶
 * @date 2026-03-18
 */

public class FavoriteController extends BaseController<UserFavorites, UserFavoritesService> {
    // 自动注入用户收藏服务，使用依赖注入方式获取实例
    @Autowired
    private UserFavoritesService userFavoritesService;
    // 构造函数，接收用户收藏服务作为参数
/**
 * FavoriteController方法
 *
 * @author 李梦瑶
 */
    public FavoriteController(UserFavoritesService userFavoritesService) {
        // 调用父类构造函数，传入服务实例
        super(userFavoritesService);
        // 将传入的服务实例赋值给本地变量
        this.userFavoritesService = userFavoritesService;
    }
    
    // 自动注入JWT工具类，使用依赖注入方式获取实例
    @Autowired
    private JwtUtils jwtUtils;
    
    // 定义处理检查收藏状态的GET请求接口，路径为/check/{productId}
    @GetMapping("/check/{productId}")
    // 定义检查收藏状态的方法，接收JWT令牌和产品ID路径参数
/**
 * check方法
 *
 * @author 李梦瑶
 */
    public R check(@RequestHeader("Authorization") String token,@PathVariable("productId") long productId) {
        // 从JWT令牌中移除"Bearer "前缀
        token=token.substring(7);
        // 从JWT令牌中解析出用户ID
        String subject = jwtUtils.getSubject(token);
        // 将用户ID转换为long类型
        long userid=Long.parseLong(subject);
        // 创建查询条件包装器对象
        QueryWrapper<UserFavorites> userFavoritesQueryWrapper=new QueryWrapper<>();
        // 设置查询条件：用户ID等于当前用户ID
        userFavoritesQueryWrapper.eq("user_id", userid);
        // 设置查询条件：产品ID等于传入的产品ID
        userFavoritesQueryWrapper.eq("product_id", productId);
        // 根据查询条件查询收藏记录
        UserFavorites userFavorites = userFavoritesService.getOne(userFavoritesQueryWrapper);
        // 返回收藏记录数据
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("userFavorites",userFavorites);
    }
    
    // 定义处理添加收藏的GET请求接口，路径为/addToFavorite/{productId}
    @GetMapping("/addToFavorite/{productId}")
    // 定义添加收藏的方法，接收JWT令牌和产品ID路径参数
/**
 * addToFavorite方法
 *
 * @author 李梦瑶
 */
    public R addToFavorite(@RequestHeader("Authorization") String token,@PathVariable("productId") long productId){
        // 从JWT令牌中移除（token)"Bearer "前缀
        token=token.substring(7);
        // 从JWT令牌中解析出用户ID
        String subject = jwtUtils.getSubject(token);
        // 将用户ID转换为long类型
        long userid=Long.parseLong(subject);
        
        // 检查是否已经收藏
        // 创建查询条件包装器对象
        QueryWrapper<UserFavorites> checkWrapper = new QueryWrapper<>();
        // 设置查询条件：用户ID等于当前用户ID
        checkWrapper.eq("user_id", userid);
        // 设置查询条件：产品ID等于传入的产品ID
        checkWrapper.eq("product_id", productId);
        // 根据查询条件查询收藏记录
        UserFavorites existing = userFavoritesService.getOne(checkWrapper);
        
        // 判断是否已存在收藏记录
        if(existing != null){
            // 如果已存在，返回已收藏状态消息
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("status","已经收藏过了");
        }
        
        // 创建新的用户收藏对象
            /**
     * UserFavorites
     * 
     * @author 李梦瑶
     */
        UserFavorites userFavorites=new UserFavorites();
        // 设置用户ID
        userFavorites.setUserId(userid);
        // 设置产品ID
        userFavorites.setProductId(productId);
        // 调用服务保存收藏记录
        boolean flag=userFavoritesService.save(userFavorites);
        // 判断保存是否成功
        if(flag){
            // 如果保存成功，返回成功状态消息
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("status","添加成功");
        }
        // 如果保存失败，返回失败状态消息
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("status","添加失败");
    }
    
    // 定义处理查询用户收藏列表的GET请求接口，路径为/selectfavoritebyuserid
    @GetMapping("/selectfavoritebyuserid")
    // 定义查询用户收藏列表的方法，接收JWT令牌参数
/**
 * selectFavoriteByUserId方法
 *
 * @author 李梦瑶
 */
    public R    selectFavoriteByUserId(@RequestHeader("Authorization") String token){
        // 从JWT令牌中移除"Bearer "前缀
        token=token.substring(7);
        // 从JWT令牌中解析出用户ID
        String subject = jwtUtils.getSubject(token);
        // 将用户ID转换为long类型
        long userid=Long.parseLong(subject);
        
        // 调用服务根据用户ID查询收藏列表
        List<UserFavorites> favoritesList = userFavoritesService.selectFavoritesByUserId(userid);
        
        // 创建结果Map对象
        Map<String, Object> result = new HashMap<>();
        // 将收藏列表放入结果Map
        result.put("favoriteList", favoritesList);
        // 将收藏总数放入结果Map
        result.put("total", favoritesList.size());
        
        // 返回收藏列表数据
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("favoriteList", favoritesList);
    }
    
    // 定义处理取消收藏的GET请求接口，路径为/removeFavorite/{id}
    @GetMapping("/removeFavorite/{id}")
    // 定义取消收藏的方法，接收JWT令牌和收藏ID路径参数
/**
 * remove方法
 *
 * @author 李梦瑶
 */
    public R remove(@RequestHeader("Authorization") String token, @PathVariable("id") long id){
        // 从JWT令牌中移除"Bearer "前缀
        token=token.substring(7);
        // 从JWT令牌中解析出用户ID
        String subject = jwtUtils.getSubject(token);
        // 将用户ID转换为long类型
        long userid=Long.parseLong(subject);
        
        // 创建查询条件包装器对象
        QueryWrapper<UserFavorites> queryWrapper = new QueryWrapper<>();
        // 设置查询条件：用户ID等于当前用户ID
        queryWrapper.eq("user_id", userid);
        // 设置查询条件：收藏ID等于传入的收藏ID
        queryWrapper.eq("id", id);
        
        // 根据查询条件删除收藏记录
        boolean flag = userFavoritesService.remove(queryWrapper);
        
        // 判断删除是否成功
        if(flag){
            // 如果删除成功，返回成功状态消息
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("status","取消收藏成功");
        }else{
            // 如果删除失败，返回失败状态消息
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("status","取消收藏失败");
        }
    }
}
