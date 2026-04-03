// 定义当前类的包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;


// 导入MyBatis-Plus的查询包装器类，用于构建查询条件
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入MyBatis-Plus的服务接口，定义了通用的CRUD方法
import com.baomidou.mybatisplus.extension.service.IService;
// 导入PageHelper分页工具类，用于分页查询
import com.github.pagehelper.PageHelper;
// 导入PageInfo分页信息类，用于存储分页结果
import com.github.pagehelper.PageInfo;
// 导入MySQL JDBC的可更新结果集类
import com.mysql.cj.jdbc.result.UpdatableResultSet;
// 导入通用工具类，提供常用的工具方法
import org.example.orderfoodafter.common.CommontUtil;
// 导入JWT工具类，用于生成和验证JWT令牌
import org.example.orderfoodafter.common.JwtUtils;
// 导入统一响应类R，用于封装API响应
import org.example.orderfoodafter.common.R;
// 导入更新用户Redis工具类，用于更新用户缓存
import org.example.orderfoodafter.common.UpdateUserRedis;
// 导入用户实体类
import org.example.orderfoodafter.entity.User;
// 导入Spring的自动装配注解，用于依赖注入
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring MVC的注解集合，包括@GetMapping、@PostMapping等
import org.springframework.web.bind.annotation.*;

// 导入反射Field类，用于获取类的字段信息
import java.lang.reflect.Field;
// 导入反射参数化类型类，用于获取泛型参数类型
import java.lang.reflect.ParameterizedType;
// 导入反射Type接口，表示所有类型的公共超接口
import java.lang.reflect.Type;
// 导入SimpleDateFormat日期格式化类，用于格式化日期
import java.text.SimpleDateFormat;
// 导入LocalDateTime类，用于表示本地日期时间
import java.time.LocalDateTime;
// 导入DateTimeFormatter日期时间格式化类，用于格式化LocalDateTime
import java.time.format.DateTimeFormatter;
// 导入ArrayList集合类，用于动态数组
import java.util.ArrayList;
// 导入Date类，用于表示日期时间
import java.util.Date;
// 导入HashMap映射类，用于存储键值对
import java.util.HashMap;
// 导入List集合接口，用于列表操作
import java.util.List;
// 导入Map接口，用于映射操作
import java.util.Map;

/**
 * 基础控制器
 * 封装通用的CRUD操作，提供增删改查的基础功能，供其他控制器继承使用
 *
 * @param <T> 实体类型
 * @param <S> 服务类型
 * @author 李吉隆
 * @date 2026-03-18
 */
// 使用@RestController注解标记该类为REST控制器，所有方法返回JSON数据
@RestController

// 定义BaseController泛型类，T为实体类型，S为服务类型且继承IService

public class BaseController<T, S extends IService> {

    /**
     * 共有的查询方法
     */


    // 声明服务接口类型的成员变量
    S service;
    // 使用@Autowired注解自动注入CommontUtil工具类实例
    @Autowired
    // 声明通用工具类成员变量
    CommontUtil commontUtil;
    // 使用@Autowired注解自动注入JwtUtils工具类实例
    @Autowired
    // 声明JWT工具类成员变量
    JwtUtils jwtUtils;
    // 使用@Autowired注解自动注入UpdateUserRedis工具类实例
    @Autowired
    // 声明更新用户Redis工具类成员变量
    UpdateUserRedis updateUserRedis;

    /**
     * 带参构造函数
     * @param service 服务实例
     * @author 李吉隆
     */
    public BaseController(S service) {
        // 将传入的服务实例赋值给成员变量
        this.service = service;
    }

    /**
     * 无参构造函数
     * @author 李吉隆
     */
    public BaseController() {

    }

    /**
     * 检查对象是否是 User 类型
     * @param t 泛型对象
     * @return 是否为User类型
     * @author 李吉隆
     */
    private boolean isUserType(T t) {
        // 判断对象不为null且类型为User类
        return t != null && t.getClass() == User.class;
    }

    /**
     * 格式化日期为字符串（辅助方法）
     * @param date Date对象
     * @return 格式化后的日期字符串，格式为yyyy-MM-dd HH:mm:ss
     * @author 李吉隆
     */
    protected String formatDate(Date date) {
        // 判断日期是否为null
        if (date == null) {
            // 返回null
            return null;
        }
        // 创建SimpleDateFormat对象，指定日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 格式化日期并返回
        return sdf.format(date);
    }

