<template>
<div class="div1">
		<fieldset class="my-fieldset">
			<legend class="my-legend">条件查询:</legend>
			标题:<el-input v-model="textinput.title" placeholder="请输入公告标题:" style="width: 200px;"></el-input>
			状态:<el-select v-model="textinput.status" style="width: 200px">
				<el-option value="" label="全部"></el-option>
				<el-option value="0" label="禁用"></el-option>
				<el-option value="1" label="正常"></el-option>
			</el-select>
			<el-button type="primary" @click="sharebylike()">搜索</el-button>
			<el-button @click="restake()">重置</el-button>
		</fieldset>
	</div>
	<el-table :data="tableData" style="width: 95%;margin-left: 10px;">
		<el-table-column prop="id" label="序号" width="60px" />
		<el-table-column prop="title" label="公告标题" />
		<el-table-column prop="content" label="公告内容" width="200" show-overflow-tooltip />
		<el-table-column prop="publisher" label="发布人" />
		<el-table-column prop="publishTime" label="发布时间" width="180" />
		<el-table-column label="状态">
			<template #default="scope">
				<el-tag type="danger" v-if="scope.row.status==0">禁用</el-tag>
				<el-tag type="success" v-else-if="scope.row.status==1">正常</el-tag>
			</template>
		</el-table-column>
		<el-table-column label="操作">
			<template #header>
				<el-button type="primary" @click="takeadd()">添加</el-button>
			</template>
			<template #default="scope">
				<el-button size="small" @click="handleEdit(scope.row)">
					修改
				</el-button>
				<el-button size="small" type="danger" @click="del(scope.$index,scope.row.id)">
					删除
				</el-button>
			</template>
		</el-table-column>
	</el-table>
	<div class="pagination-container">
		<el-pagination background layout="prev, pager, next" :total="total" :page-size="8"
			@current-change="handchange" />
	</div>
	<teleport to="body">
		<el-dialog v-model="dialogFormVisible" title="添加公告" width="600">
			<el-form :model="form" :rules="announcementRules" ref="announcementFormRef">
				<el-form-item label="公告标题" prop="title">
					<el-input v-model="form.title" placeholder="请输入公告标题" clearable>
					</el-input>
				</el-form-item>
				<el-form-item label="公告内容" prop="content">
					<el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入公告内容" clearable>
					</el-input>
				</el-form-item>
				<el-form-item label="发布人" prop="publisher">
					<el-input v-model="form.publisher" placeholder="请输入发布人" clearable>
					</el-input>
				</el-form-item>
				<el-form-item label="状态">
					<el-radio-group v-model="form.status">
						<el-radio :value="0">禁用</el-radio>
						<el-radio :value="1">正常</el-radio>
					</el-radio-group>
				</el-form-item>
			</el-form>
			<template #footer>
				<div class="dialog-footer">
					<el-button @click="dialogFormVisible = false">取消</el-button>
					<el-button type="primary" @click="handleadd()">
						添加
					</el-button>
				</div>
			</template>
		</el-dialog>
		<!-- 修改 -->
		<el-dialog v-model="dialogFormVisibleup" title="修改公告" width="600">
			<el-form :model="formup" :rules="announcementRules" ref="announcementFormUpRef">
				<el-form-item label="公告标题" prop="title">
					<el-input v-model="formup.title" placeholder="请输入公告标题" clearable>
					</el-input>
				</el-form-item>
				<el-form-item label="公告内容" prop="content">
					<el-input v-model="formup.content" type="textarea" :rows="4" placeholder="请输入公告内容" clearable>
					</el-input>
				</el-form-item>
				<el-form-item label="发布人" prop="publisher">
					<el-input v-model="formup.publisher" placeholder="请输入发布人" clearable>
					</el-input>
				</el-form-item>
				<el-form-item label="状态">
					<el-radio-group v-model="formup.status">
						<el-radio :value="0">禁用</el-radio>
						<el-radio :value="1">正常</el-radio>
					</el-radio-group>
				</el-form-item>
			</el-form>
			<template #footer>
				<div class="dialog-footer">
					<el-button @click="dialogFormVisibleup = false">取消</el-button>
					<el-button type="primary" @click="handlechange()">
					  修改
					</el-button>
				</div>
			</template>
		</el-dialog>
	</teleport>
</template>

