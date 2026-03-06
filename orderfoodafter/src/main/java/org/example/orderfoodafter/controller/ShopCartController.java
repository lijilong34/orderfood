package org.example.orderfoodafter.controller;

import com.alipay.api.domain.Goods;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.Product;
import org.example.orderfoodafter.entity.ShoppingCart;
import org.example.orderfoodafter.service.ProductService;
import org.example.orderfoodafter.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("/ShopCart")
@RestController
public class ShopCartController extends BaseController <ShoppingCart, ShoppingCartService> {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public ShopCartController (ShoppingCartService shoppingCartService) {
        super(shoppingCartService);
        this.shoppingCartService = shoppingCartService;
    }

    int i=0;
    @PostMapping("/addToCart")
    public R addToCart(@RequestBody ShoppingCart shoppingCart, @RequestHeader("Authorization") String token) throws Exception {
        ++i;
        token = token.substring(7);
        String subject = jwtUtils.getSubject(token);
        Long userid = Long.parseLong(subject);
        Map<String, Object> map = new HashMap<>();
        map.put("goodsid"+i,String.valueOf(shoppingCart.getProductId()));
        map.put("quantity"+shoppingCart.getProductId(),String.valueOf(shoppingCart.getQuantity()));
        redisTemplate.opsForHash().putAll("shop:user"+userid,map);
//        boolean flag = this.service.save(shoppingCart);
            return new R().addData("status", "添加成功");

    }
    @Autowired
    ProductService productService;

    @GetMapping("/selectshopcartbyuserid")
    public R selectShopCartByUserId(@RequestHeader("Authorization") String token) {
        try {
            token = token.substring(7);
            String subject = jwtUtils.getSubject(token);
            long userid = Integer.parseInt(subject);
            HashOperations bashOps = redisTemplate.opsForHash();
            Map<String, Object> entries = bashOps.entries("shop:user" + userid);
            Set<Map.Entry<String, Object>> set = entries.entrySet();
            List<Integer> goodsidlist = new ArrayList<Integer>();
            List<Map<String, Integer>> goodsmaplist = new ArrayList<>();
            for (Map.Entry<String, Object> entry : set) {
                if (entry.getKey().startsWith("goodsid")) {
                    int goodsid = Integer.parseInt(entry.getValue().toString());
                    int quantity = Integer.parseInt(entries.get("quantity" + goodsid).toString());
                    Map<String, Integer> map = new HashMap<>();
                    map.put("goodsid", goodsid);
                    map.put("quantity", quantity);
                    goodsmaplist.add(map);
                    goodsidlist.add(goodsid);
                }
            }
            QueryWrapper<Product> goodsQueryWrapper = new QueryWrapper<>();
            goodsQueryWrapper.in("id", goodsidlist);
            List<Product> productList = productService.list(goodsQueryWrapper);
            List<ShoppingCart> shoppingCartList = new ArrayList<>();
            for (Product product : productList) {
                ShoppingCart shoppingCart = new ShoppingCart();
                Product product1 = new Product();
                shoppingCart.setUserId(userid);
                shoppingCart.setProductId(product.getId());
                shoppingCart.setShopId(product.getShopId());  // 添加店铺ID
                product1.setName(product.getName());
                product1.setPrice(product.getPrice());
                product1.setImage(product.getImage());
                product1.setShopId(product.getShopId());  // Product对象也要设置shopId
                for (Map<String, Integer> map : goodsmaplist) {
                    if (map.get("goodsid").intValue() == product.getId().intValue()) {
                        shoppingCart.setQuantity(map.get("quantity"));
                    }
                }
                shoppingCart.setProduct(product1);
                shoppingCartList.add(shoppingCart);
            }
            return new R().addData("shoppingCartList", shoppingCartList);
        }catch (Exception e){
            throw new RuntimeException("你的购物车为空！");
        }
    }

    @PostMapping("/updateShoppingCart")
    public R updateShoppingCart(@RequestHeader("Authorization") String token,@RequestBody Map<String,Object> stringObjectMap) throws Exception {
        token = token.substring(7);
        String subject = jwtUtils.getSubject(token);
        Long userId = Long.parseLong(subject);
        String productid = stringObjectMap.get("id").toString();
        String quantity = stringObjectMap.get("quantity").toString();
        // 更新对应的数量
        redisTemplate.opsForHash().put("shop:user" + userId, "quantity" + productid, quantity);
        return new R().addData("status","修改成功");
    }
    @GetMapping("/addShopCartfor")
    public R addShopCartFor(@RequestHeader("Authorization") String token) throws Exception {
        token = token.substring(7);
        String subject = jwtUtils.getSubject(token);
        long userid = Long.parseLong(subject);
        QueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new QueryWrapper<>();
        shoppingCartQueryWrapper.eq("user_id",userid);
        boolean flag=shoppingCartService.remove(shoppingCartQueryWrapper);
        Map<Object, Object> usershopmap = redisTemplate.opsForHash().entries("shop:user" + userid);
        ShoppingCart shoppingCart = new ShoppingCart();
        for (Map.Entry<Object, Object> entry : usershopmap.entrySet()) {
            if (entry.getKey().toString().startsWith("goodsid")) {
                shoppingCart.setUserId(userid);
                long goodsid=Long.parseLong(entry.getValue().toString());
                int quantity=Integer.parseInt(usershopmap.get("quantity"+goodsid).toString());
                shoppingCart.setProductId(goodsid);
                shoppingCart.setQuantity(quantity);
                shoppingCartService.save(shoppingCart);
            }
        }
        return new R().addData("status","已成功添加到数据库");
    }

    @GetMapping("/delShopCart/{productId}")
    public R delShopCart(@RequestHeader("Authorization") String token,@PathVariable("productId") Long productId) throws Exception {
        token = token.substring(7);
        String subject = jwtUtils.getSubject(token);
        long userid = Long.parseLong(subject);
        HashOperations bashOps = redisTemplate.opsForHash();
        Map<String, Object> entries = bashOps.entries("shop:user" + userid);
        Set<Map.Entry<String, Object>> set = entries.entrySet();
        for (Map.Entry<String, Object> entry : set) {
            if (entry.getKey().startsWith("goodsid")) {
                long goodsid = Long.parseLong(entry.getValue().toString());
                if (goodsid == productId) {
                    bashOps.delete("shop:user" + userid, entry.getKey());
                    bashOps.delete("shop:user" + userid, "quantity" + productId);
                }
            }
        }
        return new R().addData("status","删除成功");
    }
}
