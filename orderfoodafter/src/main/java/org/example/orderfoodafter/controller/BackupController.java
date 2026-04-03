// 定义当前类的包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;


// 导入自定义的通用响应类R，用于统一返回结果
import org.example.orderfoodafter.common.R;
// 导入备份实体类Backup，用于表示数据库备份记录
import org.example.orderfoodafter.entity.Backup;
// 导入备份数据访问接口BackupMapper，用于数据库操作
import org.example.orderfoodafter.mapper.BackupMapper;
// 导入备份服务接口BackupService，用于业务逻辑处理
import org.example.orderfoodafter.service.BackupService;
// 导入Spring的自动装配注解，用于依赖注入
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring的值注入注解，用于从配置文件读取属性
import org.springframework.beans.factory.annotation.Value;
// 导入Spring的资源接口，用于处理文件资源
import org.springframework.core.io.Resource;
// 导入URL资源类，用于创建基于URL的资源对象
import org.springframework.core.io.UrlResource;
// 导入HTTP响应头类，用于设置响应头信息
import org.springframework.http.HttpHeaders;
// 导入媒体类型类，用于指定内容类型
import org.springframework.http.MediaType;
// 导入响应实体类，用于构建HTTP响应
import org.springframework.http.ResponseEntity;
// 导入Spring MVC的注解集合，包括@GetMapping、@PostMapping等
import org.springframework.web.bind.annotation.*;
// 导入Spring的文件上传类，用于处理多部分文件上传
import org.springframework.web.multipart.MultipartFile;
// 导入缓冲读取器类，用于读取文本流
import java.io.BufferedReader;
// 导入输入流读取器类，用于将字节流转换为字符流
import java.io.InputStreamReader;
// 导入本地日期时间类，用于处理日期时间
import java.time.LocalDateTime;
// 导入日期时间格式化类，用于格式化日期时间
import java.time.format.DateTimeFormatter;
// 导入哈希映射类，用于存储键值对
import java.util.HashMap;
// 导入映射接口，Map的顶层接口
import java.util.Map;
// 导入文件类，用于文件操作
import java.io.File;
// 导入IO异常类，用于处理IO错误
import java.io.IOException;
// 导入文件工具类，用于文件操作
import java.nio.file.Files;
// 导入路径接口，用于表示文件路径
import java.nio.file.Path;
// 导入路径工具类，用于创建路径对象
import java.nio.file.Paths;
// 导入UUID类，用于生成唯一标识符
import java.util.UUID;
// 导入并发哈希映射类，用于线程安全的键值对存储
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据库备份控制器
 * 负责管理数据库的备份、恢复、导入、导出等操作，确保数据安全和可恢复性
 *
 * @author 李吉隆
 * @date 2026-02-25
 */
// 使用@RestController注解标记该类为REST控制器，所有方法返回JSON数据
@RestController
// 使用@RequestMapping注解设置该控制器的所有接口基础路径为/backup
@RequestMapping("/backup")
// 定义BackupController公共类，处理数据库备份相关的HTTP请求
/**
 * BackupController类
 *
 * @author 李吉隆
 * @date 2026-03-18
 */

public class BackupController {

    // 使用@Autowired注解自动注入BackupService服务实例
    @Autowired
    // 声明备份服务接口的私有成员变量
    private BackupService backupService;

    // 使用@Autowired注解自动注入BackupMapper数据访问接口实例
    @Autowired
    // 声明备份数据访问接口的私有成员变量
    private BackupMapper backupMapper;

    // 从配置文件读取数据库连接信息
    // 使用@Value注解从配置文件中读取spring.datasource.url属性值
    @Value("${spring.datasource.url}")
    // 声明数据库URL的私有成员变量
    private String dbUrl;

    // 使用@Value注解从配置文件中读取spring.datasource.username属性值
    @Value("${spring.datasource.username}")
    // 声明数据库用户名的私有成员变量
    private String dbUsername;

    // 使用@Value注解从配置文件中读取spring.datasource.password属性值
    @Value("${spring.datasource.password}")
    // 声明数据库密码的私有成员变量
    private String dbPassword;

