package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.UserFavorites;
import org.example.orderfoodafter.service.UserFavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favorites")
public class UserFavoritesController extends BaseController<UserFavorites, UserFavoritesService> {
    @Autowired
    private UserFavoritesService userFavoritesService;
    public UserFavoritesController(UserFavoritesService userFavoritesService) {
        super(userFavoritesService);
        this.userFavoritesService = userFavoritesService;
    }

    @PostMapping("/selectFavoritesByUserId")
    public R selectFavoritesByUserId(@RequestHeader("Authorization") String token,@RequestBody Map<String, Object> selectwhere) throws Exception{
        token=token.substring(7);
        String userid = jwtUtils.getSubject(token);
        
        List<UserFavorites> userFavoritesList=userFavoritesService.selectFavoritesByUserId(Long.parseLong(userid));
        
        Map<String, Object> result = new HashMap<>();
        result.put("favoriteList", userFavoritesList);
        result.put("total", userFavoritesList.size());
        
        return new R().addData("favoriteList", userFavoritesList);
    }

    @PostMapping("/addFavorite")
    public R addFavorite(@RequestHeader("Authorization") String token,@RequestBody Object productId){
        token=token.substring(7);
        String userid = jwtUtils.getSubject(token);
        long userId=Long.parseLong(userid);
        long productid = Long.parseLong(productId.toString());
        
        // 检查是否已收藏
        QueryWrapper<UserFavorites> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("user_id", userId);
        checkWrapper.eq("product_id", productid);
        UserFavorites existing = userFavoritesService.getOne(checkWrapper);
        
        if(existing != null){
            return new R().addData("status","已经收藏过了");
        }
        
        UserFavorites userFavorites=new UserFavorites();
        userFavorites.setUserId(userId);
        userFavorites.setProductId(productid);
      boolean flag =userFavoritesService.save(userFavorites);
      if(!flag){
          return new R().addData("status","收藏失败");
      }
      return new R().addData("status","收藏成功");
    }

    @PostMapping("/removeFavorite")
    public R removeFavorite(@RequestHeader("Authorization") String token,@RequestBody Object params){
        token=token.substring(7);
        String userid = jwtUtils.getSubject(token);
        long userId=Long.parseLong(userid);
        
        if(params == null){
            // 全部取消
            QueryWrapper<UserFavorites> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id",userId);
            boolean flag = userFavoritesService.remove(queryWrapper);
            return new R().addData("status",flag?"全部取消成功":"全部取消失败");
        }else{
            // 取消单个
            long favoriteid = Long.parseLong(params.toString());
            QueryWrapper<UserFavorites> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id",userId);
            queryWrapper.eq("id",favoriteid);
            boolean flag = userFavoritesService.remove(queryWrapper);
            return new R().addData("status",flag?"取消成功":"取消失败");
        }
    }
}