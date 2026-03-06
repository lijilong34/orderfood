package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.orderfoodafter.common.JwtUtils;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.UserFavorites;
import org.example.orderfoodafter.service.UserFavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/Favorite")
@RestController
public class FavoriteController extends BaseController<UserFavorites, UserFavoritesService> {
    @Autowired
    private UserFavoritesService userFavoritesService;
    public FavoriteController(UserFavoritesService userFavoritesService) {
        super(userFavoritesService);
        this.userFavoritesService = userFavoritesService;
    }
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @GetMapping("/check/{productId}")
    public R check(@RequestHeader("Authorization") String token,@PathVariable("productId") long productId) {
        token=token.substring(7);
        String subject = jwtUtils.getSubject(token);
        long userid=Long.parseLong(subject);
        QueryWrapper<UserFavorites> userFavoritesQueryWrapper = new QueryWrapper<>();
        userFavoritesQueryWrapper.eq("user_id", userid);
        userFavoritesQueryWrapper.eq("product_id", productId);
        UserFavorites userFavorites = userFavoritesService.getOne(userFavoritesQueryWrapper);
        return new R().addData("userFavorites",userFavorites);
    }
    
    @GetMapping("/addToFavorite/{productId}")
    public R addToFavorite(@RequestHeader("Authorization") String token,@PathVariable("productId") long productId){
        token=token.substring(7);
        String subject = jwtUtils.getSubject(token);
        long userid=Long.parseLong(subject);
        
        // 检查是否已经收藏
        QueryWrapper<UserFavorites> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("user_id", userid);
        checkWrapper.eq("product_id", productId);
        UserFavorites existing = userFavoritesService.getOne(checkWrapper);
        
        if(existing != null){
            return new R().addData("status","已经收藏过了");
        }
        
        UserFavorites userFavorites=new UserFavorites();
        userFavorites.setUserId(userid);
        userFavorites.setProductId(productId);
        boolean flag=userFavoritesService.save(userFavorites);
        if(flag){
            return new R().addData("status","添加成功");
        }
        return new R().addData("status","添加失败");
    }
    
    @GetMapping("/selectfavoritebyuserid")
    public R selectFavoriteByUserId(@RequestHeader("Authorization") String token){
        token=token.substring(7);
        String subject = jwtUtils.getSubject(token);
        long userid=Long.parseLong(subject);
        
        List<UserFavorites> favoritesList = userFavoritesService.selectFavoritesByUserId(userid);
        
        Map<String, Object> result = new HashMap<>();
        result.put("favoriteList", favoritesList);
        result.put("total", favoritesList.size());
        
        return new R().addData("favoriteList", favoritesList);
    }
    
    @GetMapping("/removeFavorite/{id}")
    public R remove(@RequestHeader("Authorization") String token, @PathVariable("id") long id){
        token=token.substring(7);
        String subject = jwtUtils.getSubject(token);
        long userid=Long.parseLong(subject);
        
        QueryWrapper<UserFavorites> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userid);
        queryWrapper.eq("id", id);
        
        boolean flag = userFavoritesService.remove(queryWrapper);
        
        if(flag){
            return new R().addData("status","取消收藏成功");
        }else{
            return new R().addData("status","取消收藏失败");
        }
    }
}
