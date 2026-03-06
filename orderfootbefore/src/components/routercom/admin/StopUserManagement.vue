<template>
<div class="div1">
		<fieldset class="my-fieldset">
			<legend class="my-legend">条件查询:</legend>
			手机号:<el-input v-model="textinput.phone" placeholder="请输入手机号:" style="width: 200px;"></el-input>
			昵称:<el-input v-model="textinput.nickname" placeholder="请输入昵称:" style="width: 200px;"></el-input>
			<el-button type="primary" @click="sharebylike()">搜索</el-button>
			<el-button @click="restake()">重置</el-button>
		</fieldset>
	</div>
	<el-table :data="tableData" style="width: 95%;margin-left: 10px;">
		<el-table-column prop="id" label="序号" width="60px" />
		<el-table-column prop="phone" label="手机号" />
		<el-table-column prop="nickname" label="昵称" />
		<el-table-column  label="性别">
            <template #default="scope">
				<span v-if="scope.row.gender==0">未知</span>
				<span v-else-if="scope.row.gender==1">男</span>
				<span v-else-if="scope.row.gender==2">女</span>
			</template>
        </el-table-column>
		<el-table-column label="状态">
			<template #default="scope">
				<el-tag type="danger" v-if="scope.row.status==0">禁用</el-tag>
				<el-tag type="success" v-else-if="scope.row.status==1">正常</el-tag>
			</template>
		</el-table-column>
		<el-table-column label="操作">
			<template #default="scope">
				<el-button size="small" type="success"  @click="handleEdit(scope.row)">
					解禁
				</el-button>
			</template>
		</el-table-column>
	</el-table>
	<div class="pagination-container">
		<el-pagination background layout="prev, pager, next" :total="total" :page-size="6"
			@current-change="handchange" />
	</div>
</template>

<script setup>
import {
		ref,
		onMounted
	} from 'vue'
	import UserApi from '../../../../api/UserApi.js';
	import {
		ElMessageBox,
		ElMessage
	} from 'element-plus'
	
	let dialogFormVisible = ref(false);
	let textinput = ref({
		phone: '',
		nickname: ''
	});
	const tableData = ref([])

	let form = ref({
		id: '',
		phone: '',
		password: '',
		nickname: '',
		gender: 0,
		status: 1
	})
	const loginRules = {
		phone: [{
			required: true,
			message: '请输入手机号',
			trigger: 'blur'
		}, {
			pattern: /^1[3-9]\d{9}$/,
			message: '请输入正确的手机号码',
			trigger: 'blur'
		}],
		password: [{
			required: true,
			message: '请输入密码',
			trigger: 'blur'
		}, {
			min: 6,
			max: 20,
			message: '密码长度在6到20个字符',
			trigger: 'blur'
		}],
		nickname: [{
			required: true,
			message: '请输入昵称',
			trigger: 'blur'
		}, {
			min: 2,
			max: 20,
			message: '昵称长度在2到20个字符',
			trigger: 'blur'
		}]
	};
	let total = ref(0);
	const selectwhere = ref({
		page: 1,
		where: []
	})

	function sharebylike() {
			selectwhere.value.where.push({
				"column": "phone",
				"type": "like",
				"value": textinput.value.phone
			},{
				"column": "nickname",
				"type": "like",
				"value": textinput.value.nickname
			});
	
		handleData();
	}

	async function handleData() {
		try {
            	selectwhere.value.where = [	{
				"column": "status",
				"type": "eq",
				"value":0
			}]
			let res = await UserApi.UserSelect(selectwhere.value);
			tableData.value = res.data.pageInfo.list;
			total.value = res.data.pageInfo.total;
		} catch (error) {
			console.error("获取用户数据失败:", error);
			ElMessage.error("获取用户数据失败");
		}
	}

	function handchange(pagenum) {
		selectwhere.value.page = pagenum;
		handleData();
	}

	onMounted(() => {
		handleData();
	})

	
	let dialogFormVisibleup = ref(false);
	let formup = ref({
		id: '',
		phone: '',
		password: '',
		nickname: '',
		gender: 0,
		status: 1
	})

	async function handleEdit(row) {
		try {
			let res = await UserApi.updateUser({
				...row,
				status: 1 // 将状态设置为正常
			});
			if (res.code === 200) {
				ElMessage.success('用户已解禁');
				handleData(); // 刷新数据
			} else {
				ElMessage.error(res.message || '获取用户信息失败');
			}
		} catch (error) {
			console.error('获取用户详情失败:', error);
			ElMessage.error('获取用户详情失败');
		}
	}

	async function handlechange() {
		try {
			let rs = await UserApi.updateUser(formup.value);
			if (rs.code == 200) {
				ElMessage.success("修改成功");
				handleData();
				dialogFormVisibleup.value = false;
			} else {
				ElMessage.error(rs.message || '修改失败');
			}
		} catch (error) {
			console.error('修改用户错误:', error);
			ElMessage.error('修改失败: ' + (error.message || '未知错误'));
		}
	}
	
	//重新加载所有信息
	function restake(){
		textinput.value.phone="";
		textinput.value.nickname="";
		textinput.value.status="";
		sharebylike();
	}
</script>

<style>
.pagination-container {
		display: flex;
		justify-content: center;
		margin-top: 20px;
	}
</style>