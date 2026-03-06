package org.example.orderfoodafter.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.cj.jdbc.result.UpdatableResultSet;
import org.example.orderfoodafter.common.CommontUtil;
import org.example.orderfoodafter.common.JwtUtils;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.common.UpdateUserRedis;
import org.example.orderfoodafter.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 封装
 * @param <T>
 * @param <S>
 */
@RestController

public class BaseController<T, S extends IService> {

    /**
     * 共有的查询方法
     */


    S service;
    @Autowired
    CommontUtil commontUtil;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    UpdateUserRedis updateUserRedis;

    public BaseController(S service) {
        this.service = service;
    }

    public BaseController() {

    }

    /**
     * 检查对象是否是 User 类型
     */
    private boolean isUserType(T t) {
        return t != null && t.getClass() == User.class;
    }

    /**
     * 格式化日期为字符串（辅助方法）
     * @param date Date对象
     * @return 格式化后的日期字符串，格式为yyyy-MM-dd HH:mm:ss
     */
    protected String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
    
    /**
     * 格式化LocalDateTime为字符串（辅助方法）
     * @param dateTime LocalDateTime对象
     * @return 格式化后的日期时间字符串，格式为yyyy-MM-dd HH:mm:ss
     */
    protected String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    /**
     * 查询
     * @param selectwhere
     * @return
     * @throws Exception
     */
            @PostMapping("/select")
            public R select(@RequestBody Map<String, Object> selectwhere) throws Exception {
        
                List where = (List) selectwhere.get("where");
        
                QueryWrapper queryWrapper = commontUtil.getWhere(where);
                if (selectwhere.get("page") != null) {
            int page = Integer.parseInt(selectwhere.get("page").toString());

            PageHelper.startPage(page, 10);
        }

        List list = this.service.list(queryWrapper);

        PageInfo pageInfo = new PageInfo<>(list);
        return new R().addData("pageInfo", pageInfo);

    }

    /**
     * 增加的对象
     *
     * @param t
     * @return
     * @throws Exception
     */
    @PostMapping("/add")
    public R add(@RequestBody T t) throws Exception {
        boolean falg = this.service.save(t);
        if (isUserType(t)) {
            updateUserRedis.cheangeuserinfoconut();
        }
        if (falg) {
            return new R().addData("status", "添加成功");
        }
        throw new RuntimeException("添加失败");
    }

    //根据id删除
    @GetMapping("/remove/{id}")
    public R remove(@PathVariable("id") int id) throws Exception {
        boolean flag = this.service.removeById(id);
        if (flag) {
                updateUserRedis.cheangeuserinfoconut();
            return new R().addData("status", "删除成功");
        }
        throw new RuntimeException("删除失败");
    }

    /**
     * 修改
     * @param t
     * @return
     * @throws Exception
     */
    @PostMapping("/update")
    public R update(@RequestBody T t) throws Exception {
        boolean falg = this.service.updateById(t);
        if (falg) {
            if (isUserType(t)) {
                updateUserRedis.cheangeuserinfoconut();
            }
            return new R().addData("status", "修改成功");
        }
        throw new RuntimeException("修改失败");
    }

    /**
     * 根据id查询
     * @param id
     * @return
     * @throws Exception
     */
    @GetMapping("/selectbyid/{id}")
    public R selectbyid(@PathVariable("id") Long id) throws Exception {
        T entity = (T) this.service.getById(id);
        return new R().addData("entity", entity);
    }
    @GetMapping("/allselect")
    public R allselect() throws Exception {
        List<T> list = this.service.list();
        return new R().addData("list", list);
    }
    
    @Autowired
    private javax.sql.DataSource dataSource;
    
