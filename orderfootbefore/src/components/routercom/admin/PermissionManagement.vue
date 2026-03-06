<template>
	<fieldset class="my-fieldset">
        <legend class="my-legend">条件查询:</legend>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="岗位">
          <el-select v-model="searchForm.post" placeholder="请选择岗位" style="width: 200px;">
            <el-option label="服务员" value="服务员" />
            <el-option label="厨师" value="厨师" />
            <el-option label="收银员" value="收银员" />
			 <el-option label="店长" value="店长"></el-option>
            <el-option label="管理员" value="管理员"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="工作状态">
          <el-select v-model="searchForm.workStatus" placeholder="请选择工作状态" style="width: 200px;">
            <el-option label="在岗" value="1" />
            <el-option label="休假" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit()">查询</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
</fieldset>
	<el-row :gutter="24">
		<el-col :span="12">
			<el-table ref="loginFormRef" :data="tableData" border style="width: 95%;margin-left: 10px;">
				<el-table-column prop="username" label="用户名" width="150" />
				<el-table-column prop="post" label="职位" width="80" />
				<el-table-column prop="shopName" label="商店名字" width="150" />
				<el-table-column label="是否禁用" width="90">
					<template #default="scope">
						<el-tag type="danger" v-if="scope.row.workStatus==0">休假</el-tag>
						<el-tag  v-else="scope.row.workStatus==1">在岗</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="操作">
					<template #default="scope">
						<el-button size="small" type="danger" @click="see(scope.row)">
							查看菜单
						</el-button>
					</template>
				</el-table-column>
			</el-table>
			<el-pagination class="pagination-container" @current-change="handchange" size="small" :page-size="6"
				background layout="prev, pager, next" :total="total" />
		</el-col>
		<el-col :span="12">
			<el-button type="primary" @click="takeadd()">添加</el-button>当前用户名:{{employeeNeme }}
	<el-tree ref="treeRef" class="custom-tree" :data="data" :props="defaultProps" default-expand-all
		:filter-node-method="filterNode" :highlight-current="true"  node-key="menuId"
		check-strictly="true">
		<template #default="{ node,data }">
			<span class="custom-tree-node">
				<!-- <i class="el-icon-folder-opened" v-if="node.children && node.children.length"></i>
					<i class="el-icon-document" v-else></i> -->
				<span class="node-label">{{ data.menuName }}</span><el-button type="danger" @click="delEmployeeMenus(data.menuId)">删除</el-button>
			</span>
		</template>
	</el-tree>
		</el-col>
	</el-row>
	  <el-drawer v-model="dialogVisible" title="添加员工关联菜单">
    <span> 当前你选择的用户名：{{ employeeNeme}}</span>
	<el-form>
	<el-form-item label="选择要添加的菜单">
	<el-tree-select v-model="employeeaddref" :data="menuTreeData" check-strictly="false"
				:render-after-expand="false" style="width: 240px" :props="{ label: 'menuName', children: 'children' }"
				node-key="menuId" show-checkbox multiple placeholder="请选择菜单" check-on-click-node />
	</el-form-item>
	<el-form-item>
		<el-button type="primary" @click="saveEmployeeMenus">保存</el-button>
		<el-button @click="dialogVisible = false">取消</el-button>
	</el-form-item>
	</el-form>
  </el-drawer>
</template>

