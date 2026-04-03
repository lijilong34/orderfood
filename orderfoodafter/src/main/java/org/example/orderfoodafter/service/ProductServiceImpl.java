package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.orderfoodafter.common.CommontUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.mapper.ProductMapper;
import org.example.orderfoodafter.mapper.ProductDetailMapper;
import org.example.orderfoodafter.entity.Product;
import org.example.orderfoodafter.entity.ProductDetail;

/**
 * 商品Service实现类
 * 实现商品相关的业务逻辑处理功能
 *
 * @author 李梦瑶
 * @date 2026-03-18
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService{
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductDetailMapper productDetailMapper;

    @Autowired
    private CommontUtil commontUtil;

    /**
     * 查询销量前五的商品
     * @return 商品列表
     * @author 李梦瑶
     */
    @Override
    public List<Product> selectproducttop5() {
        List<Product> productList=baseMapper.selectproducttop5();
        return productList;
    }

    /**
     * 根据ID查询详情：适配前端编辑功能
     * @param id 商品ID
     * @return 商品实体
     * @author 李梦瑶
     */
    @Override
    public Product getProductById(Long id) {
        // 校验ID
        if (id == null || id < 1) {
            return null;
        }
        return productMapper.selectById(id);
    }

    /**
     * 新增菜品：适配前端增加功能
     * @param product 商品实体
     * @return 是否新增成功
     * @author 李梦瑶
     */
    @Override
    public boolean addProduct(Product product) {
        // 1. 业务校验（避免无效数据）
        if (product == null || product.getName() == null || product.getPrice() == null) {
            return false;
        }
        // 2. 初始化默认值（前端未传递的字段）
        if (product.getSales() == null) {
            product.setSales(0); // 销量默认0
        }
        if (product.getStatus() == null) {
            product.setStatus((byte) 1); // 默认上架
        }
        // 3. 执行新增（MyBatis-Plus 基础方法）
        if (productMapper.insert(product) > 0) {
            // 4. 同时添加商品详情图片信息
            if (product.getImagePath1() != null || product.getImagePath2() != null || product.getImagePath3() != null) {
                ProductDetail productDetail = new ProductDetail();
                productDetail.setProductId(product.getId());
                productDetail.setImagePath1(product.getImagePath1());
                productDetail.setImagePath2(product.getImagePath2());
                productDetail.setImagePath3(product.getImagePath3());
                productDetailMapper.insert(productDetail);
            }
            return true;
        }
        return false;
    }

    /**
     * 编辑菜品：适配前端编辑功能
     * @param product 商品实体
     * @return 是否编辑成功
     * @author 李梦瑶
     */
    @Override
    public boolean updateProduct(Product product) {
        // 1. 校验ID（必须传递ID才能更新）
        if (product == null || product.getId() == null) {
            return false;
        }
        // 2. 执行更新（动态更新非空字段）
        if (productMapper.updateById(product) > 0) {
            // 3. 同时更新商品详情图片信息
            QueryWrapper<ProductDetail> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("product_id", product.getId());
            ProductDetail existingDetail = productDetailMapper.selectOne(queryWrapper);
            
            // 检查是否有图片
            boolean hasImage = false;
            if ((product.getImagePath1() != null && !product.getImagePath1().isEmpty()) ||
                (product.getImagePath2() != null && !product.getImagePath2().isEmpty()) ||
                (product.getImagePath3() != null && !product.getImagePath3().isEmpty())) {
                hasImage = true;
            }
            
            if (existingDetail != null) {
                if (hasImage) {
                    // 存在且有图片则更新
                    existingDetail.setImagePath1(product.getImagePath1());
                    existingDetail.setImagePath2(product.getImagePath2());
                    existingDetail.setImagePath3(product.getImagePath3());
                    productDetailMapper.updateById(existingDetail);
                } else {
                    // 存在但无图片则删除
                    productDetailMapper.deleteById(existingDetail.getId());
                }
            } else if (hasImage) {
                // 不存在且有图片则插入
                ProductDetail productDetail = new ProductDetail();
                productDetail.setProductId(product.getId());
                productDetail.setImagePath1(product.getImagePath1());
                productDetail.setImagePath2(product.getImagePath2());
                productDetail.setImagePath3(product.getImagePath3());
                productDetailMapper.insert(productDetail);
            }
            return true;
        }
        return false;
    }

    /**
     * 删除菜品：适配前端删除功能
     * @param id 商品ID
     * @return 是否删除成功
     * @author 李梦瑶
     */
    @Override
    public boolean deleteProduct(Long id) {
        // 1. 校验ID
        if (id == null || id < 1) {
            return false;
        }
        // 2. 执行删除
        return productMapper.deleteById(id) > 0;
    }

    /**
     * 根据查询条件查询店铺商品
     * @param queryWrapper 查询条件
     * @return 商品列表
     * @author 李梦瑶
     */
    @Override
    public List<Product> selectallproductbyshop(QueryWrapper<Product> queryWrapper) {
       List<Product> products = baseMapper.selectallproductbyshop(queryWrapper);
       return products;
    }

    /**
     * 根据店铺ID查询商品列表
     * @param shopId 店铺ID
     * @return 商品列表
     * @author 李梦瑶
     */
    @Override
    public List<Product> selectallproductbyshop(Long shopId) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shop_id", shopId);
        return productMapper.selectList(queryWrapper);
    }

    /**
     * 根据商品ID查询商品信息
     * @param productid 商品ID
     * @return 商品实体
     * @author 李梦瑶
     */
    @Override
    public Product selectproductinfobyproductid(int productid) {
        Product product=baseMapper.selectproductinfobyproductid(productid);
        return product;
    }

    /**
     * 根据分类查询商品
     * @param queryWrapper 查询条件
     * @return 商品列表
     * @author 李梦瑶
     */
    @Override
    public List<Product> selectProductbycategory(QueryWrapper queryWrapper) {
        List<Product> Productlist = baseMapper.selectProductbycategory(queryWrapper);
        return Productlist;
    }

    /**
     * 查询销量前十的商品
     * @return 商品列表
     * @author 李梦瑶
     */
    @Override
    public List<Product> selectproducttop10() {
        List<Product> productList=baseMapper.selectproducttop10();
        return productList;
    }
}
