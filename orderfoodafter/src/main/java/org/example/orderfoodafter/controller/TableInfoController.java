package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.Product;
import org.example.orderfoodafter.entity.TableInfo;
import org.example.orderfoodafter.service.ProductService;
import org.example.orderfoodafter.service.TableInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/table")
@RestController
public class TableInfoController extends BaseController<TableInfo, TableInfoService> {
    @Autowired
    private TableInfoService tableInfoService;
    
    @Autowired
    private ProductService productService;

    public TableInfoController(TableInfoService tableInfoService) {
        super(tableInfoService);
        this.tableInfoService = tableInfoService;
    }

    @GetMapping("/available")
    public R getAvailableTables(@RequestParam(required = false) Long shopId, 
                                @RequestParam(required = false) Long productId) {
        Long finalShopId = shopId;
        
        if (productId != null && productId > 0) {
            Product product = productService.getById(productId);
            if (product == null) {
                return R.error("��Ʒ������");
            }
            finalShopId = product.getShopId();
        }
        
        if (finalShopId == null || finalShopId <= 0) {
            return R.error("�̵�ID����Ϊ��");
        }
        
        QueryWrapper<TableInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shop_id", finalShopId);
        queryWrapper.eq("status", 0);
        queryWrapper.orderByAsc("table_no");

        List<TableInfo> tableList = tableInfoService.list(queryWrapper);
        return new R().addData("tableList", tableList);
    }
}
