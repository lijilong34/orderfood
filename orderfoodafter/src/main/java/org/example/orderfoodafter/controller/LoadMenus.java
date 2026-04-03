// 定义菜单加载控制器的包路径
package org.example.orderfoodafter.controller;
// 导入JWT工具类
import org.example.orderfoodafter.common.JwtUtils;
// 导入通用响应结果类
import org.example.orderfoodafter.common.R;
// 导入菜单实体类
import org.example.orderfoodafter.entity.Menus;
// 导入菜单服务接口
import org.example.orderfoodafter.service.MenusService;
// 导入Spring的自动装配注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring的GET请求映射注解
import org.springframework.web.bind.annotation.GetMapping;
// 导入Spring的路径变量注解
import org.springframework.web.bind.annotation.PathVariable;
// 导入Spring的请求头注解
import org.springframework.web.bind.annotation.RequestHeader;
// 导入Spring的REST控制器注解
import org.springframework.web.bind.annotation.RestController;
// 导入Java的List集合类
import java.util.List;

/**
 * 菜单加载控制器
 * 负责根据用户权限动态加载菜单列表，支持管理员和普通用户的不同菜单显示
 * 
 * @author 李吉隆
 * @date 2025-11-17
 */
// 标记该类为REST控制器，所有方法返回JSON格式数据
@RestController
// 定义菜单加载控制器类
/**
 * LoadMenus类
 *
 * @author 李吉隆
 * @date 2026-03-18
 */

public class LoadMenus {
// 自动注入菜单服务，使用依赖注入方式获取实例
@Autowired
private MenusService menusService;
// 自动注入JWT工具类，使用依赖注入方式获取实例
@Autowired
JwtUtils jwtUtils;
//根据请求头查询菜单
// 定义处理查询管理员菜单的GET请求接口，路径为/selectmenusforadmin
@GetMapping("/selectmenusforadmin")
// 定义查询管理员菜单的方法，接收JWT令牌请求头参数
/**
 * selectmenusforadmin方法
 *
 * @author 李吉隆
 */
public R selectmenusforadmin(@RequestHeader("Authorization") String token) {
    // 从JWT令牌中移除"Bearer "前缀
    token=token.substring(7);
    // 从JWT令牌中解析出用户ID
    String subject = jwtUtils.getSubject(token);
    // 将用户ID转换为int类型
    int employeeid=Integer.parseInt(subject);
    // 调用服务根据员工ID查询菜单列表
    List<Menus> menusList=menusService.selectMenusbyId(employeeid);
    // 返回菜单列表数据
        /**
     * R
     * 
     * @author 李吉隆
     */
    return new R().addData("menusList",menusList);
}
// 定义处理根据用户ID查询菜单的GET请求接口，路径为/selectmenusbyuserid/{employeeid}
@GetMapping("/selectmenusbyuserid/{employeeid}")
// 定义根据用户ID查询菜单的方法，接收员工ID路径参数
/**
 * selectmenusbyuserid方法
 *
 * @author 李吉隆
 */
public R selectmenusbyuserid(@PathVariable("employeeid") int employeeid) {
    // 调用服务根据员工ID查询菜单列表
    List<Menus> menusList=menusService.selectMenusbyId(employeeid);
    // 返回菜单列表数据
        /**
     * R
     * 
     * @author 李吉隆
     */
    return new R().addData("menusList",menusList);
}
// 定义处理查询所有菜单的GET请求接口，路径为/selectmenus
@GetMapping("/selectmenus")
// 定义查询所有菜单的方法
/**
 * selectmenus方法
 *
 * @author 李吉隆
 */
public R selectmenus() {
    // 调用服务查询所有菜单列表
    List<Menus> menusList=menusService.list();
    // 返回菜单列表数据
        /**
     * R
     * 
     * @author 李吉隆
     */
    return new R().addData("menusList",menusList);
}
}