    /**
     * 格式化LocalDateTime为字符串（辅助方法）
     * @param dateTime LocalDateTime对象
     * @return 格式化后的日期时间字符串，格式为yyyy-MM-dd HH:mm:ss
     * @author 李吉隆
     */
    protected String formatLocalDateTime(LocalDateTime dateTime) {
        // 判断日期时间是否为null
        if (dateTime == null) {
            // 返回null
            return null;
        }
        // 创建DateTimeFormatter对象，指定日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 格式化日期时间并返回
        return dateTime.format(formatter);
    }

    /**
     * 查询方法
     * @param selectwhere 查询条件映射
     * @return 查询结果
     * @throws Exception 异常信息
     * @author 李吉隆
     */
    // 使用@PostMapping注解映射POST请求到/select路径
    @PostMapping("/select")
    public R select(@RequestBody Map<String, Object> selectwhere) throws Exception {

                // 从映射中获取where条件列表
                List where = (List) selectwhere.get("where");

                // 调用commontUtil的getWhere方法构建查询包装器
                QueryWrapper queryWrapper = commontUtil.getWhere(where);
                // 判断是否需要分页
                if (selectwhere.get("page") != null) {
            // 获取页码并转换为整数
            int page = Integer.parseInt(selectwhere.get("page").toString());

            // 启动分页，每页10条记录
            PageHelper.startPage(page, 10);
        }

        // 调用服务层的list方法查询数据列表
        List list = this.service.list(queryWrapper);

        // 创建分页信息对象
        PageInfo pageInfo = new PageInfo<>(list);
        // 返回包含分页信息的响应
            /**
     * R
     * 
     * @author 李吉隆
     */
        return new R().addData("pageInfo", pageInfo);

    }

    /**
     * 添加方法
     * @param t 泛型实体对象
     * @return 添加结果
     * @throws Exception 异常信息
     * @author 李吉隆
     */
    // 使用@PostMapping注解映射POST请求到/add路径
    @PostMapping("/add")
    public R add(@RequestBody T t) throws Exception {
        // 调用服务层的save方法保存实体对象
        boolean falg = this.service.save(t);
        // 判断是否为User类型
        if (isUserType(t)) {
            // 更新用户缓存计数
            updateUserRedis.cheangeuserinfoconut();
        }
        // 判断保存是否成功
        if (falg) {
            // 返回成功响应
                /**
     * R
     * 
     * @author 李吉隆
     */
            return new R().addData("status", "添加成功");
        }
        // 抛出运行时异常，提示添加失败
            /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
        throw new RuntimeException("添加失败");
    }

    /**
     * 删除方法
     * @param id 要删除的ID
     * @return 删除结果
     * @throws Exception 异常信息
     * @author 李吉隆
     */
    // 使用@GetMapping注解映射GET请求到/remove/{id}路径
    @GetMapping("/remove/{id}")
    public R remove(@PathVariable("id") int id) throws Exception {
        // 调用服务层的removeById方法根据ID删除记录
        boolean flag = this.service.removeById(id);
        // 判断删除是否成功
        if (flag) {
                // 更新用户缓存计数
                updateUserRedis.cheangeuserinfoconut();
            // 返回成功响应
                /**
     * R
     * 
     * @author 李吉隆
     */
            return new R().addData("status", "删除成功");
        }
        // 抛出运行时异常，提示删除失败
            /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
        throw new RuntimeException("删除失败");
    }

    /**
     * 更新方法
     * @param t 泛型实体对象
     * @return 更新结果
     * @throws Exception 异常信息
     * @author 李吉隆
     */
    // 使用@PostMapping注解映射POST请求到/update路径
    @PostMapping("/update")
    public R update(@RequestBody T t) throws Exception {
        // 调用服务层的updateById方法更新实体对象
        boolean falg = this.service.updateById(t);
        // 判断更新是否成功
        if (falg) {
            // 判断是否为User类型
            if (isUserType(t)) {
                // 更新用户缓存计数
                updateUserRedis.cheangeuserinfoconut();
            }
            // 返回成功响应
                /**
     * R
     * 
     * @author 李吉隆
     */
            return new R().addData("status", "修改成功");
        }
        // 抛出运行时异常，提示修改失败
            /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
        throw new RuntimeException("修改失败");
    }

