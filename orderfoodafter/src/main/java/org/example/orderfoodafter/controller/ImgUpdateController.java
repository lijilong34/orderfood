// 定义图片上传控制器的包路径
package org.example.orderfoodafter.controller;
// 导入默认应用配置类
import org.example.orderfoodafter.common.DefaultApplicationProperties;
// 导入默认属性配置类
import org.example.orderfoodafter.common.DefaulteProperties;
// 导入通用响应结果类
import org.example.orderfoodafter.common.R;
// 导入Spring的自动装配注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring的POST请求映射注解
import org.springframework.web.bind.annotation.PostMapping;
// 导入Spring的请求映射注解
import org.springframework.web.bind.annotation.RequestMapping;
// 导入Spring的请求参数注解
import org.springframework.web.bind.annotation.RequestParam;
// 导入Spring的REST控制器注解
import org.springframework.web.bind.annotation.RestController;
// 导入Spring的文件上传处理类
import org.springframework.web.multipart.MultipartFile;
// 导入Java的File类
import java.io.File;
/**
 * 图片上传控制器
 * 负责处理图片文件的上传和更新功能
 * 
 * @author 李吉隆
 * @date 2025-11-16
 */
// 标记该类为REST控制器，所有方法返回JSON格式数据
@RestController
// 定义该控制器的请求路径前缀为/imgupdate
@RequestMapping("/imgupdate")
// 定义图片上传控制器类
/**
 * ImgUpdateController类
 *
 * @author 李吉隆
 * @date 2026-03-18
 */

public class ImgUpdateController {
    // 自动注入默认属性配置类，使用依赖注入方式获取实例
    @Autowired
    private DefaulteProperties defaulteProperties;

    // 定义处理更新图片的POST请求接口，路径为/updateimage
    @PostMapping("/updateimage")
    // 定义更新图片的方法，接收文件参数，可能抛出异常
/**
 * updateImage方法
 *
 * @author 李吉隆
 */
    public R updateImage(@RequestParam("file") MultipartFile file) throws Exception{
        // 生成唯一的文件名，使用当前时间戳加上原文件名
        String  filename = System.currentTimeMillis()+file.getOriginalFilename();
        // 在控制台输出文件名，用于调试
        System.out.println("文件名: " + filename);
        // 将文件传输到指定的上传路径
            /**
     * transferTo
     * 
     * @author 李吉隆
     */
        file.transferTo(new File(defaulteProperties.getUploadfilepath()+filename));
        // 返回文件路径数据
            /**
     * R
     * 
     * @author 李吉隆
     */
        return new R().addData("imgpath",filename);
    }
}
