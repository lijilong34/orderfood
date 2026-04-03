// 定义包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入支付宝的Goods类
import com.alipay.api.domain.Goods;
// 导入MyBatis Plus的QueryWrapper类，用于构建查询条件
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入项目通用的R类，用于统一响应格式
import org.example.orderfoodafter.common.R;
// 导入Product实体类
import org.example.orderfoodafter.entity.Product;
// 导入ShoppingCart实体类
import org.example.orderfoodafter.entity.ShoppingCart;
// 导入ProductService服务接口
import org.example.orderfoodafter.service.ProductService;
// 导入ShoppingCartService服务接口
import org.example.orderfoodafter.service.ShoppingCartService;
// 导入Spring的Autowired注解，用于自动装配
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring Data Redis的HashOperations类，用于操作Redis哈希结构
import org.springframework.data.redis.core.HashOperations;
// 导入Spring Data Redis的RedisTemplate类，用于操作Redis
import org.springframework.data.redis.core.RedisTemplate;
// 导入Spring MVC的映射注解
import org.springframework.web.bind.annotation.*;

// 导入Java集合类
import java.util.*;

/**
 * 购物车控制器
 * 负责管理用户的购物车功能，包括添加商品、查询购物车、更新商品数量、删除商品等操作
 *
 * @author 李梦瑶
 * @date 2025-11-22
 */
// 使用RequestMapping注解设置该控制器的基础请求路径为/ShopCart
@RequestMapping("/ShopCart")
// 使用RestController注解标记该类为RESTful控制器，所有方法返回JSON
@RestController
// 定义ShopCartController类，继承自BaseController基类，泛型为ShoppingCart和ShoppingCartService
/**
 * ShopCartController类
 *
 * @author 李梦瑶
 * @date 2026-03-18
 */

public class ShopCartController extends BaseController <ShoppingCart, ShoppingCartService> {
    // 使用Autowired注解自动注入ShoppingCartService服务实例
    @Autowired
    // 声明ShoppingCartService服务对象
    private ShoppingCartService shoppingCartService;

    // 使用Autowired注解自动注入RedisTemplate实例
    @Autowired
    // 声明RedisTemplate对象，用于操作Redis
    RedisTemplate<String, Object> redisTemplate;

    // 定义ShopCartController的构造函数，接收ShoppingCartService参数
/**
 * ShopCartController方法
 *
 * @author 李梦瑶
 */
    public ShopCartController (ShoppingCartService shoppingCartService) {
        // 调用父类BaseController的构造函数，传入shoppingCartService
        super(shoppingCartService);
        // 将传入的shoppingCartService赋值给当前类的shoppingCartService属性
        this.shoppingCartService = shoppingCartService;
    }

    /**
     * 添加商品到购物车
     * 作者:赵康乐
     * @param shoppingCart 购物车信息
     * @param token 用户令牌
     * @return 添加结果
     * @throws Exception
     */
    // 使用PostMapping注解映射POST请求到/addToCart路径
    @PostMapping("/addToCart")
    // 定义添加商品到购物车的方法，接收ShoppingCart对象和Authorization请求头，返回R响应对象
        /**
     * addToCart
     * 
     * @author 李梦瑶
     */
    public R addToCart(@RequestBody ShoppingCart shoppingCart, @RequestHeader("Authorization") String token) throws Exception {
        // 使用try-catch块捕获异常
        try {
            // 去掉token前缀"Bearer "
            token = token.substring(7);
            // 从token中获取用户ID
            String subject = jwtUtils.getSubject(token);
            // 将用户ID转换为Long类型
            Long userid = Long.parseLong(subject);

            // 使用时间戳代替全局变量i，避免线程安全问题
            String timestamp = String.valueOf(System.currentTimeMillis());
            // 创建HashMap对象用于存储购物车数据
            Map<String, Object> map = new HashMap<>();
            // 将商品ID和时间戳组合作为键，商品ID作为值存储到map中
            map.put("goodsid"+timestamp,String.valueOf(shoppingCart.getProductId()));
            // 将商品ID作为键，商品数量作为值存储到map中
            map.put("quantity"+shoppingCart.getProductId(),String.valueOf(shoppingCart.getQuantity()));
            // 将购物车数据批量存储到Redis的Hash结构中，键为"shop:user"+用户ID
            redisTemplate.opsForHash().putAll("shop:user"+userid,map);
            // 返回成功响应，包含添加成功状态
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("status", "添加成功");
        } catch (Exception e) {
            // 捕获到异常时，抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("添加购物车失败");
        }
    }

    // 使用Autowired注解自动注入ProductService服务实例
    @Autowired
    // 声明ProductService服务对象
    ProductService productService;

