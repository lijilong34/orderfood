// 定义包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入MyBatis Plus的QueryWrapper类，用于构建查询条件
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入PageHelper的PageHelper类，用于分页
import com.github.pagehelper.PageHelper;
// 导入PageHelper的PageInfo类，用于分页信息封装
import com.github.pagehelper.PageInfo;
// 导入项目通用的R类，用于统一响应格式
import org.example.orderfoodafter.common.R;
// 导入Supplier实体类
import org.example.orderfoodafter.entity.Supplier;
// 导入SupplierService服务接口
import org.example.orderfoodafter.service.SupplierService;
// 导入Spring的Autowired注解，用于自动装配
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring MVC的映射注解
import org.springframework.web.bind.annotation.*;

// 导入Java日期类Date
import java.util.Date;
// 导入Java集合类List
import java.util.List;
// 导入Java集合类Map
import java.util.Map;

/**
 * 供应商管理控制器
 * 负责管理供应商信息，包括供应商的创建、查询、更新、删除、启用、禁用等功能
 *
 * @author 陈逸磊
 * @date 2026-01-18
 */
// 使用RestController注解标记该类为RESTful控制器，所有方法返回JSON
@RestController
// 使用RequestMapping注解设置该控制器的基础请求路径为/supplier
@RequestMapping("/supplier")
// 定义SupplierController类，继承自BaseController基类，泛型为Supplier和SupplierService
/**
 * SupplierController类
 *
 * @author 熊杨博
 * @date 2026-03-18
 */

public class SupplierController extends BaseController<Supplier, SupplierService> {

    // 使用Autowired注解自动注入SupplierService服务实例，并通过构造函数注入
    @Autowired
    // 定义SupplierController的构造函数，接收SupplierService参数
/**
 * SupplierController方法
 *
 * @author 熊杨博
 */
    public SupplierController(SupplierService service) {
        // 调用父类BaseController的构造函数，传入service
        super(service);
    }

