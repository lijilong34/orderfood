// 定义包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入项目通用的R类，用于统一响应格式
import org.example.orderfoodafter.common.R;
// 导入SystemSettings实体类
import org.example.orderfoodafter.entity.SystemSettings;
// 导入SystemSettingsService服务接口
import org.example.orderfoodafter.service.SystemSettingsService;
// 导入Spring的Autowired注解，用于自动装配
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring MVC的映射注解
import org.springframework.web.bind.annotation.*;
// 导入Spring的MultipartFile类，用于处理文件上传
import org.springframework.web.multipart.MultipartFile;

// 导入Java IO类
import java.io.File;
import java.io.IOException;
// 导入Java NIO类，用于文件操作
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
// 导入Java时间类LocalDateTime
import java.time.LocalDateTime;
// 导入Java集合类
import java.util.HashMap;
import java.util.Map;
// 导入Java UUID类，用于生成唯一标识符
import java.util.UUID;

/**
 * 系统设置控制器
 * 负责管理系统的基本设置、安全设置、备份设置等功能，包括系统参数配置、密码管理、SSL证书上传等
 *
 * @author 李吉隆
 * @date 2026-02-18
 */
// 使用RestController注解标记该类为RESTful控制器，所有方法返回JSON
@RestController
// 使用RequestMapping注解设置该控制器的基础请求路径为/system-settings
@RequestMapping("/system-settings")
// 定义SystemSettingsController类
/**
 * SystemSettingsController类
 *
 * @author 李吉隆
 * @date 2026-03-18
 */

public class SystemSettingsController {

    // 使用Autowired注解自动注入SystemSettingsService服务实例
    @Autowired
    // 声明SystemSettingsService服务对象
    private SystemSettingsService systemSettingsService;

    /**
     * 获取系统设置
     */
    // 使用GetMapping注解映射GET请求到/get路径
    @GetMapping("/get")
    // 定义获取系统设置的方法，返回R响应对象
        /**
     * 获取 getSystemSettings
     * 
     * @return getSystemSettings
     * @author 李吉隆
     */
    public R getSystemSettings() {
        // 调用service层获取系统设置
        return systemSettingsService.getSystemSettings();
    }

    /**
     * 保存基本设置
     */
    // 使用PostMapping注解映射POST请求到/save-basic路径
    @PostMapping("/save-basic")
    // 定义保存基本设置的方法，接收Map参数并返回R响应对象
        /**
     * saveBasicSettings
     * 
     * @author 李吉隆
     */
    public R saveBasicSettings(@RequestBody Map<String, Object> settings) {
        // 调用service层保存基本设置
        return systemSettingsService.saveBasicSettings(settings);
    }

    /**
     * 保存安全设置
     */
    // 使用PostMapping注解映射POST请求到/save-security路径
    @PostMapping("/save-security")
    // 定义保存安全设置的方法，接收Map参数并返回R响应对象
        /**
     * saveSecuritySettings
     * 
     * @author 李吉隆
     */
    public R saveSecuritySettings(@RequestBody Map<String, Object> settings) {
        // 调用service层保存安全设置
        return systemSettingsService.saveSecuritySettings(settings);
    }

    /**
     * 保存备份设置
     */
    // 使用PostMapping注解映射POST请求到/save-backup路径
    @PostMapping("/save-backup")
    // 定义保存备份设置的方法，接收Map参数并返回R响应对象
        /**
     * saveBackupSettings
     * 
     * @author 李吉隆
     */
    public R saveBackupSettings(@RequestBody Map<String, Object> settings) {
        // 调用service层保存备份设置
        return systemSettingsService.saveBackupSettings(settings);
    }

    /**
     * 修改当前登录用户的密码
     */
    // 使用PostMapping注解映射POST请求到/change-password路径
    @PostMapping("/change-password")
    // 定义修改密码的方法，接收Map参数和Authorization请求头，返回R响应对象
        /**
     * changeAdminPassword
     * 
     * @author 李吉隆
     */
    public R changeAdminPassword(@RequestBody Map<String, String> passwordInfo,
                                 @RequestHeader(value = "Authorization", required = false) String authorization) {
        // 从请求头中获取token
        // 判断authorization是否不为空且以"Bearer "开头
        if (authorization != null && authorization.startsWith("Bearer ")) {
            // 去掉"Bearer "前缀，获取token
            String token = authorization.substring(7);
            // 将token添加到密码信息Map中
            passwordInfo.put("token", token);
        }
        // 调用service层修改管理员密码
        return systemSettingsService.changeAdminPassword(passwordInfo);
    }

