<template>
	<div class="complaint-management">
		<el-card>
			<template #header>
				<div class="card-header">
					<span class="title">投诉管理</span>
				</div>
			</template>

			<el-table :data="complaintList" style="width: 100%" v-loading="loading">
				<el-table-column label="投诉人" prop="userName" width="120">
					<template #default="{ row }">
						<div class="user-info">
							<span class="nickname">{{ row.username }}</span>
						</div>
					</template>
				</el-table-column>

				<el-table-column label="手机号" prop="phonenum" width="120"></el-table-column>

				<el-table-column label="订单编号" prop="orderNo" width="200"></el-table-column>

				<el-table-column label="投诉类型" width="100">
					<template #default="{ row }">
						<el-tag>{{ getComplaintTypeText(row.complaintType) }}</el-tag>
					</template>
				</el-table-column>

				<el-table-column label="投诉内容" prop="content" min-width="200" show-overflow-tooltip></el-table-column>

				<el-table-column label="投诉时间" prop="createTime" width="180">
					<template #default="{ row }">
						{{ formatDate(row.createTime) }}
					</template>
				</el-table-column>

				<el-table-column label="状态" width="100" align="center">
					<template #default="{ row }">
						<el-tag :type="getStatusType(row.status)">
							{{ getStatusText(row.status) }}
						</el-tag>
					</template>
				</el-table-column>

				<el-table-column label="操作" width="200" align="center" fixed="right">
					<template #default="{ row }">
						<el-dropdown trigger="click" @command="(command) => handleCommand(command, row)">
							<el-button type="primary" size="small">
								操作<el-icon class="el-icon--right"><arrow-down /></el-icon>
							</el-button>
							<template #dropdown>
								<el-dropdown-menu>
									<el-dropdown-item command="updateStatus">
										<el-icon><Edit /></el-icon>修改状态
									</el-dropdown-item>
									<el-dropdown-item command="delete" :disabled="row.status === 0">
										<el-icon><Delete /></el-icon>{{ row.status === 0 ? '待处理不能删除' : '删除' }}
									</el-dropdown-item>
								</el-dropdown-menu>
							</template>
						</el-dropdown>
					</template>
				</el-table-column>
			</el-table>

			<el-pagination
				v-model:current-page="currentPage"
				v-model:page-size="pageSize"
				:page-sizes="[10, 20, 50, 100]"
				:total="total"
				layout="total, sizes, prev, pager, next, jumper"
				@size-change="handleSizeChange"
				@current-change="handleCurrentChange"
				style="margin-top: 20px; justify-content: flex-end;"
			/>
		</el-card>

		<!-- 状态修改对话框 -->
		<el-dialog
			v-model="statusDialogVisible"
			title="修改投诉状态"
			width="400px"
			:close-on-click-modal="false"
		>
			<el-form label-width="80px">
				<el-form-item label="投诉状态">
					<el-select
						v-model="selectedStatus"
						placeholder="请选择状态"
						style="width: 100%;"
					>
						<el-option
							v-for="option in statusOptions"
							:key="option.value"
							:label="option.label"
							:value="option.value"
						/>
					</el-select>
				</el-form-item>
			</el-form>
			<template #footer>
				<el-button @click="cancelUpdateStatus">取消</el-button>
				<el-button type="primary" @click="confirmUpdateStatus">确定</el-button>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Picture, Edit, Delete, ArrowDown } from '@element-plus/icons-vue';
import ComplaintApi from '../../../../api/ComplaintApi';

const loading = ref(false);
const complaintList = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 状态修改对话框相关
const statusDialogVisible = ref(false);
const currentComplaint = ref(null);
const selectedStatus = ref(0);

//从本地存储获取商户ID
const shopId = localStorage.getItem('shopid');

const loadComplaints = async () => {
	loading.value = true;
	try {
		const res = await ComplaintApi.getComplaintList({
			page: currentPage.value,
			pageSize: pageSize.value,
			shopId: shopId
		});
		console.log('获取投诉列表响应:', res);
		if (res.code === 200 && res.data) {
			if (res.data.pageInfo) {
				complaintList.value = res.data.pageInfo.list;
				total.value = res.data.pageInfo.total;
			} else if (res.data.list) {
				complaintList.value = res.data.list;
				total.value = res.data.total;
			}
		} else {
			ElMessage.error(res.message || '获取投诉列表失败');
		}
	} catch (error) {
		console.error('获取投诉列表失败:', error);
		ElMessage.error('获取投诉列表失败');
	} finally {
		loading.value = false;
	}
};