    /**
     * 生成 Excel 表头映射
     * @param headers 表头名称数组
     * @return Excel 单元格地址和表头名称的映射
     * @throws Exception
     */
    protected Map<String, String> generateExcelHeaders(String[] headers) throws Exception {
        Map<String, String> headerMap = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            char columnChar = (char) ('A' + i); // A, B, C...
            String cellRef = columnChar + "1"; // A1, B1, C1...
            headerMap.put(cellRef, headers[i]);
        }
        return headerMap;
    }
    
    /**
     * 根据实体类获取数据库表列备注作为 Excel 表头
     * @param entityClass 实体类
     * @return Excel 单元格地址和列备注的映射
     * @throws Exception
     */
    protected Map<String, String> generateExcelHeadersFromTable(Class<?> entityClass) throws Exception {
        String tableName = getTableNameFromEntity(entityClass);
        return getTableColumnComments(tableName);
    }
    
    /**
     * 为当前控制器对应的实体类生成 Excel 表头（最简使用方法）
     * @return Excel 单元格地址和列备注的映射
     * @throws Exception
     */
    @GetMapping("/loadbiao")
    public R generateExcelHeadersForCurrentEntity() throws Exception {
        // 获取泛型参数 T 的类型
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (typeArguments.length > 0) {
                Class<?> entityClass = (Class<?>) typeArguments[0];
                return  new R().addData("listbiao",generateExcelHeadersFromTable(entityClass)) ;
            }
        }
        throw new RuntimeException("无法获取当前控制器的实体类类型");
    }
    
    /**
     * 从实体类获取表名
     * @param entityClass 实体类
     * @return 表名
     */
    private String getTableNameFromEntity(Class<?> entityClass) {
        // 检查是否有 @TableName 注解
        if (entityClass.isAnnotationPresent(com.baomidou.mybatisplus.annotation.TableName.class)) {
            com.baomidou.mybatisplus.annotation.TableName tableName = 
                entityClass.getAnnotation(com.baomidou.mybatisplus.annotation.TableName.class);
            String value = tableName.value();
            if (value != null && !value.trim().isEmpty()) {
                // 移除反引号
                value = value.replace("`", "");
                return value;
            }
        }
        
        // 默认使用类名转下划线格式
        String className = entityClass.getSimpleName();
        return className.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
    
    /**
     * 获取表的列备注信息
     * @param tableName 表名
     * @return 列名和备注的映射
     * @throws Exception
     */
    private Map<String, String> getTableColumnComments(String tableName) throws Exception {
        Map<String, String> headerMap = new HashMap<>();
        
        System.out.println("开始获取表 '" + tableName + "' 的列备注信息");
        
        try (java.sql.Connection connection = dataSource.getConnection()) {
            // 获取数据库名称
            String databaseName = connection.getCatalog();
            System.out.println("数据库名称: " + databaseName);
            
            // 查询系统表获取列备注信息（适用于MySQL）
            String query = "SELECT COLUMN_NAME, COLUMN_COMMENT FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? ORDER BY ORDINAL_POSITION";
            System.out.println("执行SQL查询: " + query);
            System.out.println("参数: databaseName=" + databaseName + ", tableName=" + tableName);
            
            try (java.sql.PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, databaseName);
                stmt.setString(2, tableName);
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    int columnIndex = 0;
                    boolean hasResults = false;
                    
                    while (rs.next()) {
                        hasResults = true;
                        String columnName = rs.getString("COLUMN_NAME");
                        String columnComment = rs.getString("COLUMN_COMMENT");
                        
                        System.out.println("找到列: " + columnName + ", 备注: " + columnComment);
                        
                        // 如果备注为空，使用列名
                        if (columnComment == null || columnComment.trim().isEmpty()) {
                            columnComment = columnName;
                        }
                        
                        // 生成Excel列地址 (A1, B1, C1...)
                        char columnChar = (char) ('A' + columnIndex);
                        String cellRef = columnChar + "1";
                        headerMap.put(cellRef, columnComment);
                        
                        columnIndex++;
                    }
                    
                    if (!hasResults) {
                        System.out.println("警告: 没有找到表 '" + tableName + "' 的列信息");
                        // 如果查询不到列信息，尝试使用实体类字段名作为表头
                        return getEntityFieldHeaders(tableName);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("获取表列备注信息失败: " + e.getMessage());
            e.printStackTrace();
            // 发生异常时尝试使用实体类字段名作为表头
            return getEntityFieldHeaders(tableName);
        }
        
        System.out.println("成功获取 " + headerMap.size() + " 个表头");
        return headerMap;
    }
    
    /**
     * 根据表名获取实体类字段名作为表头（通用方案）
     * @param tableName 表名
     * @return 表头映射
     */
    private Map<String, String> getEntityFieldHeaders(String tableName) {
        Map<String, String> headerMap = new HashMap<>();
        
        try {
            // 尝试根据表名找到对应的实体类
            Class<?> entityClass = findEntityClassByTableName(tableName);
            if (entityClass != null) {
                System.out.println("找到实体类: " + entityClass.getName());
                
                // 获取所有字段
                java.lang.reflect.Field[] fields = entityClass.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    java.lang.reflect.Field field = fields[i];
                    
                    // 跳过序列化字段
                    if (field.isSynthetic()) continue;
                    
                    String fieldName = field.getName();
                    
                    // 生成Excel列地址 (A1, B1, C1...)
                    char columnChar = (char) ('A' + i);
                    String cellRef = columnChar + "1";
                    headerMap.put(cellRef, fieldName);
                    
                    System.out.println("使用字段名作为表头: " + fieldName + " -> " + cellRef);
                }
            } else {
                System.out.println("警告: 未找到表 '" + tableName + "' 对应的实体类");
            }
        } catch (Exception e) {
            System.err.println("获取实体类字段失败: " + e.getMessage());
        }
        
        return headerMap;
    }
    
    /**
     * 根据表名查找对应的实体类
     * @param tableName 表名
     * @return 实体类，如果找不到返回null
     */
    private Class<?> findEntityClassByTableName(String tableName) {
        try {
            // 这里需要扫描实体类包，查找有@TableName注解的类
            // 由于我们无法动态扫描，这里提供一个通用的查找方法
            
            // 常见的实体类包路径
            String[] commonPackages = {
                "org.example.orderfoodafter.entity",
                "org.example.orderfoodafter.model",
                "org.example.orderfoodafter.domain"
            };
            
            for (String packageName : commonPackages) {
                try {
                    // 尝试加载包中的所有类（简化版本）
                    // 在实际项目中，可能需要使用Spring的ClassPathScanningCandidateComponentProvider
                    java.io.File directory = new java.io.File("src/main/java/" + packageName.replace('.', '/'));
                    if (directory.exists()) {
                        java.io.File[] files = directory.listFiles((dir, name) -> name.endsWith(".java"));
                        if (files != null) {
                            for (java.io.File file : files) {
                                String className = packageName + "." + file.getName().replace(".java", "");
                                try {
                                    Class<?> clazz = Class.forName(className);
                                    if (clazz.isAnnotationPresent(com.baomidou.mybatisplus.annotation.TableName.class)) {
                                        com.baomidou.mybatisplus.annotation.TableName annotation = 
                                            clazz.getAnnotation(com.baomidou.mybatisplus.annotation.TableName.class);
                                        String annotationValue = annotation.value().replace("`", "");
                                        if (annotationValue.equalsIgnoreCase(tableName)) {
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
        } catch (Exception e) {
            System.err.println("查找实体类失败: " + e.getMessage());
        }
        
        return null;
    }
}
