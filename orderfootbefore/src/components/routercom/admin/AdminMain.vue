<template>
<div class="left">
    <div class="header-bar">
			<div class="header-info">
				<span class="post-name">{{postname}}</span>
				<span class="username" :title="username">{{ username }}</span>
			</div>
			<el-link type="success" underline="hover" @click="logout()" class="logout-link">退出登录</el-link>
		</div>
    <!-- 搜索框 -->
		<el-input clearable v-model="filterText" placeholder="输入关键字查询" class="search-input" />
    <el-tree @node-click="handleNodeClick" ref="treeRef" class="custom-tree" :data="data" :props="defaultProps"
			default-expand-all :filter-node-method="filterNode" :highlight-current="true">
			<template #default="{ node,data }">
				<span class="custom-tree-node">
					<span class="node-label">{{ data.menuName }}</span>
				</span>
			</template>
		</el-tree>
</div>
  <div class="right-content">
    <router-view></router-view>
  </div>
</template>

<script setup>
import {ref,onMounted,watch} from 'vue'
import util from '../../../../utils/utils'
import LoadMenusApi from '../../../../api/LoadMenusApi'
import { useRouter, useRoute } from 'vue-router'
const treeRef = ref();

const router = useRouter()
let data=ref('')
const filterText = ref('')

watch(filterText, (val) => {
		treeRef.value?.filter(val)
	})

const postname=ref()
if(localStorage.getItem('identity')==1){
	postname.value="店长:";
}else{
	postname.value="管理员:";
}

//获取用户名
let username=ref('')

onMounted(() => {
		init();
	})



const init = async () => {
    try {
        username.value=localStorage.getItem("username")
        let res = await LoadMenusApi.LoadMenusForAdmin()
        console.log('菜单API返回的原始数据:', res)
        
        // 将返回的菜单数据格式调整为与数据库字段匹配
        const menusList = res.data.menusList || []
        console.log('转换前的菜单数据:', menusList)
        
        // 修复数据字段映射 - 确保API返回的字段与路由所需字段一致
        const normalizedMenus = menusList.map(menu => ({
          ...menu,
          path: menu.path || menu.menuUrl,  // 优先使用path字段
          component: menu.component,
          menuId: menu.menuId,
          parentId: menu.parentId,
          menuName: menu.menuName,
          visible: menu.visible !== undefined ? menu.visible : 1,
          status: menu.status !== undefined ? menu.status : 1
        }))
        
        let treeMenus = util.arrayToTree(normalizedMenus, 0, "menuId", "parentId", "children")
        console.log('转换后的树形菜单:', treeMenus)
        data.value = treeMenus

    } catch (error) {
        console.error('获取菜单数据失败:', error)
    }
}

function logout() {
		localStorage.clear()
		router.push({
			"path": "/adminlogin"
		})
	}

// 节点点击事件处理
	const handleNodeClick = (data) => {
		if (data.children.length==0 && data.path != null) { //如果需要路由跳转
			router.push({
				"path": data.path
			})
		}
		// 处理节点点击逻辑（如跳转、详情展示等）
	}

    	// 节点过滤方法
    	const filterNode = (value, data) => {
    		if (!value) return true
    		return data.menuName.includes(value)
    	}

	const defaultProps = {
		children: 'children',
		label: 'menuName',
	}
</script>

<style>
.tree-container {
		max-width: 600px;
	}

	.header-bar {
		height: 60px;
		background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
		padding: 0 15px;
		font-size: 16px;
		color: #ffffff;
		display: flex;
		justify-content: space-between;
		align-items: center;
		box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
	}

	.header-info {
		display: flex;
		align-items: center;
		gap: 10px;
		flex: 1;
		min-width: 0;
	}

	.post-name {
		font-weight: 600;
		white-space: nowrap;
		flex-shrink: 0;
	}

	.username {
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		max-width: 80px;
		color: #ffffff;
	}

	.logout-link {
		color: #ffffff !important;
		font-weight: 500;
		flex-shrink: 0;
		padding: 4px 12px;
		border-radius: 4px;
		transition: all 0.3s;
		background-color: rgba(255, 255, 255, 0.15);
	}

	.logout-link:hover {
		background-color: rgba(255, 255, 255, 0.25);
		transform: translateY(-1px);
	}

	.search-input {
		width: calc(100% - 20px);
		margin: 10px;
	}

	:deep(.search-input .el-input__wrapper) {
		border-radius: 20px;
		box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
	}

	.node-label {
		font-size: 15px;
		font-weight: 500;
	}

	.custom-tree .el-tree-node__content {
		height: 38px !important;
		line-height: 38px !important;
		border-radius: 4px;
		margin: 2px 8px;
		transition: all 0.3s;
	}

	.custom-tree-node {
		display: flex;
		align-items: center;
		gap: 8px;
	}

	.el-tree--highlight-current .el-tree-node.is-current>.el-tree-node__content {
		background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
		color: #ffffff;
		border-radius: 4px;
		box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
	}

	.left {
		border-right: 1px solid #e0e0e0;
		width: 220px;
		position: absolute;
		bottom: 0px;
		top: 0px;
		background-color: #f8f9fa;
		box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
	}

	.right-content {
  margin-left: 220px;
  padding: 20px;
  height: 100vh;
  overflow: auto;
  background-color: #ffffff;
}
</style>