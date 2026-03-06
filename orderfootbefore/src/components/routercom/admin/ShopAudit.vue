<template>
  <div class="shop-audit-container">
    <!-- 条件查询 -->
    <div class="search-container">
      <h3>条件查询:</h3>
      <div class="search-form">
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
          <el-form-item label="店铺名称">
            <el-input v-model="searchForm.name" placeholder="请输入店铺名称"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
          </el-form-item>
          <el-form-item>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 表格 -->
    <div class="table-container">
      <el-table :data="shops" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="店铺ID" width="80"></el-table-column>
        <el-table-column prop="name" label="店铺名称" min-width="120"></el-table-column>
        <el-table-column prop="businessScope" label="经营品类" min-width="100"></el-table-column>
        <el-table-column prop="address" label="地址" min-width="150"></el-table-column>
        <el-table-column prop="businessHours" label="营业时间" min-width="120"></el-table-column>
        <el-table-column prop="phone" label="联系电话" min-width="110"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 3" type="warning">待审核</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="approveShop(scope.row.id)">审核通过</el-button>
            <el-button type="danger" size="small" @click="rejectShop(scope.row.id)">拒绝</el-button>
            <el-button type="info" size="small" @click="viewDetails(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[5, 10, 20, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      ></el-pagination>
    </div>

    <!-- 店铺详情对话框 -->
    <teleport to="body">
      <el-dialog
        title="店铺详情"
        v-model="dialogVisible"
        width="50%"
      >
        <div v-if="selectedShop" class="shop-details">
          <el-form label-position="top" :model="selectedShop">
            <el-form-item label="店铺名称">
              {{ selectedShop.name }}
            </el-form-item>
            <el-form-item label="店铺logo">
              <img v-if="selectedShop.logo" :src="selectedShop.logo" alt="店铺logo" class="detail-logo" />
              <span v-else>无</span>
            </el-form-item>
            <el-form-item label="店铺地址">
              {{ selectedShop.address }}
            </el-form-item>
            <el-form-item label="联系电话">
              {{ selectedShop.phone }}
            </el-form-item>
            <el-form-item label="营业时间">
              {{ selectedShop.businessHours }}
            </el-form-item>
            <el-form-item label="经营品类">
              {{ selectedShop.businessScope }}
            </el-form-item>
            <el-form-item label="停车信息">
              {{ selectedShop.parkingInfo || '无' }}
            </el-form-item>
            <el-form-item label="店铺介绍">
              {{ selectedShop.introduction || '无' }}
            </el-form-item>
          </el-form>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </el-dialog>
    </teleport>
  </div>
</template>

<script>
import ShopApi from '../../../../api/ShopApi';