    /**
     * 根据ID查询方法
     * @param id 查询的ID
     * @return 查询结果
     * @throws Exception 异常信息
     * @author 李吉隆
     */
    // 使用@GetMapping注解映射GET请求到/selectbyid/{id}路径
    @GetMapping("/selectbyid/{id}")
    public R selectbyid(@PathVariable("id") Long id) throws Exception {
        // 调用服务层的getById方法根据ID查询实体对象
        T entity = (T) this.service.getById(id);
        // 返回包含实体对象的响应
            /**
     * R
     * 
     * @author 李吉隆
     */
        return new R().addData("entity", entity);
    }
    /**
     * 查询所有方法
     * @return 查询结果
     * @throws Exception 异常信息
     * @author 李吉隆
     */
    // 使用@GetMapping注解映射GET请求到/allselect路径
    @GetMapping("/allselect")
    public R allselect() throws Exception {
        // 调用服务层的list方法查询所有记录
        List<T> list = this.service.list();
        // 返回包含列表的响应
            /**
     * R
     * 
     * @author 李吉隆
     */
        return new R().addData("list", list);
    }
    
    // 使用@Autowired注解自动注入数据源
    @Autowired
    // 声明数据源成员变量
    private javax.sql.DataSource dataSource;

    /**
     * 生成 Excel 表头映射
     * @param headers 表头名称数组
     * @return Excel 单元格地址和表头名称的映射
     * @throws Exception 异常信息
     * @author 李吉隆
     */
    protected Map<String, String> generateExcelHeaders(String[] headers) throws Exception {
        // 创建映射用于存储表头
        Map<String, String> headerMap = new HashMap<>();
        // 循环遍历表头数组
        for (int i = 0; i < headers.length; i++) {
            // 计算列字符（A, B, C...）
            char columnChar = (char) ('A' + i); // A, B, C...
            // 构建单元格引用（A1, B1, C1...）
            String cellRef = columnChar + "1"; // A1, B1, C1...
            // 将单元格引用和表头名称存入映射
            headerMap.put(cellRef, headers[i]);
        }
        // 返回表头映射
        return headerMap;
    }
    
    /**
     * 根据实体类获取数据库表列备注作为 Excel 表头
     * @param entityClass 实体类
     * @return Excel 单元格地址和列备注的映射
     * @throws Exception
     */
    // 定义受保护的方法，根据实体类获取表列备注作为表头
    protected Map<String, String> generateExcelHeadersFromTable(Class<?> entityClass) throws Exception {  // 实体类
        // 调用getTableNameFromEntity方法获取表名
        String tableName = getTableNameFromEntity(entityClass);
        // 调用getTableColumnComments方法获取列备注
            /**
     * 获取 getTableColumnComments
     * 
     * @return getTableColumnComments
     * @author 李吉隆
     */
        return getTableColumnComments(tableName);
    }

    /**
     * 为当前控制器对应的实体类生成 Excel 表头（最简使用方法）
     * @return Excel 单元格地址和列备注的映射
     * @throws Exception
     */
    // 使用@GetMapping注解映射GET请求到/loadbiao路径
    @GetMapping("/loadbiao")
    // 定义生成当前实体类表头的方法，返回类型为R
        /**
     * generateExcelHeadersForCurrentEntity
     * 
     * @author 李吉隆
     */
    public R generateExcelHeadersForCurrentEntity() throws Exception {
        // 获取泛型参数 T 的类型
        Type genericSuperclass = getClass().getGenericSuperclass();
        // 判断是否为参数化类型
        if (genericSuperclass instanceof ParameterizedType) {
            // 转换为参数化类型
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            // 获取类型参数数组
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            // 判断类型参数数组是否有元素
            if (typeArguments.length > 0) {
                // 获取第一个类型参数（实体类）
                Class<?> entityClass = (Class<?>) typeArguments[0];
                // 调用generateExcelHeadersFromTable方法生成表头并返回
                    /**
     * R
     * 
     * @author 李吉隆
     */
                return  new R().addData("listbiao",generateExcelHeadersFromTable(entityClass)) ;
            }
        }
        // 抛出运行时异常，提示无法获取实体类类型
            /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
        throw new RuntimeException("无法获取当前控制器的实体类类型");
    }
    