    // 默认备份目录
    // 使用@Value注解从配置文件中读取backup.default-path属性值，默认为D:/backups
    @Value("${backup.default-path:D:/backups}")
    // 声明默认备份路径的私有成员变量
    private String defaultBackupPath;

    // MySQL路径配置
    // 使用@Value注解从配置文件中读取mysql.dump-path属性值，默认为MySQL 5.7的mysqldump路径
    @Value("${mysql.dump-path:C:\\Program Files\\MySQL\\MySQL Server 5.7\\bin\\mysqldump.exe}")
    // 声明MySQL dump工具路径的私有成员变量
    private String mysqlDumpPath;

    // 使用@Value注解从配置文件中读取mysql.exe-path属性值，默认为MySQL 5.7的mysql.exe路径
    @Value("${mysql.exe-path:C:\\Program Files\\MySQL\\MySQL Server 5.7\\bin\\mysql.exe}")
    // 声明MySQL执行程序路径的私有成员变量
    private String mysqlExePath;

    // 备份任务进度映射
    // 声明一个静态的并发哈希映射，用于存储备份任务的进度信息
    private static final Map<String, Integer> backupProgressMap = new ConcurrentHashMap<>();

    /**
     * 获取备份列表
     */
    // 使用@GetMapping注解映射GET请求到/backup/list路径
    @GetMapping("/list")
    // 定义获取备份列表的公共方法，返回类型为R（统一响应类）
        /**
     * 获取 getBackupList
     * 
     * @return getBackupList
     * @author 李吉隆
     */
    public R getBackupList(@RequestParam(defaultValue = "1") Integer currentPage,  // 当前页码参数，默认为1
                          @RequestParam(defaultValue = "10") Integer pageSize) {  // 每页数量参数，默认为10
        // 使用try-catch捕获可能的异常
        try {
            // 调用备份服务的getBackupList方法获取备份列表
            return backupService.getBackupList(currentPage, pageSize);
        } catch (Exception e) {  // 捕获所有异常
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含异常信息
            return R.error("获取备份列表失败: " + e.getMessage());
        }
    }

    /**
     * 删除备份
     */
    // 使用@DeleteMapping注解映射DELETE请求到/backup/delete/{backupId}路径
    @DeleteMapping("/delete/{backupId}")
    // 定义删除备份的公共方法，返回类型为R，接收路径参数backupId
/**
 * deleteBackup方法
 *
 * @author 李吉隆
 */
    public R deleteBackup(@PathVariable Long backupId) {  // 备份ID，作为路径参数传入
        // 使用try-catch捕获可能的异常
        try {
            // 获取备份记录
            // 调用backupMapper的selectById方法根据ID查询备份记录
            Backup backup = backupMapper.selectById(backupId);
            // 判断备份记录是否存在
            if (backup == null) {
                // 返回错误响应，提示备份记录不存在
                return R.error("备份记录不存在");
            }

            // 删除物理文件（可选）
            // 创建File对象，指向备份文件路径
                /**
     * File
     * 
     * @author 李吉隆
     */
            File backupFile = new File(backup.getBackupFilePath());
            // 判断备份文件是否存在
            if (backupFile.exists()) {
                // 删除物理文件
                backupFile.delete();
            }

            // 删除数据库记录
            // 调用backupMapper的deleteById方法删除数据库记录
            int result = backupMapper.deleteById(backupId);

            // 判断删除结果是否大于0
            if (result > 0) {
                // 创建成功响应对象
                R response = R.ok();
                // 设置成功消息
                response.setMessage("备份删除成功");
                // 返回成功响应
                return response;
            } else {
                // 返回错误响应，提示删除失败
                return R.error("备份删除失败");
            }
        } catch (Exception e) {  // 捕获所有异常
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含异常信息
            return R.error("删除过程中发生错误: " + e.getMessage());
        }
    }

