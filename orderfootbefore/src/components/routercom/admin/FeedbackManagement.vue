<template>
	<div class="div1">
		<fieldset class="my-fieldset">
			<legend class="my-legend">条件查询:</legend>
			用户昵称:<el-input v-model="textinput.nickname" placeholder="请输入用户昵称:" style="width: 200px;"></el-input>
			手机号:<el-input v-model="textinput.phone" placeholder="请输入手机号:" style="width: 200px;"></el-input>
			反馈类型:<el-select v-model="textinput.type" style="width: 200px" placeholder="请选择反馈类型">
				<el-option :value="0" label="全部"></el-option>
				<el-option :value="1" label="功能建议"></el-option>
				<el-option :value="2" label="问题反馈"></el-option>
				<el-option :value="3" label="投诉"></el-option>
				<el-option :value="4" label="其他"></el-option>
			</el-select>
			状态:<el-select v-model="textinput.status" style="width: 200px" placeholder="请选择状态">
				<el-option :value="null" label="全部"></el-option>
				<el-option :value="0" label="待处理"></el-option>
				<el-option :value="1" label="处理中"></el-option>
				<el-option :value="2" label="已处理"></el-option>
				<el-option :value="3" label="已关闭"></el-option>
			</el-select>
			<el-button type="primary" @click="sharebylike()">搜索</el-button>
			<el-button @click="restake()">重置</el-button>
		</fieldset>
	</div>
	<el-table :data="tableData" style="width: 95%;margin-left: 10px;">
		<el-table-column prop="id" label="序号" width="80px" />
		<el-table-column prop="username" label="用户昵称" width="120px" />
		<el-table-column prop="userphone" label="手机号" width="130px" />
		<el-table-column prop="type" label="反馈类型" width="100px">
			<template #default="scope">
				<el-tag :type="getTypeType(scope.row.type)">
					{{ getTypeText(scope.row.type) }}
				</el-tag>
			</template>
		</el-table-column>
		<el-table-column prop="content" label="反馈内容" min-width="200" show-overflow-tooltip />
		<el-table-column prop="priority" label="优先级" width="90px">
			<template #default="scope">
				<el-tag :type="getPriorityType(scope.row.priority)" :effect="getPriorityEffect(scope.row.priority)">
					{{ getPriorityText(scope.row.priority) }}
				</el-tag>
			</template>
		</el-table-column>
		<el-table-column prop="status" label="状态" width="100px">
			<template #default="scope">
				<el-tag :type="getStatusType(scope.row.status)">
					{{ getStatusText(scope.row.status) }}
				</el-tag>
			</template>
		</el-table-column>
		<el-table-column prop="createTime" label="创建时间" width="180px" />
<el-table-column label="操作" width="180px" fixed="right">
			<template #default="scope">
				<el-button size="small" type="primary" @click="handleEdit(scope.row)">
					修改状态
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

	<!-- 查看详情对话框 -->
	<teleport to="body">
		<el-dialog v-model="dialogViewVisible" title="修改反馈状态" width="500">
			<el-form :model="editForm" label-width="80px">
				<el-form-item label="反馈内容">
					<div class="feedback-content">{{ editForm.content }}</div>
				</el-form-item>
				<el-form-item label="状态">
					<el-radio-group v-model="editForm.status">
						<el-radio :label="0">待处理</el-radio>
						<el-radio :label="1">处理中</el-radio>
						<el-radio :label="2">已处理</el-radio>
						<el-radio :label="3">已关闭</el-radio>
					</el-radio-group>
				</el-form-item>
			</el-form>
			<template #footer>
				<el-button @click="dialogViewVisible = false">取消</el-button>
				<el-button type="primary" @click="handleSaveStatus">
					保存
				</el-button>
			</template>
		</el-dialog>
	</teleport>
</template>