    /**
     * 上传SSL证书
     */
    // 使用PostMapping注解映射POST请求到/upload-ssl路径
    @PostMapping("/upload-ssl")
    // 定义上传SSL证书的方法，接收MultipartFile参数和uploadPath请求参数，返回R响应对象
        /**
     * uploadSSLCertificate
     * 
     * @author 李吉隆
     */
    public R uploadSSLCertificate(@RequestParam("file") MultipartFile file,
                                 @RequestParam(value = "uploadPath", required = false) String uploadPath) {
        // 使用try-catch块捕获异常
        try {
            // 验证文件
            // 判断文件是否为空
            if (file.isEmpty()) {
                // 如果文件为空，抛出运行时异常
                    /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
                throw new RuntimeException("请选择要上传的SSL证书文件");
            }

            // 检查文件类型
            // 获取文件原始名称
            String fileName = file.getOriginalFilename();
            // 判断文件名是否为空或不是.pem、.crt、.key结尾
            if (fileName == null || (!fileName.endsWith(".pem") && !fileName.endsWith(".crt") && !fileName.endsWith(".key"))) {
                // 如果文件类型不支持，抛出运行时异常
                    /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
                throw new RuntimeException("只支持.pem、.crt、.key格式的SSL证书文件");
            }

            // 确定保存路径
            // 判断uploadPath是否不为空，如果为空则使用默认路径
            String sslDirPath = uploadPath != null ? uploadPath : "D:/ssl-certificates";

            // 确保目录存在
            // 创建File对象表示SSL证书目录
                /**
     * File
     * 
     * @author 李吉隆
     */
            File sslDir = new File(sslDirPath);
            // 判断目录是否存在
            if (!sslDir.exists()) {
                // 创建目录，并判断是否创建成功
                boolean created = sslDir.mkdirs();
                // 如果创建失败，抛出运行时异常
                if (!created) {
                        /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
                    throw new RuntimeException("无法创建SSL证书目录，请检查权限");
                }
            }

            // 保存文件
            // 生成唯一的文件名，使用UUID加上原始文件名
            String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
            // 构建完整的文件路径
            String filePath = sslDirPath + File.separator + uniqueFileName;
            // 创建Path对象
            Path path = Paths.get(filePath);
            // 将文件字节数组写入到指定路径
            Files.write(path, file.getBytes());

            // 调用服务保存SSL证书信息
            return systemSettingsService.uploadSSLCertificate(fileName, filePath);

        } catch (IOException e) {
            // 捕获IO异常，抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
            throw new RuntimeException("上传SSL证书时发生IO错误: " + e.getMessage());
        } catch (Exception e) {
            // 捕获其他异常，抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
            throw new RuntimeException("上传SSL证书时发生错误: " + e.getMessage());
        }
    }

    /**
     * 删除SSL证书
     */
    // 使用DeleteMapping注解映射DELETE请求到/remove-ssl路径
    @DeleteMapping("/remove-ssl")
    // 定义删除SSL证书的方法，返回R响应对象
        /**
     * removeSSLCertificate
     * 
     * @author 李吉隆
     */
    public R removeSSLCertificate() {
        // 调用service层删除SSL证书
        return systemSettingsService.removeSSLCertificate();
    }



    /**
     * 重启系统
     */
    // 使用PostMapping注解映射POST请求到/restart路径
    @PostMapping("/restart")
    // 定义重启系统的方法，返回R响应对象
        /**
     * restartSystem
     * 
     * @author 李吉隆
     */
    public R restartSystem() {
        // 调用service层重启系统
        return systemSettingsService.restartSystem();
    }