    /**
     * 恢复备份
     */
    // 使用@PostMapping注解映射POST请求到/backup/restore/{backupId}路径
    @PostMapping("/restore/{backupId}")
    // 定义恢复备份的公共方法，返回类型为R，接收路径参数backupId
/**
 * restoreBackup方法
 *
 * @author 李吉隆
 */
    public R restoreBackup(@PathVariable Long backupId) {  // 备份ID，作为路径参数传入
        // 使用try-catch捕获可能的异常
        try {
            // 输出调试信息标题
            System.out.println("=== 恢复备份调试信息 ===");
            // 输出备份ID
            System.out.println("备份ID: " + backupId);

            // 获取备份记录
            // 调用backupMapper的selectById方法根据ID查询备份记录
            Backup backup = backupMapper.selectById(backupId);
            // 判断备份记录是否存在
            if (backup == null) {
                // 返回错误响应，提示备份记录不存在
                return R.error("备份记录不存在");
            }

            // 输出备份记录信息
            System.out.println("备份记录: " + backup);
            // 输出备份文件路径
            System.out.println("备份文件路径: " + backup.getBackupFilePath());

            // 检查备份文件是否存在
            // 创建File对象，指向备份文件路径
                /**
     * File
     * 
     * @author 李吉隆
     */
            File backupFile = new File(backup.getBackupFilePath());
            // 判断备份文件是否存在
            if (!backupFile.exists()) {
                // 返回错误响应，提示备份文件不存在
                return R.error("备份文件不存在: " + backup.getBackupFilePath());
            }

            // 提取数据库名称
            // 调用extractDbName方法从数据库URL中提取数据库名
            String dbName = extractDbName(dbUrl);

            // 构建mysql恢复命令
            // 调用getMysqlExePath方法获取MySQL执行程序路径
            String mysqlExePath = getMysqlExePath();
            // 调用buildMysqlCommand方法构建MySQL命令数组
            String[] command = buildMysqlCommand(mysqlExePath, backupFile.getAbsolutePath(), dbUsername, dbPassword, dbName);

            // 输出恢复命令
            System.out.println("恢复命令: " + String.join(" ", command));

            // 执行恢复命令
            // 调用Runtime的exec方法执行命令
            Process process = Runtime.getRuntime().exec(command);

            // 读取错误输出
            // 创建字符串构建器用于存储错误信息
                /**
     * StringBuilder
     * 
     * @author 李吉隆
     */
            StringBuilder errorOutput = new StringBuilder();
            // 使用try-with-resources自动关闭资源
                /**
     * try
     * 
     * @author 李吉隆
     */
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                // 声明行变量
                String line;
                // 循环读取错误输出流
                while ((line = reader.readLine()) != null) {
                    // 将错误信息追加到字符串构建器
                    errorOutput.append(line).append("\n");
                }
            }

            // 等待进程完成并获取退出码
            int exitCode = process.waitFor();

            // 判断退出码是否为0
            if (exitCode == 0) {
                // 创建成功响应对象
                R response = R.ok();
                // 设置成功消息
                response.setMessage("数据库恢复成功");
                // 返回成功响应
                return response;
            } else {
                // 返回错误响应，包含错误信息
                return R.error("数据库恢复失败: " + errorOutput.toString());
            }

        } catch (Exception e) {  // 捕获所有异常
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含异常信息
            return R.error("恢复过程中发生错误: " + e.getMessage());
        }
    }

    /**
     * 下载备份文件
     */
    // 使用@GetMapping注解映射GET请求到/backup/export/{backupId}路径
    @GetMapping("/export/{backupId}")
    // 定义导出备份的公共方法，返回类型为ResponseEntity<Resource>，接收路径参数backupId