<script setup>
import {
		ref,
		onMounted
	} from 'vue'
	import FeedbackApi from '../../../../api/FeedbackApi.js';
	import {
		ElMessageBox,
		ElMessage
	} from 'element-plus'

	let textinput = ref({
		nickname: '',
		phone: '',
		type: 0,
		status: null
	});

	let tableData = ref([]);
	let total = ref(0);
	const selectwhere = ref({
		page: 1,
		where: []
	})

	// 获取反馈类型标签类型
	const getTypeType = (type) => {
		if (type === 'suggestion') return 'primary';
		if (type === 'complaint') return 'danger';
		if (type === 'inquiry') return 'warning';
		if (type === 'other') return 'info';
		return 'info';
	};

	// 获取反馈类型文本
	const getTypeText = (type) => {
		const typeMap = {
			1: '功能建议',
			2: '问题反馈',
			3: '服务评价',
			4: '其他'
		};
		return typeMap[type] || '其他';
	};

	// 获取优先级标签类型
	const getPriorityType = (priority) => {
		if (priority === 1) return 'danger';
		if (priority === 2) return 'warning';
		if (priority === 3) return 'info';
		return 'info';
	};

	// 获取优先级标签效果
	const getPriorityEffect = (priority) => {
		if (priority === 1) return 'dark';
		return 'light';
	};

	// 获取优先级文本
	const getPriorityText = (priority) => {
		const priorityMap = {
			1: '高',
			2: '中',
			3: '低'
		};
		return priorityMap[priority] || '中';
	};

	// 获取状态标签类型
	const getStatusType = (status) => {
		if (status === 0) return 'warning';
		if (status === 1) return 'primary';
		if (status === 2) return 'success';
		return 'info';
	};

	// 获取状态文本
	const getStatusText = (status) => {
		const statusMap = {
			0: '待处理',
			1: '处理中',
			2: '已处理',
			3: '已关闭'
		};
		return statusMap[status] || '待处理';
	};

	function sharebylike() {
		console.log('sharebylike被调用了', textinput.value);
		selectwhere.value.where = [];
		if (textinput.value.nickname) {
			selectwhere.value.where.push({
				"column": "`user`.nickname",
				"type": "like",
				"value": textinput.value.nickname
			});
		}
		if (textinput.value.phone) {
			selectwhere.value.where.push({
				"column": "`user`.phone",
				"type": "like",
				"value": textinput.value.phone
			});
		}
		if (textinput.value.type !== null && textinput.value.type !== undefined && textinput.value.type !== 0) {
			selectwhere.value.where.push({
				"column": "type",
				"type": "eq",
				"value": textinput.value.type
			});
		}
		if (textinput.value.status !== null && textinput.value.status !== undefined && textinput.value.status !== '') {
			selectwhere.value.where.push({
				"column": "user_feedback.status",
				"type": "eq",
				"value": textinput.value.status
			});
		}
		
		selectwhere.value.page = 1;
		handleData();
	}

	async function handleData() {
		try {
			console.log('搜索条件:', selectwhere.value.where);
			let res = await FeedbackApi.selectfeedbackforadmin(selectwhere.value);
			console.log('获取反馈数据结果:', res);
			if (res.code === 200 && res.data && res.data.pageInfo) {
				tableData.value = res.data.pageInfo.list;
				total.value = res.data.pageInfo.total;
			} else {
				ElMessage.error(res.message || '获取反馈数据失败');
			}
		} catch (error) {
			console.error("获取反馈数据失败:", error);
			ElMessage.error("获取反馈数据失败");
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
	ElMessageBox.confirm('确认删除该反馈吗?', '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning'
	}).then(async () => {
		try {
			const res = await FeedbackApi.deleteFeedback(id);
			if (res.code === 200) {
				ElMessage.success('删除成功');
				handleData();
			} else {
				ElMessage.error(res.message || '删除失败');
			}
		} catch (error) {
			ElMessage.error('删除失败');
		}
	});
}

let dialogViewVisible = ref(false);
	let editForm = ref({
		id: null,
		content: '',
		status: 0
	});

	// 修改状态
	async function handleEdit(row) {
		editForm.value = {
			id: row.id,
			content: row.content,
			status: row.status
		};
		dialogViewVisible.value = true;
	}

	// 保存状态
	async function handleSaveStatus() {
		try {
			const res = await FeedbackApi.updateFeedbackStatus({
				id: editForm.value.id,
				status: editForm.value.status
			});
			if (res.code === 200) {
				ElMessage.success('修改成功');
				dialogViewVisible.value = false;
				handleData();
			} else {
				ElMessage.error(res.message || '修改失败');
			}
		} catch (error) {
			ElMessage.error('修改失败');
		}
	}

	// 回复
	async function handleReply(row) {
		try {
			let res = await FeedbackApi.getFeedbackById(row.id);
			if (res.code === 200) {
				dialogReplyVisible.value = true;
				replyForm.value = {
					id: res.data.entity.id,
					content: res.data.entity.content,
					status: res.data.entity.status === 'pending' ? 'processing' : res.data.entity.status,
					reply: res.data.entity.reply || ''
				};
			} else {
				ElMessage.error(res.message || '获取反馈详情失败');
			}
		} catch (error) {
			console.error('获取反馈详情失败:', error);
			ElMessage.error('获取反馈详情失败');
		}
	}

	// 确认回复
	async function handleConfirmReply() {
		try {
			let rs = await FeedbackApi.updateFeedbackStatus({
				id: replyForm.value.id,
				status: replyForm.value.status,
				reply: replyForm.value.reply
			});
			if (rs.code === 200) {
				ElMessage.success("回复成功");
				handleData();
				dialogReplyVisible.value = false;
			} else {
				ElMessage.error(rs.message || '回复失败');
			}
		} catch (error) {
			console.error('回复反馈错误:', error);
			ElMessage.error('回复失败: ' + (error.message || '未知错误'));
		}
	}

	// 重新加载所有信息
	function restake() {
		textinput.value.nickname = "";
		textinput.value.phone = "";
		textinput.value.type = "";
		textinput.value.status = "";
		sharebylike();
	}
</script>

<style>
	.pagination-container {
		display: flex;
		justify-content: center;
		margin-top: 20px;
	}

	.feedback-detail {
		padding: 10px 0;
	}

	.detail-item {
		display: flex;
		margin-bottom: 15px;
		padding-bottom: 10px;
		border-bottom: 1px solid #f0f0f0;
	}

	.detail-item.full-width {
		flex-direction: column;
	}

	.detail-item .label {
		width: 100px;
		font-weight: bold;
		color: #666;
		flex-shrink: 0;
	}

	.detail-item .value {
		flex: 1;
		color: #333;
	}

	.detail-item .content-value {
		flex: 1;
		color: #333;
		line-height: 1.6;
		white-space: pre-wrap;
		word-break: break-all;
		background: #f5f7fa;
		padding: 10px;
		border-radius: 4px;
		margin-top: 5px;
	}

	.feedback-content {
		background: #f5f7fa;
		padding: 10px;
		border-radius: 4px;
		line-height: 1.6;
		white-space: pre-wrap;
		word-break: break-all;
		color: #666;
	}
</style>