package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.Product;
import org.example.orderfoodafter.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class productController  extends  BaseController<Product, ProductService> {
    @Autowired
    private ProductService productService;

    public productController(ProductService productService) {
        super(productService);
        this.productService = productService;
    }

    //根据菜品id查询
    @GetMapping("/getbyid/{id}")
    public R getById(@PathVariable("id") int id) {
        Product product = productService.selectproductinfobyproductid(id);
        return new R().addData("entity", product);
    }


    /**
     * 3. 新增菜品（对应前端“增加”按钮）
     * 前端请求：POST /product/add
     * 前端传递参数：{name: "菜品名", price: 19.9, ...}（表单数据）
     */
    @PostMapping("/addProduct")
    public R addProduct(@RequestBody Product product) {
        try {
            boolean success = productService.addProduct(product);
            if (success) {
                R response = new R();
                response.setMessage("新增菜品成功");
                return response;
            }
            return R.error("新增菜品失败");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("新增菜品失败：" + e.getMessage());
        }
    }

    /**
     * 4. 编辑菜品（对应前端“编辑”按钮）
     * 前端请求：POST /product/updateProduct
     * 前端传递参数：{id: 1, name: "菜品名", price: 19.9, ...}（表单数据）
     */
    @PostMapping("/updateProduct")
    public R updateProduct(@RequestBody Product product) {
        try {
            boolean success = productService.updateProduct(product);
            if (success) {
                R response = new R();
                response.setMessage("编辑菜品成功");
                return response;
            }
            return R.error("编辑菜品失败");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("编辑菜品失败：" + e.getMessage());
        }
    }


    /**
     * 5. 删除菜品（对应前端“删除”按钮）
     * 前端请求：GET /product/delete/{id}
     */
    @GetMapping("/delete/{id}")
    public R deleteProduct(@PathVariable("id") Long id) {
        try {
            boolean success = productService.deleteProduct(id);
            if (success) {
                R response = new R();
                response.setMessage("删除菜品成功");
                return response;

            }
            return R.error("删除菜品失败");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("删除菜品失败：" + e.getMessage());
        }
    }
    @PostMapping("/selectallproduct")
    public R selectallproduct(@RequestBody Map<String, Object> selectwhere) throws Exception{
        List where = (List) selectwhere.get("where");

        QueryWrapper queryWrapper = commontUtil.getWhere(where);
        if (selectwhere.get("page") != null) {
            int page = Integer.parseInt(selectwhere.get("page").toString());

            PageHelper.startPage(page, 8);
        }
        List<Product> productList=productService.list(queryWrapper);
        PageInfo<Product> pageInfo = new PageInfo<>(productList);
        return new R().addData("pageInfo", pageInfo);
    }

    @PostMapping("/selectproductbycategory")
    public R selectProductbycategory(@RequestBody Map<String,Object> selectwhere) throws Exception{
        List where = (List) selectwhere.get("where");

        QueryWrapper queryWrapper = commontUtil.getWhere(where);

            int page = Integer.parseInt(selectwhere.get("page").toString());

            PageHelper.startPage(page, 8);

        List<Product> Productlist=productService.selectProductbycategory(queryWrapper);

        PageInfo pageInfo = new PageInfo<>(Productlist);

        return new R().addData("pageInfo", pageInfo);

    }
    @GetMapping("/selectproducttop10")
    public R selectproducttop10(){
        List<Product> productList=productService.selectproducttop10();
        return new R().addData("productList", productList);
    }
    
    @GetMapping("/selectallproductbyshop")
    public R selectallproductbyshop(@RequestParam("shopId") Long shopId){
        List<Product> productList=productService.selectallproductbyshop(shopId);
        return new R().addData("productList", productList);
    }
}