/**
 * exportBackup方法
 *
 * @author 李吉隆
 */
    public ResponseEntity<Resource> exportBackup(@PathVariable Long backupId) {  // 备份ID，作为路径参数传入
        // 使用try-catch捕获可能的异常
        try {
            // 输出调试信息标题
            System.out.println("=== 下载备份文件调试信息 ===");
            // 输出备份ID
            System.out.println("备份ID: " + backupId);

            // 获取备份记录
            // 调用backupMapper的selectById方法根据ID查询备份记录
            Backup backup = backupMapper.selectById(backupId);
            // 判断备份记录是否存在
            if (backup == null) {
                // 输出错误信息
                System.out.println("错误：备份记录不存在");
                // 返回404未找到响应
                return ResponseEntity.notFound().build();
            }

            // 输出备份记录信息
            System.out.println("备份记录: " + backup);
            // 输出备份文件路径
            System.out.println("备份文件路径: " + backup.getBackupFilePath());

            // 检查备份文件是否存在
            // 创建File对象，指向备份文件路径
                /**
     * File
     * 
     * @author 李吉隆
     */
            File backupFile = new File(backup.getBackupFilePath());
            // 输出文件是否存在
            System.out.println("文件是否存在: " + backupFile.exists());
            // 输出文件绝对路径
            System.out.println("文件绝对路径: " + backupFile.getAbsolutePath());
            // 输出文件大小
            System.out.println("文件大小: " + (backupFile.exists() ? backupFile.length() + " bytes" : "N/A"));

            // 判断文件是否存在
            if (!backupFile.exists()) {
                // 返回404未找到响应
                return ResponseEntity.notFound().build();
            }

            // 创建资源
            // 根据文件URI创建URL资源对象
                /**
     * UrlResource
     * 
     * @author 李吉隆
     */
            Resource resource = new UrlResource(backupFile.toURI());

            // 判断资源是否存在或可读
            if (resource.exists() || resource.isReadable()) {
                // 设置响应头
                // 设置内容类型为二进制流
                String contentType = "application/octet-stream";
                // 设置内容处置为附件下载，并指定文件名
                String headerValue = "attachment; filename=\"" + backup.getBackupFileName() + "\"";

                // 输出内容类型
                System.out.println("准备返回文件，Content-Type: " + contentType);
                // 输出内容处置
                System.out.println("Content-Disposition: " + headerValue);

                // 构建并返回响应实体
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))  // 设置内容类型
                        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)  // 设置内容处置头
                        .body(resource);  // 设置响应体为资源对象
            } else {
                // 输出错误信息
                System.out.println("错误：文件不可读");
                // 返回404未找到响应
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {  // 捕获所有异常
            // 输出错误信息
            System.err.println("下载备份文件时发生错误:");
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回500内部服务器错误响应
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 执行MySQL备份指令并返回备份文件位置
     */
    // 使用@PostMapping注解映射POST请求到/backup/execute-mysql-backup路径
    @PostMapping("/execute-mysql-backup")
    // 定义执行MySQL备份的公共方法，返回类型为R，接收请求体参数params
        /**
     * executeMysqlBackup
     * 
     * @author 李吉隆
     */
    public R executeMysqlBackup(@RequestBody(required = false) Map<String, String> params) {  // 备份参数映射，可选
        // 使用try-catch捕获可能的异常
        try {
            // 获取备份路径，如果未指定则使用默认路径
            // 判断参数是否为空，如果不为空则获取backupPath参数，否则使用默认备份路径
            String backupPath = params != null ? params.getOrDefault("backupPath", defaultBackupPath) : defaultBackupPath;

            // 创建备份目录
            // 创建File对象，指向备份路径
                /**
     * File
     * 
     * @author 李吉隆
     */
            File backupDirectory = new File(backupPath);
            // 判断备份目录是否存在
            if (!backupDirectory.exists()) {
                // 创建备份目录
                boolean created = backupDirectory.mkdirs();
                // 判断目录是否创建成功
                if (!created) {
                    // 返回错误响应，提示无法创建备份目录
                    return R.error("无法创建备份目录，请检查权限");
                }
            }
            // 生成备份文件名（使用时间戳）
            // 获取当前时间并格式化为字符串
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            // 构建备份文件名
            String backupFileName = "orderfoot_backup_" + timestamp + ".sql";
            // 构建备份文件完整路径
            String backupFilePath = backupPath + File.separator + backupFileName;

            // 提取数据库名称
            // 调用extractDbName方法从数据库URL中提取数据库名
            String dbName = extractDbName(dbUrl);

            // 构建mysqldump命令
            // 调用getMysqlDumpPath方法获取MySQL dump工具路径
            String mysqlDumpPath = getMysqlDumpPath();

            // 输出调试信息标题
            System.out.println("=== 备份调试信息 ===");
            // 输出MySQL Dump路径
            System.out.println("MySQL Dump路径: " + mysqlDumpPath);

            // 检查MySQL dump文件是否存在
            // 创建File对象，指向MySQL dump工具路径
                /**
     * File
     * 
     * @author 李吉隆
     */
            File mysqldumpFile = new File(mysqlDumpPath);
            // 判断MySQL dump工具是否存在
            if (!mysqldumpFile.exists()) {
                // 返回错误响应，提示MySQL dump工具不存在
                return R.error("MySQL mysqldump工具不存在: " + mysqlDumpPath +
                             "。请检查MySQL是否正确安装，或修改配置文件中的mysql.dump-path路径。");
            }

            // 输出备份文件路径
            System.out.println("备份文件路径: " + backupFilePath);
            // 输出数据库名
            System.out.println("数据库名: " + dbName);

            // 使用数组方式构建命令，避免路径空格问题
            // 创建命令数组，包含MySQL dump命令及参数
            String[] command = {
                mysqlDumpPath,  // MySQL dump工具路径
                "-h", "localhost",  // 指定主机为localhost
                "-u" + dbUsername,  // 指定用户名
                "-p" + dbPassword,  // 指定密码
                "--databases", dbName,  // 指定要备份的数据库
                "--result-file=" + backupFilePath  // 指定输出文件路径
            };

            // 输出命令数组
            System.out.println("命令数组: " + java.util.Arrays.toString(command));

            // 执行备份命令
            // 调用Runtime的exec方法执行命令
            Process process = Runtime.getRuntime().exec(command);

            // 读取命令输出
            // 创建字符串构建器用于存储输出信息
                /**
     * StringBuilder
     * 
     * @author 李吉隆
     */
            StringBuilder output = new StringBuilder();
            // 使用try-with-resources自动关闭资源
                /**
     * try
     * 
     * @author 李吉隆
     */
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                // 声明行变量
                String line;
                // 循环读取输出流
                while ((line = reader.readLine()) != null) {
                    // 将输出信息追加到字符串构建器
                    output.append(line).append("\n");
                }
            }

            // 等待进程完成
            // 调用waitFor方法等待进程结束并获取退出码
            int exitCode = process.waitFor();

            // 判断退出码是否为0
            if (exitCode == 0) {
                // 备份成功
                // 创建File对象，指向备份文件路径
                    /**
     * File
     * 
     * @author 李吉隆
     */
                File backupFile = new File(backupFilePath);
                // 判断备份文件是否存在且文件大小大于0
                if (backupFile.exists() && backupFile.length() > 0) {
                    // 创建数据映射用于存储备份信息
                    Map<String, Object> data = new HashMap<>();
                    // 存储备份文件路径
                    data.put("backupFilePath", backupFilePath);
                    // 存储备份文件名
                    data.put("backupFileName", backupFileName);
                    // 存储备份文件大小
                    data.put("backupSize", backupFile.length());
                    // 存储备份时间
                    data.put("backupTime", LocalDateTime.now().toString());

                    // 将备份记录保存到数据库
                    // 调用saveBackupRecordToDatabase方法保存备份记录
                    saveBackupRecordToDatabase(backupFileName, backupFilePath, backupFile.length());

                    // 创建成功响应对象
                    R result = R.ok();
// 设置成功消息
result.setMessage("MySQL备份成功");
// 添加备份数据到响应
result.addData("backupInfo", data);
// 返回成功响应
return result;
                } else {
                    // 返回错误响应，提示备份文件生成失败或为空
                    return R.error("备份文件生成失败或为空");
                }
            } else {
                // 备份失败
                // 返回错误响应，包含输出信息
                return R.error("MySQL备份失败: " + output.toString());
            }

        } catch (Exception e) {  // 捕获所有异常
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含异常信息
            return R.error("执行MySQL备份时发生错误: " + e.getMessage());
        }
    }

    /**
     * 导入备份文件
     * @param file 上传的备份文件
     * @param importPath 指定的导入路径（可选）
     * @return 导入结果
     */
    // 使用@PostMapping注解映射POST请求到/backup/import路径
    @PostMapping("/import")
    // 定义导入备份的公共方法，返回类型为R，接收文件参数和可选的导入路径参数
        /**
     * importBackup
     * 
     * @author 李吉隆
     */
    public R importBackup(
            @RequestParam("file") MultipartFile file,  // 上传的备份文件
            @RequestParam(value = "importPath", required = false) String importPath) {  // 导入路径，可选

        // 使用try-catch捕获可能的异常
        try {
            // 验证文件
            // 判断上传文件是否为空
            if (file.isEmpty()) {
                // 返回错误响应，提示选择备份文件
                return R.error("请选择要导入的备份文件");
            }

            // 检查文件类型
            // 获取上传文件的原始文件名
            String fileName = file.getOriginalFilename();
            // 判断文件名是否为null或不以.sql结尾
            if (fileName == null || !fileName.endsWith(".sql")) {
                // 返回错误响应，提示只支持SQL文件格式
                return R.error("只支持SQL文件格式");
            }

            // 确定临时文件保存路径
            // 判断导入路径是否为null，如果不为null则使用导入路径，否则使用默认备份路径
            String tempDirPath = importPath != null ? importPath : defaultBackupPath;

            // 确保临时目录存在
            // 创建File对象，指向临时目录路径
                /**
     * File
     * 
     * @author 李吉隆
     */
            File tempDir = new File(tempDirPath);
            // 判断临时目录是否存在
            if (!tempDir.exists()) {
                // 创建临时目录
                boolean created = tempDir.mkdirs();
                // 判断目录是否创建成功
                if (!created) {
                    // 返回错误响应，提示无法创建临时目录
                    return R.error("无法创建临时目录，请检查权限");
                }
            }

            // 保存上传的文件到临时位置
            // 构建临时文件路径，使用UUID生成唯一文件名
            String tempFilePath = tempDirPath + File.separator + "temp_" + UUID.randomUUID() + ".sql";
            // 创建Path对象，指向临时文件路径
            Path tempPath = Paths.get(tempFilePath);
            // 将上传文件的字节写入临时文件
            Files.write(tempPath, file.getBytes());

            // 使用try-finally确保清理临时文件
            try {
                // 提取数据库名称
                // 调用extractDbName方法从数据库URL中提取数据库名
                String dbName = extractDbName(dbUrl);

                // 构建mysql导入命令
                // 调用getMysqlExePath方法获取MySQL执行程序路径
                String mysqlExePath = getMysqlExePath();
                // 调用buildMysqlCommand方法构建MySQL命令数组
                String[] command = buildMysqlCommand(mysqlExePath, tempFilePath, dbUsername, dbPassword, dbName);

                // 执行导入命令
                // 创建ProcessBuilder对象，用于构建进程
                    /**
     * ProcessBuilder
     * 
     * @author 李吉隆
     */
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                // 重定向错误流到输出流
                processBuilder.redirectErrorStream(true);
                // 启动进程
                Process process = processBuilder.start();

                // 读取错误输出
                // 创建字符串构建器用于存储错误信息
                    /**
     * StringBuilder
     * 
     * @author 李吉隆
     */
                StringBuilder errorOutput = new StringBuilder();
                // 使用try-with-resources自动关闭资源
                    /**
     * try
     * 
     * @author 李吉隆
     */
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    // 声明行变量
                    String line;
                    // 循环读取错误输出流
                    while ((line = reader.readLine()) != null) {
                        // 将错误信息追加到字符串构建器
                        errorOutput.append(line).append("\n");
                    }
                }

                // 等待进程完成并获取退出码
                int exitCode = process.waitFor();

                // 判断退出码是否为0
                if (exitCode == 0) {
                    // 创建成功响应对象
                    R result = R.ok();
// 设置成功消息
result.setMessage("数据库备份导入成功");
// 返回成功响应
return result;
                } else {
                    // 返回错误响应，包含错误信息
                    return R.error("数据库导入失败: " + errorOutput.toString());
                }
            } finally {
                // 清理临时文件
                // 删除临时文件（如果存在）
                Files.deleteIfExists(tempPath);
            }
        } catch (Exception e) {  // 捕获所有异常
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含异常信息
            return R.error("导入过程中发生错误: " + e.getMessage());
        }
    }

    /**
     * 从数据库URL提取数据库名称
     */
    // 定义私有方法，用于从数据库URL中提取数据库名称
        /**
     * extractDbName
     * 
     * @author 李吉隆
     */
    private String extractDbName(String url) {  // 数据库连接URL
        // 处理jdbc:mysql://localhost:3306/dbname 格式
        // 判断URL中是否包含问号（URL参数分隔符）
        if (url.contains("?")) {
            // 截取问号之前的部分，去除URL参数
            url = url.substring(0, url.indexOf("?"));
        }
        // 返回最后一个斜杠之后的部分，即数据库名称
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * 保存备份记录到数据库
     */
    // 定义私有方法，用于将备份记录保存到数据库
        /**
     * saveBackupRecordToDatabase
     * 
     * @author 李吉隆
     */
    private void saveBackupRecordToDatabase(String fileName, String filePath, long fileSize) {  // 文件名、文件路径、文件大小
        // 使用try-catch捕获可能的异常
        try {
            // 创建Backup对象
                /**
     * Backup
     * 
     * @author 李吉隆
     */
            Backup backup = new Backup();
            // 设置备份文件名
            backup.setBackupFileName(fileName);
            // 设置备份文件路径
            backup.setBackupFilePath(filePath);
            // 设置备份文件大小
            backup.setBackupSize(fileSize);
            // 设置备份时间为当前时间
            backup.setBackupTime(LocalDateTime.now());
            // 设置备份类型为手动备份
            backup.setBackupType("manual");
            // 设置备份状态为成功
            backup.setBackupStatus("success");
            // 设置备份描述
            backup.setDescription("手动数据库备份");
            // 设置创建时间为当前时间
            backup.setCreateTime(LocalDateTime.now());
            // 设置更新时间为当前时间
            backup.setUpdateTime(LocalDateTime.now());

            // 调用服务保存
            // 调用backupService的saveBackupRecord方法保存备份记录
            backupService.saveBackupRecord(backup);
            // 输出保存成功信息
            System.out.println("备份记录已保存到数据库: " + fileName);
        } catch (Exception e) {  // 捕获所有异常
            // 输出错误信息
            System.err.println("保存备份记录到数据库失败: " + e.getMessage());
            // 打印异常堆栈信息
            e.printStackTrace();
        }
    }

    /**
     * 获取MySQL dump命令路径
     */
    // 定义私有方法，用于获取MySQL dump工具路径
        /**
     * 获取 getMysqlDumpPath
     * 
     * @return getMysqlDumpPath
     * @author 李吉隆
     */
    private String getMysqlDumpPath() {
        // 返回MySQL dump工具路径
        return mysqlDumpPath;
    }

    /**
     * 获取MySQL执行程序路径
     */
    // 定义私有方法，用于获取MySQL执行程序路径
        /**
     * 获取 getMysqlExePath
     * 
     * @return getMysqlExePath
     * @author 李吉隆
     */
    private String getMysqlExePath() {
        // 返回MySQL执行程序路径
        return mysqlExePath;
    }
    
    /**
     * 构建MySQL命令（根据操作系统使用不同的命令格式）
     */
    // 定义私有方法，用于构建MySQL命令数组
        /**
     * buildMysqlCommand
     * 
     * @author 李吉隆
     */
    private String[] buildMysqlCommand(String mysqlExePath, String sqlFilePath, String dbUsername, String dbPassword, String dbName) {  // MySQL路径、SQL文件路径、用户名、密码、数据库名
        // 获取操作系统名称并转换为小写
        String os = System.getProperty("os.name").toLowerCase();

        // 判断操作系统是否为Windows
        if (os.contains("win")) {
            // Windows系统使用cmd.exe执行管道命令
            // 返回Windows命令数组
            return new String[] {
                "cmd", "/c",  // Windows命令提示符
                "type", sqlFilePath, "|", mysqlExePath,  // 使用type命令读取SQL文件并通过管道传递给MySQL
                "-h", "localhost",  // 指定主机为localhost
                "-u" + dbUsername,  // 指定用户名
                "-p" + dbPassword,  // 指定密码
                dbName  // 指定数据库名
            };
        } else {
            // Linux/Unix系统使用bash执行管道命令
            // 返回Linux/Unix命令数组
            return new String[] {
                "bash", "-c",  // Bash命令解释器
                "cat \"" + sqlFilePath + "\" | \"" + mysqlExePath + "\" -h localhost -u" + dbUsername + " -p" + dbPassword + " " + dbName  // 使用cat命令读取SQL文件并通过管道传递给MySQL
            };
        }
    }
// BackupController类定义结束
}