<script setup>
import {
		ref,
		onMounted
	} from 'vue'
	import AnnouncementApi from '../../../../api/AnnouncementApi.js';
	import {
		ElMessageBox,
		ElMessage
	} from 'element-plus' 

	let dialogFormVisible = ref(false);
	let textinput = ref({
		title: '',
		status: '',
	});
	const tableData = ref([])

	let form = ref({
		id: '',
		title: '',
		content: '',
		publisher: '',
		status: 1,
	})

	const announcementRules = {
		title: [{
			required: true,
			message: '请输入公告标题',
			trigger: 'blur'
		}, {
			min: 2,
			max: 100,
			message: '公告标题长度在2到100个字符',
			trigger: 'blur'
		}],
		content: [{
			required: true,
			message: '请输入公告内容',
			trigger: 'blur'
		}, {
			min: 5,
			max: 1000,
			message: '公告内容长度在5到1000个字符',
			trigger: 'blur'
		}],
		publisher: [{
			required: true,
			message: '请输入发布人',
			trigger: 'blur'
		}, {
			min: 2,
			max: 20,
			message: '发布人长度在2到20个字符',
			trigger: 'blur'
		}]
	};

	let total = ref(0);
	const selectwhere = ref({
		page: 1,
		where: []
	})

	function sharebylike() {
		selectwhere.value.where = [];
		if (textinput.value.title) {
			selectwhere.value.where.push({
				"column": "title",
				"type": "like",
				"value": textinput.value.title
			});
		}
		if (textinput.value.status !== '') {
			selectwhere.value.where.push({
				"column": "status",
				"type": "eq",
				"value": textinput.value.status
			});
		}
		handleData();
	}

	async function handleData() {
		try {
			let res = await AnnouncementApi.AnnouncementSelect(selectwhere.value);
			tableData.value = res.data.pageInfo.list;
			total.value = res.data.pageInfo.total;
		} catch (error) {
			console.error("获取公告数据失败:", error);
			ElMessage.error("获取公告数据失败");
		}
	}

	function handchange(pagenum) {
		selectwhere.value.page = pagenum;
		handleData();
	}

	onMounted(() => {
		handleData();
	})

	function del(index, id) {
		ElMessageBox.confirm('确定要删除这个公告吗?', '警告', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning'
		}).then(async () => {
			try {
				let rs = await AnnouncementApi.deleteAnnouncement(id);
				if (rs.code === 200) {
					ElMessage.success(rs.data.status || '删除成功');
					handleData(); // 重新加载数据
				} else {
					ElMessage.error(rs.message || '删除失败');
				}
			} catch (error) {
				console.error('删除公告错误:', error);
				ElMessage.error('删除失败: ' + (error.message || '未知错误'));
			}
		}).catch(() => {
			// 用户取消删除，不执行任何操作
		});
	}

	async function handleadd() {
		try {
			let rs = await AnnouncementApi.addAnnouncement(form.value);
			if (rs.code == 200) {
				ElMessage.success("添加成功");
				handleData();
				dialogFormVisible.value = false;
			} else {
				ElMessage.error(rs.message || '添加失败');
			}
		} catch (error) {
			console.error('添加公告错误:', error);
			ElMessage.error('添加失败: ' + (error.message || '未知错误'));
		}
	}

	async function takeadd() {
		dialogFormVisible.value = true;
		form.value = {
			id: '',
			title: '',
			content: '',
			publisher: '',
			status: 1
		};
	}
	
	let dialogFormVisibleup = ref(false);
	let formup = ref({
		id: '',
		title: '',
		content: '',
		publisher: '',
		status: 1,
	})

	async function handleEdit(row) {
		try {
			let res = await AnnouncementApi.getAnnouncementById(row.id);
			if (res.code === 200) {
				dialogFormVisibleup.value = true;
				formup.value = {
					...res.data.entity
				};
			} else {
				ElMessage.error(res.message || '获取公告信息失败');
			}
		} catch (error) {
			console.error('获取公告详情失败:', error);
			ElMessage.error('获取公告详情失败');
		}
	}

	async function handlechange() {
		try {
			let rs = await AnnouncementApi.updateAnnouncement(formup.value);
			if (rs.code == 200) {
				ElMessage.success("修改成功");
				handleData();
				dialogFormVisibleup.value = false;
			} else {
				ElMessage.error(rs.message || '修改失败');
			}
		} catch (error) {
			console.error('修改公告错误:', error);
			ElMessage.error('修改失败: ' + (error.message || '未知错误'));
		}
	}
	
	//重新加载所有信息
	function restake(){
		textinput.value.title="";
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