export default {
  name: 'ShopAudit',
  data() {
    return {
      shops: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      loading: false,
      dialogVisible: false,
      selectedShop: null,
      searchForm: {
        name: ''
      }
    };
  },
  mounted() {
    this.getPendingShops();
  },
  methods: {
    // 获取待审核店铺
    getPendingShops() {
      this.loading = true;
      const where = [
        {
          column: 'status',
          value: 3,
          type: 'eq'
        }
      ];
      
      // 添加店铺名称查询条件
      if (this.searchForm.name) {
        where.push({
          column: 'name',
          value: this.searchForm.name,
          type: 'like'
        });
      }
      
      const params = {
        where: where,
        page: this.currentPage
      };
      
      console.log('发送查询请求参数:', JSON.stringify(params, null, 2));
      
      ShopApi.ShopSelect(params)
        .then(response => {
          console.log('获取待审核店铺完整响应:', JSON.stringify(response, null, 2));
          if (response.code === 200) {
            console.log('待审核店铺数据:', response.data.pageInfo.list);
            console.log('待审核店铺总数:', response.data.pageInfo.total);
            this.shops = response.data.pageInfo.list;
            this.total = response.data.pageInfo.total;
          } else {
            this.$message.error('获取待审核店铺失败: ' + (response.message || '未知错误'));
            this.shops = [];
            this.total = 0;
          }
        })
        .catch(error => {
          console.error('获取待审核店铺错误详情:', error);
          this.$message.error('获取待审核店铺失败: ' + (error.message || '网络错误'));
          this.shops = [];
          this.total = 0;
        })
        .finally(() => {
          this.loading = false;
        });
    },
    
    // 审核通过
    approveShop(shopId) {
      this.$confirm('确定要审核通过该店铺吗？审核通过后店铺将变为正常营业状态', '审核确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      .then(() => {
        return ShopApi.openShop(shopId);
      })
      .then(response => {
        if (response.code === 200) {
          this.$message.success('店铺审核通过');
          this.getPendingShops(); // 刷新列表
        } else {
          this.$message.error('审核通过失败: ' + response.message);
        }
      })
      .catch(error => {
        if (error !== 'cancel') {
          this.$message.error('审核通过失败: ' + (error.message || '操作取消'));
        }
      });
    },
    
    // 拒绝审核
    rejectShop(shopId) {
      this.$confirm('确定要拒绝该店铺吗？拒绝后可以选择封禁或删除', '拒绝确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'danger'
      })
      .then(() => {
        return this.$confirm('请选择处理方式', '处理方式', {
          confirmButtonText: '封禁',
          cancelButtonText: '删除',
          type: 'info'
        });
      })
      .then(() => {
        // 封禁店铺
        return ShopApi.banShop(shopId);
      })
      .then(response => {
        if (response.code === 200) {
          this.$message.success('店铺已封禁');
          this.getPendingShops(); // 刷新列表
        } else {
          this.$message.error('封禁失败: ' + response.message);
        }
      })
      .catch(error => {
        if (error === 'cancel') {
          // 删除店铺
          return ShopApi.deleteShop(shopId);
        }
        throw error;
      })
      .then(response => {
        if (response.code === 200) {
          this.$message.success('店铺已删除');
          this.getPendingShops(); // 刷新列表
        } else {
          this.$message.error('删除失败: ' + response.message);
        }
      })
      .catch(error => {
        if (error !== 'cancel') {
          this.$message.error('拒绝失败: ' + (error.message || '操作取消'));
        }
      });
    },
    
    // 查看详情
    viewDetails(shop) {
      this.selectedShop = shop;
      this.dialogVisible = true;
    },
    
    // 分页处理 - 当前页改变
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getPendingShops();
    },
    
    // 分页处理 - 每页条数改变
    handleSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
      this.getPendingShops();
    },
    
    // 搜索
    handleSearch() {
      this.currentPage = 1;
      this.getPendingShops();
    },
    
    // 重置
    handleReset() {
      this.searchForm = {
        name: ''
      };
      this.currentPage = 1;
      this.getPendingShops();
    }
  }
};
</script>

<style scoped>
.shop-audit-container {
  padding: 20px;
  background-color: #ffffff;
  min-height: 600px;
}

/* 搜索容器样式 */
.search-container {
  background-color: #ffffff;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.search-container h3 {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.search-form {
  margin-left: 0;
}

/* 表格容器样式 */
.table-container {
  background-color: white;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  margin-bottom: 20px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

/* Element UI 表格样式覆盖 */
:deep(.el-table) {
  border: none;
}

:deep(.el-table__header-wrapper) th {
  background-color: #fafafa;
  font-weight: bold;
  color: #333;
  border-bottom: 1px solid #e8e8e8;
}

:deep(.el-table__body-wrapper) td {
  border-bottom: 1px solid #e8e8e8;
  padding: 12px 0;
}

:deep(.el-table__body-wrapper) tr:hover {
  background-color: #f5f7fa;
}

/* 分页容器样式 */
.pagination-container {
  display: flex;
  justify-content: right;
  margin-top: 20px;
  padding-right: 10px;
}

/* 详情页样式 */
.shop-details {
  max-height: 500px;
  overflow-y: auto;
}

.detail-logo {
  width: 200px;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #e8e8e8;
}

/* 按钮样式 */
:deep(.el-button--small) {
  padding: 4px 12px;
  font-size: 12px;
  margin-right: 8px;
}

/* 搜索表单样式 */
:deep(.demo-form-inline) {
  margin-bottom: 0;
}

:deep(.el-form-item__label) {
  font-weight: bold;
  color: #333;
}

:deep(.el-input__inner) {
  width: 200px;
}

/* 加载状态 */
:deep(.el-loading-mask) {
  background-color: rgba(255, 255, 255, 0.8);
}
</style>