    /**
     * 店铺管理员获取供应商列表（分页）
     */
    // 使用PostMapping注解映射POST请求到/shopadmin/list路径
    @PostMapping("/shopadmin/list")
    // 定义获取供应商列表的方法，接收Map参数并返回R响应对象
        /**
     * 获取 getShopAdminSupplierList
     * 
     * @return getShopAdminSupplierList
     * @author 李吉隆
     */
    public R getShopAdminSupplierList(@RequestBody Map<String, Object> params) {
        // 使用try-catch块捕获异常
        try {
            // 从参数中获取页码，默认为1
            int page = params.containsKey("page") ? Integer.parseInt(params.get("page").toString()) : 1;
            // 从参数中获取每页大小，默认为10
            int pageSize = params.containsKey("pageSize") ? Integer.parseInt(params.get("pageSize").toString()) : 10;
            // 从参数中获取店铺ID
            Long shopId = params.containsKey("shopId") ? Long.parseLong(params.get("shopId").toString()) : null;

            // 设置分页参数
            PageHelper.startPage(page, pageSize);

            // 创建查询条件构建器
            QueryWrapper<Supplier> queryWrapper = new QueryWrapper<>();

            // 根据店铺ID筛选
            // 判断店铺ID是否不为空
            if (shopId != null) {
                // 设置查询条件：shop_id等于传入的shopId
                queryWrapper.eq("shop_id", shopId);
            }

            // 搜索关键词（供应商名称、联系人、联系电话）
            // 判断参数中是否包含keyword且不为空
            if (params.containsKey("keyword") && params.get("keyword") != null) {
                // 获取keyword参数并去除空格
                String keyword = params.get("keyword").toString().trim();
                // 判断keyword是否不为空
                if (!keyword.isEmpty()) {
                    // 设置查询条件：使用OR逻辑，匹配名称、联系人或联系电话
                    queryWrapper.and(wrapper ->
                        wrapper.like("name", keyword)
                               .or()
                               .like("contact_person", keyword)
                               .or()
                               .like("contact_phone", keyword)
                    );
                }
            }

            // 状态筛选
            // 判断参数中是否包含status且不为空
            if (params.containsKey("status") && params.get("status") != null) {
                // 获取status参数的字符串值
                String statusStr = params.get("status").toString();
                // 判断statusStr是否不为空
                if (!statusStr.isEmpty()) {
                    // 设置查询条件：status等于传入的status
                    queryWrapper.eq("status", Integer.parseInt(statusStr));
                }
            }

            // 按创建时间倒序排列
            queryWrapper.orderByDesc("create_time");

            // 查询供应商列表
            List<Supplier> supplierList = service.list(queryWrapper);
            // 创建分页信息对象
            PageInfo<Supplier> pageInfo = new PageInfo<>(supplierList);

            // 返回成功响应，包含分页信息
                /**
     * R
     * 
     * @author 李吉隆
     */
            return new R().addData("pageInfo", pageInfo);
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("获取供应商列表失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员创建供应商
     */
    // 使用PostMapping注解映射POST请求到/shopadmin/create路径
    @PostMapping("/shopadmin/create")
    // 定义创建供应商的方法，接收Map参数并返回R响应对象
        /**
     * createSupplier
     * 
     * @author 李吉隆
     */
    public R createSupplier(@RequestBody Map<String, Object> params) {
        // 使用try-catch块捕获异常
        try {
            // 创建新的Supplier对象实例
                /**
     * Supplier
     * 
     * @author 李吉隆
     */
            Supplier supplier = new Supplier();

            // 验证必填字段：供应商名称
            if (!params.containsKey("name") || params.get("name") == null || params.get("name").toString().trim().isEmpty()) {
                // 如果供应商名称为空，返回错误响应
                return R.error("供应商名称不能为空");
            }
            // 验证必填字段：联系人
            if (!params.containsKey("contactPerson") || params.get("contactPerson") == null || params.get("contactPerson").toString().trim().isEmpty()) {
                // 如果联系人为空，返回错误响应
                return R.error("联系人不能为空");
            }
            // 验证必填字段：联系电话
            if (!params.containsKey("contactPhone") || params.get("contactPhone") == null || params.get("contactPhone").toString().trim().isEmpty()) {
                // 如果联系电话为空，返回错误响应
                return R.error("联系电话不能为空");
            }
            // 验证必填字段：店铺ID
            if (!params.containsKey("shopId") || params.get("shopId") == null) {
                // 如果店铺ID为空，返回错误响应
                return R.error("店铺ID不能为空");
            }

            // 设置供应商名称
            supplier.setName(params.get("name").toString().trim());
            // 设置联系人
            supplier.setContactPerson(params.get("contactPerson").toString().trim());
            // 设置联系电话
            supplier.setContactPhone(params.get("contactPhone").toString().trim());

            // 设置地址（可选）
            // 判断参数中是否包含address且不为空
            if (params.containsKey("address") && params.get("address") != null) {
                // 设置供应商地址
                supplier.setAddress(params.get("address").toString().trim());
            }

            // 设置店铺ID
            Long shopId = Long.parseLong(params.get("shopId").toString());
            supplier.setShopId(shopId);

            // 默认状态为1（正常合作）
            supplier.setStatus((byte) 1);

            // 设置创建时间为当前时间
                /**
     * 设置 setCreateTime
     * 
     * @param setCreateTime setCreateTime
     * @author 李吉隆
     */
            supplier.setCreateTime(new Date());
            // 设置更新时间为当前时间
                /**
     * 设置 setUpdateTime
     * 
     * @param setUpdateTime setUpdateTime
     * @author 李吉隆
     */
            supplier.setUpdateTime(new Date());

            // 调用service层保存供应商信息
            boolean result = service.save(supplier);

            // 判断保存是否成功
            if (result) {
                // 保存成功，返回成功响应，包含供应商信息和成功消息
                    /**
     * R
     * 
     * @author 李吉隆
     */
                return new R().addData("supplier", supplier).addData("message", "供应商创建成功");
            } else {
                // 保存失败，返回错误响应
                return R.error("供应商创建失败");
            }
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("供应商创建失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员更新供应商
     */
    // 使用PutMapping注解映射PUT请求到/shopadmin/update路径
    @PutMapping("/shopadmin/update")
    // 定义更新供应商的方法，接收Map参数并返回R响应对象
        /**
     * updateSupplier
     * 
     * @author 李吉隆
     */
    public R updateSupplier(@RequestBody Map<String, Object> params) {
        // 使用try-catch块捕获异常
        try {
            // 从参数中获取供应商ID
            Long id = Long.parseLong(params.get("id").toString());

            // 根据ID查询供应商信息
            Supplier supplier = service.getById(id);
            // 判断供应商是否存在
            if (supplier == null) {
                // 如果供应商不存在，返回错误响应
                return R.error("供应商不存在");
            }

            // 更新字段：供应商名称
            // 判断参数中是否包含name
            if (params.containsKey("name")) {
                // 设置供应商名称
                supplier.setName(params.get("name").toString().trim());
            }
            // 更新字段：联系人
            // 判断参数中是否包含contactPerson
            if (params.containsKey("contactPerson")) {
                // 设置联系人
                supplier.setContactPerson(params.get("contactPerson").toString().trim());
            }
            // 更新字段：联系电话
            // 判断参数中是否包含contactPhone
            if (params.containsKey("contactPhone")) {
                // 设置联系电话
                supplier.setContactPhone(params.get("contactPhone").toString().trim());
            }
            // 更新字段：地址
            // 判断参数中是否包含address
            if (params.containsKey("address")) {
                // 设置地址
                supplier.setAddress(params.get("address").toString().trim());
            }
            // 更新字段：状态
            // 判断参数中是否包含status
            if (params.containsKey("status")) {
                // 设置状态
                supplier.setStatus(Byte.parseByte(params.get("status").toString()));
            }

            // 设置更新时间为当前时间
                /**
     * 设置 setUpdateTime
     * 
     * @param setUpdateTime setUpdateTime
     * @author 李吉隆
     */
            supplier.setUpdateTime(new Date());

            // 调用service层更新供应商信息
            boolean result = service.updateById(supplier);

            // 判断更新是否成功
            if (result) {
                // 更新成功，返回成功响应，包含供应商信息和成功消息
                    /**
     * R
     * 
     * @author 李吉隆
     */
                return new R().addData("supplier", supplier).addData("message", "供应商更新成功");
            } else {
                // 更新失败，返回错误响应
                return R.error("供应商更新失败");
            }
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("供应商更新失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员删除供应商
     */
    // 使用DeleteMapping注解映射DELETE请求到/shopadmin/delete/{id}路径
    @DeleteMapping("/shopadmin/delete/{id}")
    // 定义删除供应商的方法，接收路径参数id，返回R响应对象
/**
 * deleteSupplier方法
 *
 * @author 熊杨博
 */
    public R deleteSupplier(@PathVariable Long id) {
        // 使用try-catch块捕获异常
        try {
            // 根据ID查询供应商信息
            Supplier supplier = service.getById(id);
            // 判断供应商是否存在
            if (supplier == null) {
                // 如果供应商不存在，返回错误响应
                return R.error("供应商不存在");
            }

            // 调用service层删除供应商
            boolean result = service.removeById(id);

            // 判断删除是否成功
            if (result) {
                // 删除成功，返回成功响应，包含成功消息
                    /**
     * R
     * 
     * @author 李吉隆
     */
                return new R().addData("message", "供应商删除成功");
            } else {
                // 删除失败，返回错误响应
                return R.error("供应商删除失败");
            }
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("供应商删除失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员获取供应商详情
     */
    // 使用GetMapping注解映射GET请求到/shopadmin/detail/{id}路径
    @GetMapping("/shopadmin/detail/{id}")
    // 定义获取供应商详情的方法，接收路径参数id，返回R响应对象
/**
 * getSupplierDetail方法
 *
 * @author 熊杨博
 */
    public R getSupplierDetail(@PathVariable Long id) {
        // 使用try-catch块捕获异常
        try {
            // 根据ID查询供应商信息
            Supplier supplier = service.getById(id);
            // 判断供应商是否存在
            if (supplier == null) {
                // 如果供应商不存在，返回错误响应
                return R.error("供应商不存在");
            }

            // 返回成功响应，包含供应商信息
                /**
     * R
     * 
     * @author 李吉隆
     */
            return new R().addData("supplier", supplier);
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("获取供应商详情失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员启用供应商
     */
    // 使用PostMapping注解映射POST请求到/shopadmin/enable/{id}路径
    @PostMapping("/shopadmin/enable/{id}")
    // 定义启用供应商的方法，接收路径参数id，返回R响应对象
/**
 * enableSupplier方法
 *
 * @author 熊杨博
 */
    public R enableSupplier(@PathVariable Long id) {
        // 使用try-catch块捕获异常
        try {
            // 根据ID查询供应商信息
            Supplier supplier = service.getById(id);
            // 判断供应商是否存在
            if (supplier == null) {
                // 如果供应商不存在，返回错误响应
                return R.error("供应商不存在");
            }

            // 设置供应商状态为1（正常）
            supplier.setStatus((byte) 1);
            // 设置更新时间为当前时间
                /**
     * 设置 setUpdateTime
     * 
     * @param setUpdateTime setUpdateTime
     * @author 李吉隆
     */
            supplier.setUpdateTime(new Date());

            // 调用service层更新供应商信息
            boolean result = service.updateById(supplier);

            // 判断更新是否成功
            if (result) {
                // 更新成功，返回成功响应，包含成功消息
                    /**
     * R
     * 
     * @author 李吉隆
     */
                return new R().addData("message", "供应商启用成功");
            } else {
                // 更新失败，返回错误响应
                return R.error("供应商启用失败");
            }
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("供应商启用失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员禁用供应商
     */
    // 使用PostMapping注解映射POST请求到/shopadmin/disable/{id}路径
    @PostMapping("/shopadmin/disable/{id}")
    // 定义禁用供应商的方法，接收路径参数id，返回R响应对象
/**
 * disableSupplier方法
 *
 * @author 熊杨博
 */
    public R disableSupplier(@PathVariable Long id) {
        // 使用try-catch块捕获异常
        try {
            // 根据ID查询供应商信息
            Supplier supplier = service.getById(id);
            // 判断供应商是否存在
            if (supplier == null) {
                // 如果供应商不存在，返回错误响应
                return R.error("供应商不存在");
            }

            // 设置供应商状态为0（终止）
            supplier.setStatus((byte) 0);
            // 设置更新时间为当前时间
                /**
     * 设置 setUpdateTime
     * 
     * @param setUpdateTime setUpdateTime
     * @author 李吉隆
     */
            supplier.setUpdateTime(new Date());

            // 调用service层更新供应商信息
            boolean result = service.updateById(supplier);

            // 判断更新是否成功
            if (result) {
                // 更新成功，返回成功响应，包含成功消息
                    /**
     * R
     * 
     * @author 李吉隆
     */
                return new R().addData("message", "供应商禁用成功");
            } else {
                // 更新失败，返回错误响应
                return R.error("供应商禁用失败");
            }
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("供应商禁用失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员获取供应商下拉列表
     */
    // 使用GetMapping注解映射GET请求到/shopadmin/options路径
    @GetMapping("/shopadmin/options")
    // 定义获取供应商下拉列表的方法，接收请求参数shopId，返回R响应对象
        /**
     * 获取 getSupplierOptions
     * 
     * @return getSupplierOptions
     * @author 李吉隆
     */
    public R getSupplierOptions(@RequestParam Long shopId) {
        // 使用try-catch块捕获异常
        try {
            // 创建查询条件构建器
            QueryWrapper<Supplier> queryWrapper = new QueryWrapper<>();
            // 设置查询条件：shop_id等于传入的shopId
            queryWrapper.eq("shop_id", shopId);
            // 设置查询条件：status等于1（只获取正常状态的供应商）
            queryWrapper.eq("status", 1);
            // 按名称升序排列
            queryWrapper.orderByAsc("name");

            // 查询供应商列表
            List<Supplier> supplierList = service.list(queryWrapper);

            // 返回成功响应，包含供应商列表
                /**
     * R
     * 
     * @author 李吉隆
     */
            return new R().addData("suppliers", supplierList);
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("获取供应商下拉列表失败: " + e.getMessage());
        }
    }
}