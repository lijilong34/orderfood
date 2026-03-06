package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.Supplier;
import org.example.orderfoodafter.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 供应商管理控制器
 */
@RestController
@RequestMapping("/supplier")
public class SupplierController extends BaseController<Supplier, SupplierService> {

    @Autowired
    public SupplierController(SupplierService service) {
        super(service);
    }

    /**
     * 店铺管理员获取供应商列表（分页）
     */
    @PostMapping("/shopadmin/list")
    public R getShopAdminSupplierList(@RequestBody Map<String, Object> params) {
        try {
            int page = params.containsKey("page") ? Integer.parseInt(params.get("page").toString()) : 1;
            int pageSize = params.containsKey("pageSize") ? Integer.parseInt(params.get("pageSize").toString()) : 10;
            Long shopId = params.containsKey("shopId") ? Long.parseLong(params.get("shopId").toString()) : null;

            PageHelper.startPage(page, pageSize);

            QueryWrapper<Supplier> queryWrapper = new QueryWrapper<>();
            
            // 根据店铺ID筛选
            if (shopId != null) {
                queryWrapper.eq("shop_id", shopId);
            }

            // 搜索关键词（供应商名称、联系人、联系电话）
            if (params.containsKey("keyword") && params.get("keyword") != null) {
                String keyword = params.get("keyword").toString().trim();
                if (!keyword.isEmpty()) {
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
            if (params.containsKey("status") && params.get("status") != null) {
                String statusStr = params.get("status").toString();
                if (!statusStr.isEmpty()) {
                    queryWrapper.eq("status", Integer.parseInt(statusStr));
                }
            }

            queryWrapper.orderByDesc("create_time");

            List<Supplier> supplierList = service.list(queryWrapper);
            PageInfo<Supplier> pageInfo = new PageInfo<>(supplierList);

            return new R().addData("pageInfo", pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取供应商列表失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员创建供应商
     */
    @PostMapping("/shopadmin/create")
    public R createSupplier(@RequestBody Map<String, Object> params) {
        try {
            Supplier supplier = new Supplier();

            // 验证必填字段
            if (!params.containsKey("name") || params.get("name") == null || params.get("name").toString().trim().isEmpty()) {
                return R.error("供应商名称不能为空");
            }
            if (!params.containsKey("contactPerson") || params.get("contactPerson") == null || params.get("contactPerson").toString().trim().isEmpty()) {
                return R.error("联系人不能为空");
            }
            if (!params.containsKey("contactPhone") || params.get("contactPhone") == null || params.get("contactPhone").toString().trim().isEmpty()) {
                return R.error("联系电话不能为空");
            }
            if (!params.containsKey("shopId") || params.get("shopId") == null) {
                return R.error("店铺ID不能为空");
            }

            supplier.setName(params.get("name").toString().trim());
            supplier.setContactPerson(params.get("contactPerson").toString().trim());
            supplier.setContactPhone(params.get("contactPhone").toString().trim());
            
            if (params.containsKey("address") && params.get("address") != null) {
                supplier.setAddress(params.get("address").toString().trim());
            }
            
            Long shopId = Long.parseLong(params.get("shopId").toString());
            supplier.setShopId(shopId);
            
            // 默认状态为1（正常合作）
            supplier.setStatus((byte) 1);
            
            supplier.setCreateTime(new Date());
            supplier.setUpdateTime(new Date());

            boolean result = service.save(supplier);

            if (result) {
                return new R().addData("supplier", supplier).addData("message", "供应商创建成功");
            } else {
                return R.error("供应商创建失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("供应商创建失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员更新供应商
     */
    @PutMapping("/shopadmin/update")
    public R updateSupplier(@RequestBody Map<String, Object> params) {
        try {
            Long id = Long.parseLong(params.get("id").toString());

            Supplier supplier = service.getById(id);
            if (supplier == null) {
                return R.error("供应商不存在");
            }

            // 更新字段
            if (params.containsKey("name")) {
                supplier.setName(params.get("name").toString().trim());
            }
            if (params.containsKey("contactPerson")) {
                supplier.setContactPerson(params.get("contactPerson").toString().trim());
            }
            if (params.containsKey("contactPhone")) {
                supplier.setContactPhone(params.get("contactPhone").toString().trim());
            }
            if (params.containsKey("address")) {
                supplier.setAddress(params.get("address").toString().trim());
            }
            if (params.containsKey("status")) {
                supplier.setStatus(Byte.parseByte(params.get("status").toString()));
            }

            supplier.setUpdateTime(new Date());

            boolean result = service.updateById(supplier);

            if (result) {
                return new R().addData("supplier", supplier).addData("message", "供应商更新成功");
            } else {
                return R.error("供应商更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("供应商更新失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员删除供应商
     */
    @DeleteMapping("/shopadmin/delete/{id}")
    public R deleteSupplier(@PathVariable Long id) {
        try {
            Supplier supplier = service.getById(id);
            if (supplier == null) {
                return R.error("供应商不存在");
            }

            boolean result = service.removeById(id);

            if (result) {
                return new R().addData("message", "供应商删除成功");
            } else {
                return R.error("供应商删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("供应商删除失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员获取供应商详情
     */
    @GetMapping("/shopadmin/detail/{id}")
    public R getSupplierDetail(@PathVariable Long id) {
        try {
            Supplier supplier = service.getById(id);
            if (supplier == null) {
                return R.error("供应商不存在");
            }

            return new R().addData("supplier", supplier);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取供应商详情失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员启用供应商
     */
    @PostMapping("/shopadmin/enable/{id}")
    public R enableSupplier(@PathVariable Long id) {
        try {
            Supplier supplier = service.getById(id);
            if (supplier == null) {
                return R.error("供应商不存在");
            }

            supplier.setStatus((byte) 1); // 1-正常
            supplier.setUpdateTime(new Date());
            
            boolean result = service.updateById(supplier);

            if (result) {
                return new R().addData("message", "供应商启用成功");
            } else {
                return R.error("供应商启用失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("供应商启用失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员禁用供应商
     */
    @PostMapping("/shopadmin/disable/{id}")
    public R disableSupplier(@PathVariable Long id) {
        try {
            Supplier supplier = service.getById(id);
            if (supplier == null) {
                return R.error("供应商不存在");
            }

            supplier.setStatus((byte) 0); // 0-终止
            supplier.setUpdateTime(new Date());
            
            boolean result = service.updateById(supplier);

            if (result) {
                return new R().addData("message", "供应商禁用成功");
            } else {
                return R.error("供应商禁用失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("供应商禁用失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员获取供应商下拉列表
     */
    @GetMapping("/shopadmin/options")
    public R getSupplierOptions(@RequestParam Long shopId) {
        try {
            QueryWrapper<Supplier> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("shop_id", shopId);
            queryWrapper.eq("status", 1); // 只获取正常状态的供应商
            queryWrapper.orderByAsc("name");

            List<Supplier> supplierList = service.list(queryWrapper);

            return new R().addData("suppliers", supplierList);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取供应商下拉列表失败: " + e.getMessage());
        }
    }
}
