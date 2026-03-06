package org.example.orderfoodafter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName(value = "product_detail")
public class ProductDetail {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 商品id
     */
    @TableField(value = "product_id")
    private Long productId;

    /**
     * 图片1
     */
    @TableField(value = "image_path1")
    private String imagePath1;

    /**
     * 图片2
     */
    @TableField(value = "image_path2")
    private String imagePath2;

    /**
     * 图片3
     */
    @TableField(value = "image_path3")
    private String imagePath3;

    /**
     * 获取id
     *
     * @return id - id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置id
     *
     * @param id id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取商品id
     *
     * @return product_id - 商品id
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * 设置商品id
     *
     * @param productId 商品id
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * 获取图片1
     *
     * @return image_path1 - 图片1
     */
    public String getImagePath1() {
        return imagePath1;
    }

    /**
     * 设置图片1
     *
     * @param imagePath1 图片1
     */
    public void setImagePath1(String imagePath1) {
        this.imagePath1 = imagePath1;
    }

    /**
     * 获取图片2
     *
     * @return image_path2 - 图片2
     */
    public String getImagePath2() {
        return imagePath2;
    }

    /**
     * 设置图片2
     *
     * @param imagePath2 图片2
     */
    public void setImagePath2(String imagePath2) {
        this.imagePath2 = imagePath2;
    }

    /**
     * 获取图片3
     *
     * @return image_path3 - 图片3
     */
    public String getImagePath3() {
        return imagePath3;
    }

    /**
     * 设置图片3
     *
     * @param imagePath3 图片3
     */
    public void setImagePath3(String imagePath3) {
        this.imagePath3 = imagePath3;
    }
}