<script setup>
	import {
		ref,
		onMounted
	} from 'vue'
		let menuTreeData = ref([]);
	import EmployeeApi from '../../../../api/EmployeeApi.js'
	import LoadMenusApi from '../../../../api/LoadMenusApi.js';
	import utils from '../../../../utils/utils.js'
	import EmployeeMenusApi from '../../../../api/EmployeeMenusApi.js';
	import {
		ElMessage,
		ElMessageBox
	} from 'element-plus';
	let defaultProps=ref({
  label: 'menuName',
  children: 'children',
  value: 'menuId'
});
let employeeNeme=ref();
let employeeaddref=ref([]);
	const data = ref([])
	let pagesize = ref(5);
	let total = ref(10);
	const searchForm = ref({
  username: '',
  post: '',
  workStatus: ''
  })
	const selectwhere = ref({
		page: 1,
		where: []
	})
	const onSubmit = () => {
		 selectwhere.value.where=[{
     "column":"username",
     "type":"like",
     "value":searchForm.value.username
    },{
      "column":"post",
     "type":"eq",
     "value":searchForm.value.post
    },{
       "column":"work_status",
     "type":"eq",
     "value":searchForm.value.workStatus
    }]
		init();
	}
	const resetForm = () => {
  searchForm.value.username = '';
  searchForm.value.post = '';
  searchForm.value.workStatus = '';
  onSubmit();
}
	const tableData = ref([]);
	let dialogVisible = ref(false);
	onMounted(() => {
		init();
	})
	const init = async () => {
		let res = await EmployeeApi.selectEmployeeandshop(selectwhere.value);
		console.log("初始化数据", res)
		tableData.value = res.data.pageInfo.list; //设置表格数据
		total.value = res.data.pageInfo.total;
	}

	function handchange(pagenum) {
		selectwhere.value.page = pagenum;
		init();
	}
   let employeeid=ref();
	async function see(row) {
	    employeeid.value=row.id;
		employeeNeme.value=row.username;
		let res=await LoadMenusApi.selectmenusbyuserid(employeeid.value);
		console.log('用户id菜单',res);
		let treeMenus = utils.arrayToTree(res.data.menusList, 0, "menuId", "parentId", "children");
		data.value=treeMenus;
	}
	async function delEmployeeMenus(menuId) {
		ElMessageBox.confirm('确定要删除这个菜单吗?', '警告', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning'
		}).then(async () => {
			try {
				// 创建新的局部变量，避免修改全局的selectwhere
				const deleteWhere = {
					where: [{
						"column": "employeeid",
						"type": "eq",
						"value": employeeid.value
					}, {
						"column": "menusid",
						"type": "eq",
						"value": menuId
					}]
				};
				let rs = await EmployeeMenusApi.delEmployeeMenus(deleteWhere);
				if (rs.code === 200) {
					ElMessage.success(rs.data.status || '删除成功');
					// 直接刷新当前员工的菜单列表，而不是重新加载所有数据
					see({ id: employeeid.value, username: employeeNeme.value });
				} else {
					ElMessage.error(rs.message || '删除失败');
				}
			} catch (error) {
				console.error('删除地址错误:', error);
				ElMessage.error('删除失败: ' + (error.message || '未知错误'));
			}
		}).catch(() => {
			// 用户取消删除，不执行任何操作
		});
	}
	async function takeadd(){
dialogVisible.value=true;
employeeaddref.value = []; // 重置选择
      let res = await LoadMenusApi.selectallmenus();
	  console.log("所有菜单",res)
		let treeRes = utils.arrayToTree(res.data.menusList, 0,
			"menuId", "parentId", "children");
		menuTreeData.value = treeRes;
	}

async function saveEmployeeMenus() {
	if (!employeeid.value) {
		ElMessage.warning('请先选择一个员工');
		return;
	}
	if (!employeeaddref.value || employeeaddref.value.length === 0) {
		ElMessage.warning('请选择要添加的菜单');
		return;
	}
	try {
		// 构建请求参数
		// 当check-strictly为false时，employeeaddref.value已包含所有选中的节点（包括自动选中的父节点）
		const params = {
			employeeId: employeeid.value,
			menuIds: employeeaddref.value
		};
		
		console.log('保存的所有节点ID（包括父节点）:', employeeaddref.value);
		
		let res = await EmployeeMenusApi.addEmployeeMenus(params);
		if (res.code === 200) {
			ElMessage.success('菜单添加成功');
			dialogVisible.value = false;
			// 重新加载当前员工的菜单数据
			see({ id: employeeid.value, username: employeeNeme.value });
		} else {
			ElMessage.error(res.message || '添加失败');
		}
	} catch (error) {
		console.error('添加菜单错误:', error);
		ElMessage.error('添加失败: ' + (error.message || '未知错误'));
	}
}
</script>

<style>
</style>