    /**
     * 从实体类获取表名
     * @param entityClass 实体类
     * @return 表名
     */
    // 定义私有方法，从实体类获取表名
        /**
     * 获取 getTableNameFromEntity
     * 
     * @return getTableNameFromEntity
     * @author 李吉隆
     */
    private String getTableNameFromEntity(Class<?> entityClass) {  // 实体类
        // 检查是否有 @TableName 注解
        if (entityClass.isAnnotationPresent(com.baomidou.mybatisplus.annotation.TableName.class)) {
            // 获取@TableName注解
            com.baomidou.mybatisplus.annotation.TableName tableName =
                entityClass.getAnnotation(com.baomidou.mybatisplus.annotation.TableName.class);
            // 获取注解的value属性值
            String value = tableName.value();
            // 判断value是否不为null且不为空
            if (value != null && !value.trim().isEmpty()) {
                // 移除反引号
                value = value.replace("`", "");
                // 返回表名
                return value;
            }
        }

        // 默认使用类名转下划线格式
        // 获取类名
        String className = entityClass.getSimpleName();
        // 将驼峰命名转换为下划线命名并返回
        return className.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
    
    /**
     * 获取表的列备注信息
     * @param tableName 表名
     * @return 列名和备注的映射
     * @throws Exception
     */
    // 定义私有方法，获取表的列备注信息
    private Map<String, String> getTableColumnComments(String tableName) throws Exception {  // 表名
        // 创建映射用于存储表头
        Map<String, String> headerMap = new HashMap<>();

        // 输出调试信息
        System.out.println("开始获取表 '" + tableName + "' 的列备注信息");

        // 使用try-with-resources自动关闭数据库连接
        try (java.sql.Connection connection = dataSource.getConnection()) {
            // 获取数据库名称
            String databaseName = connection.getCatalog();
            // 输出数据库名称
            System.out.println("数据库名称: " + databaseName);

            // 查询系统表获取列备注信息（适用于MySQL）
            String query = "SELECT COLUMN_NAME, COLUMN_COMMENT FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? ORDER BY ORDINAL_POSITION";
            // 输出SQL查询语句
            System.out.println("执行SQL查询: " + query);
            // 输出参数
            System.out.println("参数: databaseName=" + databaseName + ", tableName=" + tableName);

            // 使用try-with-resources自动关闭预处理语句
            try (java.sql.PreparedStatement stmt = connection.prepareStatement(query)) {
                // 设置第一个参数为数据库名
                stmt.setString(1, databaseName);
                // 设置第二个参数为表名
                stmt.setString(2, tableName);
                // 使用try-with-resources自动关闭结果集
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    // 初始化列索引
                    int columnIndex = 0;
                    // 初始化是否有结果标志
                    boolean hasResults = false;

                    // 循环遍历结果集
                    while (rs.next()) {
                        // 设置有结果标志为true
                        hasResults = true;
                        // 获取列名
                        String columnName = rs.getString("COLUMN_NAME");
                        // 获取列备注
                        String columnComment = rs.getString("COLUMN_COMMENT");

                        // 输出找到的列信息
                        System.out.println("找到列: " + columnName + ", 备注: " + columnComment);

                        // 如果备注为空，使用列名
                        if (columnComment == null || columnComment.trim().isEmpty()) {
                            // 使用列名作为备注
                            columnComment = columnName;
                        }

                        // 生成Excel列地址 (A1, B1, C1...)
                        // 计算列字符
                        char columnChar = (char) ('A' + columnIndex);
                        // 构建单元格引用
                        String cellRef = columnChar + "1";
                        // 将单元格引用和备注存入映射
                        headerMap.put(cellRef, columnComment);

                        // 列索引递增
                        columnIndex++;
                    }

                    // 判断是否有结果
                    if (!hasResults) {
                        // 输出警告信息
                        System.out.println("警告: 没有找到表 '" + tableName + "' 的列信息");
                        // 如果查询不到列信息，尝试使用实体类字段名作为表头
                            /**
     * 获取 getEntityFieldHeaders
     * 
     * @return getEntityFieldHeaders
     * @author 李吉隆
     */
                        return getEntityFieldHeaders(tableName);
                    }
                }
            }
        } catch (Exception e) {  // 捕获异常
            // 输出错误信息
            System.err.println("获取表列备注信息失败: " + e.getMessage());
            // 打印异常堆栈信息
            e.printStackTrace();
            // 发生异常时尝试使用实体类字段名作为表头
                /**
     * 获取 getEntityFieldHeaders
     * 
     * @return getEntityFieldHeaders
     * @author 李吉隆
     */
            return getEntityFieldHeaders(tableName);
        }

        // 输出成功获取表头数量
        System.out.println("成功获取 " + headerMap.size() + " 个表头");
        // 返回表头映射
        return headerMap;
    }
    