    /**
     * 查询用户购物车
     * 作者:赵康乐
     * @param token 用户令牌
     * @return 购物车列表
     */
    // 使用GetMapping注解映射GET请求到/selectshopcartbyuserid路径
    @GetMapping("/selectshopcartbyuserid")
    // 定义查询用户购物车的方法，接收Authorization请求头，返回R响应对象
        /**
     * selectShopCartByUserId
     * 
     * @author 李梦瑶
     */
    public R selectShopCartByUserId(@RequestHeader("Authorization") String token) {
        // 使用try-catch块捕获异常
        try {
            // 去掉token前缀"Bearer "
            token = token.substring(7);
            // 从token中获取用户ID
            String subject = jwtUtils.getSubject(token);
            // 将用户ID转换为整数类型
            long userid = Integer.parseInt(subject);
            // 获取Redis的Hash操作对象
            HashOperations bashOps = redisTemplate.opsForHash();
            // 从Redis中获取该用户的购物车数据
            Map<String, Object> entries = bashOps.entries("shop:user" + userid);
            // 获取购物车数据的键值对集合
            Set<Map.Entry<String, Object>> set = entries.entrySet();
            // 创建商品ID列表
            List<Integer> goodsidlist = new ArrayList<Integer>();
            // 创建商品和数量的映射列表
            List<Map<String, Integer>> goodsmaplist = new ArrayList<>();
            // 遍历购物车数据的键值对
            for (Map.Entry<String, Object> entry : set) {
                // 判断键是否以"goodsid"开头
                if (entry.getKey().startsWith("goodsid")) {
                    // 解析商品ID
                    int goodsid = Integer.parseInt(entry.getValue().toString());
                    // 解析商品数量
                    int quantity = Integer.parseInt(entries.get("quantity" + goodsid).toString());
                    // 创建HashMap存储商品ID和数量
                    Map<String, Integer> map = new HashMap<>();
                    // 将商品ID存入map
                    map.put("goodsid", goodsid);
                    // 将商品数量存入map
                    map.put("quantity", quantity);
                    // 将map添加到商品映射列表中
                    goodsmaplist.add(map);
                    // 将商品ID添加到商品ID列表中
                    goodsidlist.add(goodsid);
                }
            }
            // 创建查询条件构建器
            QueryWrapper<Product> goodsQueryWrapper = new QueryWrapper<>();
            // 设置查询条件：商品ID在商品ID列表中
            goodsQueryWrapper.in("id", goodsidlist);
            // 查询商品列表
            List<Product> productList = productService.list(goodsQueryWrapper);
            // 创建购物车列表
            List<ShoppingCart> shoppingCartList = new ArrayList<>();
            // 遍历商品列表，构建购物车数据
            for (Product product : productList) {
                // 创建购物车对象
                    /**
     * ShoppingCart
     * 
     * @author 李梦瑶
     */
                ShoppingCart shoppingCart = new ShoppingCart();
                // 创建商品对象
                    /**
     * Product
     * 
     * @author 李梦瑶
     */
                Product product1 = new Product();
                // 设置购物车的用户ID
                shoppingCart.setUserId(userid);
                // 设置购物车的商品ID
                shoppingCart.setProductId(product.getId());
                // 设置购物车的店铺ID
                shoppingCart.setShopId(product.getShopId());
                // 设置商品名称
                product1.setName(product.getName());
                // 设置商品价格
                product1.setPrice(product.getPrice());
                // 设置商品图片
                product1.setImage(product.getImage());
                // 设置商品的店铺ID
                product1.setShopId(product.getShopId());
                // 遍历商品映射列表，匹配商品数量
                for (Map<String, Integer> map : goodsmaplist) {
                    // 判断商品ID是否匹配
                    if (map.get("goodsid").intValue() == product.getId().intValue()) {
                        // 设置购物车的商品数量
                        shoppingCart.setQuantity(map.get("quantity"));
                    }
                }
                // 将商品对象设置到购物车中
                shoppingCart.setProduct(product1);
                // 将购物车对象添加到列表中
                shoppingCartList.add(shoppingCart);
            }
            // 返回成功响应，包含购物车列表
                /**
     * R
     * 
     * @author 李梦瑶
     */
            return new R().addData("shoppingCartList", shoppingCartList);
        }catch (Exception e){
            // 捕获到异常时，抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("你的购物车为空！");
        }
    }