    /**
     * 上传系统Logo
     */
    // 使用PostMapping注解映射POST请求到/upload-logo路径
    @PostMapping("/upload-logo")
    // 定义上传系统Logo的方法，接收MultipartFile参数和uploadPath请求参数，返回R响应对象
        /**
     * uploadSystemLogo
     * 
     * @author 李吉隆
     */
    public R uploadSystemLogo(@RequestParam("file") MultipartFile file,
                              @RequestParam(value = "uploadPath", required = false) String uploadPath) {
        // 使用try-catch块捕获异常
        try {
            // 验证文件
            // 判断文件是否为空
            if (file.isEmpty()) {
                // 如果文件为空，抛出运行时异常
                    /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
                throw new RuntimeException("请选择要上传的Logo文件");
            }

            // 检查文件类型
            // 获取文件原始名称
            String fileName = file.getOriginalFilename();
            // 判断文件名是否为空或不是图片格式
            if (fileName == null || (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg")
                    && !fileName.endsWith(".png") && !fileName.endsWith(".gif"))) {
                // 如果文件类型不支持，抛出运行时异常
                    /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
                throw new RuntimeException("只支持.jpg、.jpeg、.png、.gif格式的图片文件");
            }

            // 确定保存路径
            // 判断uploadPath是否不为空，如果为空则使用默认路径
            String logoDirPath = uploadPath != null ? uploadPath : "D:/system-logos";

            // 确保目录存在
            // 创建File对象表示Logo目录
                /**
     * File
     * 
     * @author 李吉隆
     */
            File logoDir = new File(logoDirPath);
            // 判断目录是否存在
            if (!logoDir.exists()) {
                // 创建目录，并判断是否创建成功
                boolean created = logoDir.mkdirs();
                // 如果创建失败，抛出运行时异常
                if (!created) {
                        /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
                    throw new RuntimeException("无法创建Logo目录，请检查权限");
                }
            }

            // 保存文件
            // 生成唯一的文件名，使用"system_logo_"前缀加上UUID和原始文件名
            String uniqueFileName = "system_logo_" + UUID.randomUUID().toString() + "_" + fileName;
            // 构建完整的文件路径
            String filePath = logoDirPath + File.separator + uniqueFileName;
            // 创建Path对象
            Path path = Paths.get(filePath);
            // 将文件字节数组写入到指定路径
            Files.write(path, file.getBytes());

            // 构造访问URL（实际项目中应该是可访问的URL）
            String logoUrl = "/api/system-logo/" + uniqueFileName;

            // 更新系统设置中的Logo URL
            // 创建Map对象存储Logo设置
            Map<String, Object> logoSetting = new HashMap<>();
            // 将Logo URL添加到设置中
            logoSetting.put("logoUrl", logoUrl);

            // 调用service层保存基本设置
            return systemSettingsService.saveBasicSettings(logoSetting);

        } catch (IOException e) {
            // 捕获IO异常，抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
            throw new RuntimeException("上传Logo时发生IO错误: " + e.getMessage());
        } catch (Exception e) {
            // 捕获其他异常，抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
            throw new RuntimeException("上传Logo时发生错误: " + e.getMessage());
        }
    }

    /**
     * 获取密码强度
     */
    // 使用PostMapping注解映射POST请求到/password-strength路径
    @PostMapping("/password-strength")
    // 定义计算密码强度的方法，接收Map参数并返回R响应对象
        /**
     * calculatePasswordStrength
     * 
     * @author 李吉隆
     */
    public R calculatePasswordStrength(@RequestBody Map<String, String> request) {
        // 使用try-catch块捕获异常
        try {
            // 获取密码
            String password = request.get("password");

            // 判断密码是否为空
            if (password == null || password.isEmpty()) {
                // 如果密码为空，抛出运行时异常
                    /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
                throw new RuntimeException("密码不能为空");
            }

            // 初始化密码强度分数
            int strength = 0;

            // 长度检测
            // 判断密码长度是否大于等于8
            if (password.length() >= 8) strength += 25;
            // 判断密码长度是否大于等于12
            if (password.length() >= 12) strength += 15;

            // 字符类型检测
            // 判断是否包含小写字母
            if (password.matches(".*[a-z].*")) strength += 15;
            // 判断是否包含大写字母
            if (password.matches(".*[A-Z].*")) strength += 15;
            // 判断是否包含数字
            if (password.matches(".*[0-9].*")) strength += 15;
            // 判断是否包含特殊字符
            if (password.matches(".*[^a-zA-Z0-9].*")) strength += 15;

            // 限制最大强度为100
            strength = Math.min(strength, 100);

            // 定义密码强度文本
            String strengthText;
            // 定义密码强度颜色
            String strengthColor;

            // 根据强度设置文本和颜色
            // 判断强度是否小于40
            if (strength < 40) {
                // 设置为弱
                strengthText = "弱";
                // 设置为红色
                strengthColor = "#f56c6c";
            } else if (strength < 70) {
                // 设置为中等
                strengthText = "中等";
                // 设置为橙色
                strengthColor = "#e6a23c";
            } else {
                // 设置为强
                strengthText = "强";
                // 设置为绿色
                strengthColor = "#67c23a";
            }

            // 创建结果Map
            Map<String, Object> result = new HashMap<>();
            // 将强度分数添加到结果中
            result.put("strength", strength);
            // 将强度文本添加到结果中
            result.put("strengthText", strengthText);
            // 将强度颜色添加到结果中
            result.put("strengthColor", strengthColor);

            // 返回成功响应，包含密码强度信息
                /**
     * R
     * 
     * @author 李吉隆
     */
            return new R().addData("passwordStrength", result);

        } catch (Exception e) {
            // 捕获异常，抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
            throw new RuntimeException("计算密码强度时发生错误: " + e.getMessage());
        }
    }
}