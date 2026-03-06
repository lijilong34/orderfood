package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.common.WhereEntity;
import org.example.orderfoodafter.entity.BrowseHistory;
import org.example.orderfoodafter.service.BrowseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/history")
public class BrowseHistoryController extends BaseController<BrowseHistory, BrowseHistoryService> {
    @Autowired
    private BrowseHistoryService browseHistoryService;
    public BrowseHistoryController(BrowseHistoryService browseHistoryService) {
        super(browseHistoryService);
        this.browseHistoryService = browseHistoryService;
    }
    @PostMapping("/selecthistorybyuserId")
    public R selecthistorybyUserId(@RequestHeader("Authorization") String token,@RequestBody Map<String, Object> selectwhere) throws Exception{
        token=token.substring(7);
        String userid = jwtUtils.getSubject(token);
        List where = (List) selectwhere.get("where");
        
        // 如果where为null，创建一个新的列表
        if (where == null) {
            where = new ArrayList();
        }
        
        // 添加用户ID过滤条件
        WhereEntity whereEntity = new WhereEntity();
        whereEntity.setColumn("user_id");
        whereEntity.setType("eq");
        whereEntity.setValue(userid);
        where.add(whereEntity);
        
        QueryWrapper queryWrapper = commontUtil.getWhere(where);
        if (selectwhere.get("page") != null) {
            int page = Integer.parseInt(selectwhere.get("page").toString());
            PageHelper.startPage(page, 6);
        }
        
        List<BrowseHistory> browseHistoryList=browseHistoryService.selecthistorybyuserId(queryWrapper);
        PageInfo<BrowseHistory> pageInfo=new PageInfo<>(browseHistoryList);
        return new R().addData("pageInfo", pageInfo);
    }

    @GetMapping("/addhistory/{productId}")
    public R addhistory(@RequestHeader("Authorization") String token,@PathVariable("productId") long productId){
        token=token.substring(7);
        String userid = jwtUtils.getSubject(token);
        long userId=Long.parseLong(userid);
        BrowseHistory browseHistory=new BrowseHistory();
        browseHistory.setUserId(userId);
        browseHistory.setProductId(productId);
      boolean flag =browseHistoryService.save(browseHistory);
      if(!flag){
          throw new RuntimeException("添加历史记录失败");
      }
      return new R().addData("status","添加历史记录成功");
    }

    @PostMapping("/delhistory")
    public R delhistory(@RequestHeader("Authorization") String token,@RequestBody long historyid){
        token=token.substring(7);
        String userid = jwtUtils.getSubject(token);
        long userId=Long.parseLong(userid);
        
        if(historyid == 0){
            // 全部清除
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("user_id",userId);
            boolean flag = browseHistoryService.remove(queryWrapper);
            return new R().addData("status",flag?"全部清除成功":"全部清除失败");
        }else{
            // 删除单个
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("user_id",userId);
            queryWrapper.eq("id",historyid);
            boolean flag = browseHistoryService.remove(queryWrapper);
            return new R().addData("status",flag?"删除成功":"删除失败");
        }
    }
}