    // 使用PostMapping注解映射POST请求到/updateShoppingCart路径
    @PostMapping("/updateShoppingCart")
    // 定义更新购物车的方法，接收Authorization请求头和Map参数，返回R响应对象
/**
 * updateShoppingCart方法
 *
 * @author 李梦瑶
 */
    public R updateShoppingCart(@RequestHeader("Authorization") String token,@RequestBody Map<String,Object> stringObjectMap) throws Exception {
        // 去掉token前缀"Bearer "
        token = token.substring(7);
        // 从token中获取用户ID
        String subject = jwtUtils.getSubject(token);
        // 将用户ID转换为Long类型
        Long userId = Long.parseLong(subject);
        // 从参数中获取商品ID
        String productid = stringObjectMap.get("id").toString();
        // 从参数中获取商品数量
        String quantity = stringObjectMap.get("quantity").toString();
        // 更新Redis中该商品的数量
        redisTemplate.opsForHash().put("shop:user" + userId, "quantity" + productid, quantity);
        // 返回成功响应，包含修改成功状态
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("status","修改成功");
    }
    // 使用GetMapping注解映射GET请求到/addShopCartfor路径
    @GetMapping("/addShopCartfor")
    // 定义将Redis中的购物车数据添加到数据库的方法，接收Authorization请求头，返回R响应对象
/**
 * addShopCartFor方法
 *
 * @author 李梦瑶
 */
    public R addShopCartFor(@RequestHeader("Authorization") String token) throws Exception {
        // 去掉token前缀"Bearer "
        token = token.substring(7);
        // 从token中获取用户ID
        String subject = jwtUtils.getSubject(token);
        // 将用户ID转换为Long类型
        long userid = Long.parseLong(subject);
        // 创建查询条件构建器
        QueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new QueryWrapper<>();
        // 设置查询条件：用户ID等于当前用户ID
        shoppingCartQueryWrapper.eq("user_id",userid);
        // 删除数据库中该用户的所有购物车记录
        boolean flag=shoppingCartService.remove(shoppingCartQueryWrapper);
        // 从Redis中获取该用户的购物车数据
        Map<Object, Object> usershopmap = redisTemplate.opsForHash().entries("shop:user" + userid);
        // 创建购物车对象
            /**
     * ShoppingCart
     * 
     * @author 李梦瑶
     */
        ShoppingCart shoppingCart = new ShoppingCart();
        // 遍历Redis中的购物车数据
        for (Map.Entry<Object, Object> entry : usershopmap.entrySet()) {
            // 判断键是否以"goodsid"开头
            if (entry.getKey().toString().startsWith("goodsid")) {
                // 设置购物车的用户ID
                shoppingCart.setUserId(userid);
                // 解析商品ID
                long goodsid=Long.parseLong(entry.getValue().toString());
                // 解析商品数量
                int quantity=Integer.parseInt(usershopmap.get("quantity"+goodsid).toString());
                // 设置购物车的商品ID
                shoppingCart.setProductId(goodsid);
                // 设置购物车的商品数量
                shoppingCart.setQuantity(quantity);
                // 将购物车记录保存到数据库
                shoppingCartService.save(shoppingCart);
            }
        }
        // 返回成功响应，包含添加成功状态
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("status","已成功添加到数据库");
    }

    // 使用GetMapping注解映射GET请求到/delShopCart/{productId}路径
    @GetMapping("/delShopCart/{productId}")
    // 定义删除购物车中商品的方法，接收Authorization请求头和路径参数productId，返回R响应对象
/**
 * delShopCart方法
 *
 * @author 李梦瑶
 */
    public R delShopCart(@RequestHeader("Authorization") String token,@PathVariable("productId") Long productId) throws Exception {
        // 去掉token前缀"Bearer "
        token = token.substring(7);
        // 从token中获取用户ID
        String subject = jwtUtils.getSubject(token);
        // 将用户ID转换为Long类型
        long userid = Long.parseLong(subject);
        // 获取Redis的Hash操作对象
        HashOperations bashOps = redisTemplate.opsForHash();
        // 从Redis中获取该用户的购物车数据
        Map<String, Object> entries = bashOps.entries("shop:user" + userid);
        // 获取购物车数据的键值对集合
        Set<Map.Entry<String, Object>> set = entries.entrySet();
        // 遍历购物车数据的键值对
        for (Map.Entry<String, Object> entry : set) {
            // 判断键是否以"goodsid"开头
            if (entry.getKey().startsWith("goodsid")) {
                // 解析商品ID
                long goodsid = Long.parseLong(entry.getValue().toString());
                // 判断商品ID是否匹配
                if (goodsid == productId) {
                    // 从Redis中删除该商品的ID记录
                    bashOps.delete("shop:user" + userid, entry.getKey());
                    // 从Redis中删除该商品的数量记录
                    bashOps.delete("shop:user" + userid, "quantity" + productId);
                }
            }
        }
        // 返回成功响应，包含删除成功状态
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("status","删除成功");
    }
}