    /**
     * 根据表名获取实体类字段名作为表头（通用方案）
     * @param tableName 表名
     * @return 表头映射
     */
    // 定义私有方法，根据表名获取实体类字段名作为表头
    private Map<String, String> getEntityFieldHeaders(String tableName) {  // 表名
        // 创建映射用于存储表头
        Map<String, String> headerMap = new HashMap<>();

        // 使用try-catch捕获可能的异常
        try {
            // 尝试根据表名找到对应的实体类
            Class<?> entityClass = findEntityClassByTableName(tableName);
            // 判断是否找到实体类
            if (entityClass != null) {
                // 输出找到的实体类名称
                System.out.println("找到实体类: " + entityClass.getName());

                // 获取所有字段
                java.lang.reflect.Field[] fields = entityClass.getDeclaredFields();
                // 循环遍历字段数组
                for (int i = 0; i < fields.length; i++) {
                    // 获取当前字段
                    java.lang.reflect.Field field = fields[i];

                    // 跳过序列化字段
                    if (field.isSynthetic()) continue;

                    // 获取字段名
                    String fieldName = field.getName();

                    // 生成Excel列地址 (A1, B1, C1...)
                    // 计算列字符
                    char columnChar = (char) ('A' + i);
                    // 构建单元格引用
                    String cellRef = columnChar + "1";
                    // 将单元格引用和字段名存入映射
                    headerMap.put(cellRef, fieldName);

                    // 输出使用字段名作为表头的信息
                    System.out.println("使用字段名作为表头: " + fieldName + " -> " + cellRef);
                }
            } else {
                // 输出警告信息
                System.out.println("警告: 未找到表 '" + tableName + "' 对应的实体类");
            }
        } catch (Exception e) {  // 捕获异常
            // 输出错误信息
            System.err.println("获取实体类字段失败: " + e.getMessage());
        }

        // 返回表头映射
        return headerMap;
    }
    
    /**
     * 根据表名查找对应的实体类
     * @param tableName 表名
     * @return 实体类，如果找不到返回null
     */
    // 定义私有方法，根据表名查找对应的实体类
    private Class<?> findEntityClassByTableName(String tableName) {  // 表名
        // 使用try-catch捕获可能的异常
        try {
            // 这里需要扫描实体类包，查找有@TableName注解的类
            // 由于我们无法动态扫描，这里提供一个通用的查找方法

            // 常见的实体类包路径
            String[] commonPackages = {
                "org.example.orderfoodafter.entity",
                "org.example.orderfoodafter.model",
                "org.example.orderfoodafter.domain"
            };

            // 循环遍历常见包路径
            for (String packageName : commonPackages) {
                // 使用try-catch捕获可能的异常
                try {
                    // 尝试加载包中的所有类（简化版本）
                    // 在实际项目中，可能需要使用Spring的ClassPathScanningCandidateComponentProvider
                    // 创建目录对象，指向包路径
                    java.io.File directory = new java.io.File("src/main/java/" + packageName.replace('.', '/'));
                    // 判断目录是否存在
                    if (directory.exists()) {
                        // 获取目录下所有.java文件
                        java.io.File[] files = directory.listFiles((dir, name) -> name.endsWith(".java"));
                        // 判断文件列表是否不为null
                        if (files != null) {
                            // 循环遍历文件列表
                            for (java.io.File file : files) {
                                // 构建完整类名
                                String className = packageName + "." + file.getName().replace(".java", "");
                                // 使用try-catch捕获类找不到异常
                                try {
                                    // 加载类
                                    Class<?> clazz = Class.forName(className);
                                    // 判断类是否有@TableName注解
                                    if (clazz.isAnnotationPresent(com.baomidou.mybatisplus.annotation.TableName.class)) {
                                        // 获取@TableName注解
                                        com.baomidou.mybatisplus.annotation.TableName annotation =
                                            clazz.getAnnotation(com.baomidou.mybatisplus.annotation.TableName.class);
                                        // 获取注解值并移除反引号
                                        String annotationValue = annotation.value().replace("`", "");
                                        // 判断注解值是否与表名匹配
                                        if (annotationValue.equalsIgnoreCase(tableName)) {
                                            // 返回找到的实体类
                                            return clazz;
                                        }
                                    }
                                } catch (ClassNotFoundException e) {
                                    // 忽略找不到的类
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // 忽略异常，继续尝试其他包
                }
            }
        } catch (Exception e) {  // 捕获异常
            // 输出错误信息
            System.err.println("查找实体类失败: " + e.getMessage());
        }

        // 返回null表示未找到
        return null;
    }
// BaseController类定义结束
}
