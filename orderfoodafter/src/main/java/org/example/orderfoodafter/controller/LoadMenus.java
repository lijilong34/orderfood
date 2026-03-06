package org.example.orderfoodafter.controller;

import org.example.orderfoodafter.common.JwtUtils;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.Menus;
import org.example.orderfoodafter.service.MenusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 描述：动态加载菜单
 * 作者:李吉隆
 * 日期:2025/12/3
 */
@RestController
public class LoadMenus {
@Autowired
private MenusService menusService;
@Autowired
JwtUtils jwtUtils;
//根据请求头查询菜单
@GetMapping("/selectmenusforadmin")
public R selectmenusforadmin(@RequestHeader("Authorization") String token) {
    token=token.substring(7);
    String subject = jwtUtils.getSubject(token);
    int employeeid=Integer.parseInt(subject);
    List<Menus> menusList=menusService.selectMenusbyId(employeeid);
    return new R().addData("menusList",menusList);
}
@GetMapping("/selectmenusbyuserid/{employeeid}")
public R selectmenusbyuserid(@PathVariable("employeeid") int employeeid) {
    List<Menus> menusList=menusService.selectMenusbyId(employeeid);
    return new R().addData("menusList",menusList);
}
@GetMapping("/selectmenus")
public R selectmenus() {
    List<Menus> menusList=menusService.list();
    return new R().addData("menusList",menusList);
}
}
