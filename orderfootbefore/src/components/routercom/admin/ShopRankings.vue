<template>
	<div class="shop-rankings">
		<el-card>
			<template #header>
				<div class="card-header">
					<span class="title">店铺排行榜</span>
					<el-radio-group v-model="rankingType" @change="loadRankings">
						<el-radio-button value="revenue">营业额排行</el-radio-button>
						<el-radio-button value="orders">订单数排行</el-radio-button>
						<el-radio-button value="rating">评分排行</el-radio-button>
					</el-radio-group>
				</div>
			</template>

			<el-table :data="rankingList" style="width: 100%" v-loading="loading">
				<el-table-column label="排名" width="80" align="center">
					<template #default="{ $index }">
						<span v-if="$index === 0" class="rank-icon">🥇</span>
						<span v-else-if="$index === 1" class="rank-icon">🥈</span>
						<span v-else-if="$index === 2" class="rank-icon">🥉</span>
						<span v-else class="rank-number">{{ $index + 1 }}</span>
					</template>
				</el-table-column>

				<el-table-column label="店铺名称" prop="shopName" min-width="150">
					<template #default="{ row }">
						<div class="shop-info">
							<el-avatar :src="row.logo ? 'http://localhost:8080/imeageserver/'+row.logo : ''" :size="40" class="shop-avatar"></el-avatar>
							<span class="shop-name">{{ row.shopName }}</span>
						</div>
					</template>
				</el-table-column>

				<el-table-column label="营业额" prop="revenue" v-if="rankingType === 'revenue'" align="right">
					<template #default="{ row }">
						<span class="amount">¥{{ formatNumber(row.revenue) }}</span>
					</template>
				</el-table-column>

				<el-table-column label="订单数" prop="orderCount" v-if="rankingType === 'orders'" align="right">
					<template #default="{ row }">
						<span class="order-count">{{ row.orderCount }} 单</span>
					</template>
				</el-table-column>

				<el-table-column label="评分" prop="rating" v-if="ratingType === 'rating'" align="center">
					<template #default="{ row }">
						<el-rate v-model="row.rating" disabled show-score text-color="#ff9900"></el-rate>
					</template>
				</el-table-column>

				<el-table-column label="客单价" prop="avgOrderValue" align="right" width="120">
					<template #default="{ row }">
						<span class="amount">¥{{ formatNumber(row.avgOrderValue) }}</span>
					</template>
				</el-table-column>

				<el-table-column label="状态" prop="status" align="center" width="100">
					<template #default="{ row }">
						<el-tag :type="getStatusType(row.status)">
							{{ getStatusText(row.status) }}
						</el-tag>
					</template>
				</el-table-column>

				<el-table-column label="创建时间" prop="createTime" align="center" width="180">
					<template #default="{ row }">
						{{ formatDate(row.createTime) }}
					</template>
				</el-table-column>
			</el-table>
		</el-card>
	</div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import ShopRankingsApi from '../../../../api/ShopRankingsApi';

const rankingType = ref('revenue');
const rankingList = ref([]);
const loading = ref(false);

const loadRankings = async () => {
	loading.value = true;
	try {
		let res;
		if (rankingType.value === 'revenue') {
			res = await ShopRankingsApi.getRevenueRankings();
		} else if (rankingType.value === 'orders') {
			res = await ShopRankingsApi.getOrderRankings();
		} else if (rankingType.value === 'rating') {
			res = await ShopRankingsApi.getRatingRankings();
		}

		if (res.code === 200 && res.data) {
			if (res.data.shops) {
				rankingList.value = res.data.shops;
			} else if (res.data.list) {
				rankingList.value = res.data.list;
			} else if (Array.isArray(res.data)) {
				rankingList.value = res.data;
			}
		} else {
			ElMessage.error(res.message || '获取排行榜数据失败');
		}
	} catch (error) {
		console.error('获取排行榜数据失败:', error);
		ElMessage.error('获取排行榜数据失败');
	} finally {
		loading.value = false;
	}
};

const formatNumber = (num) => {
	if (num === null || num === undefined) return '0.00';
	return Number(num).toFixed(2);
};

const getStatusType = (status) => {
	const typeMap = {
		0: 'info',
		1: 'success',
		2: 'warning',
		3: 'danger'
	};
	return typeMap[status] || 'info';
};

const getStatusText = (status) => {
	const textMap = {
		0: '暂停营业',
		1: '正常营业',
		2: '封禁',
		3: '已删除'
	};
	return textMap[status] || '未知';
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

onMounted(() => {
	loadRankings();
});
</script>

<style scoped>
.shop-rankings {
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

.shop-info {
	display: flex;
	align-items: center;
	gap: 10px;
}

.shop-avatar {
	flex-shrink: 0;
}

.shop-name {
	font-weight: 500;
}

.rank-icon {
	font-size: 24px;
}

.rank-number {
	font-size: 16px;
	font-weight: bold;
	color: #606266;
}

.amount {
	color: #f56c6c;
	font-weight: bold;
}

.order-count {
	color: #409eff;
	font-weight: bold;
}
</style>