const getImageUrl = (imageUrls) => {
	if (!imageUrls) return '';
	const urls = imageUrls.split(',');
	return urls[0] ? 'http://localhost:8080/imeageserver/' + urls[0] : '';
};

const getImageList = (imageUrls) => {
	if (!imageUrls) return [];
	const urls = imageUrls.split(',');
	return urls.map(url => 'http://localhost:8080/imeageserver/' + url);
};

const formatDate = (date) => {
	if (!date) return '-';
	const d = new Date(date);
	const year = d.getFullYear();
	const month = String(d.getMonth() + 1).padStart(2, '0');
	const day = String(d.getDate()).padStart(2, '0');
	const hours = String(d.getHours()).padStart(2, '0');
	const minutes = String(d.getMinutes()).padStart(2, '0');
	return `${year}-${month}-${day} ${hours}:${minutes}`;
};

const getStatusType = (status) => {
	const typeMap = {
		0: 'warning',
		1: 'success',
		2: 'info'
	};
	return typeMap[status] || 'info';
};

const getStatusText = (status) => {
	const textMap = {
		0: '待处理',
		1: '已处理',
		2: '已关闭'
	};
	return textMap[status] || '未知';
};

const getComplaintTypeText = (type) => {
	const typeMap = {
		1: '菜品问题',
		2: '服务问题',
		3: '配送问题',
		4: '其他'
	};
	return typeMap[type] || '其他';
};

// 状态选项
const statusOptions = [
	{ label: '待处理', value: 0 },
	{ label: '已处理', value: 1 },
	{ label: '已关闭', value: 2 }
];

const handleDelete = async (id) => {
	try {
		await ElMessageBox.confirm('确认删除该投诉吗?', '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning'
		});
		const res = await ComplaintApi.deleteComplaint(id);
		if (res.code === 200) {
			ElMessage.success('删除成功');
			loadComplaints();
		} else {
			ElMessage.error(res.message || '删除失败');
		}
	} catch (error) {
		if (error !== 'cancel') {
			console.error('删除失败:', error);
			ElMessage.error('删除失败');
		}
	}
};

const handleCommand = async (command, row) => {
	if (command === 'updateStatus') {
		handleUpdateStatus(row);
	} else if (command === 'delete') {
		handleDelete(row.id);
	}
};

const handleUpdateStatus = (row) => {
	currentComplaint.value = row;
	selectedStatus.value = row.status || 0;
	statusDialogVisible.value = true;
};

const confirmUpdateStatus = async () => {
	if (!currentComplaint.value) return;

	try {
		const res = await ComplaintApi.updateStatus({
			id: currentComplaint.value.id,
			status: selectedStatus.value
		});

		if (res.code === 200) {
			ElMessage.success('状态修改成功');
			statusDialogVisible.value = false;
			loadComplaints();
		} else {
			ElMessage.error(res.message || '状态修改失败');
		}
	} catch (error) {
		console.error('状态修改失败:', error);
		ElMessage.error('状态修改失败');
	}
};

const cancelUpdateStatus = () => {
	statusDialogVisible.value = false;
	currentComplaint.value = null;
	selectedStatus.value = 0;
};

const handleSizeChange = (val) => {
	pageSize.value = val;
	loadComplaints();
};

const handleCurrentChange = (val) => {
	currentPage.value = val;
	loadComplaints();
};

onMounted(() => {
	loadComplaints();
});
</script>

<style scoped>
.complaint-management {
	padding: 20px;
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.title {
	font-size: 18px;
	font-weight: bold;
}

.user-info {
	display: flex;
	align-items: center;
	gap: 8px;
}

.nickname {
	font-weight: 500;
}

.scores div {
	margin: 2px 0;
}

.image-error {
	display: flex;
	justify-content: center;
	align-items: center;
	width: 100%;
	height: 100%;
	background-color: #f5f7fa;
	color: #909399;
	font-size: 20px;
}
</style>