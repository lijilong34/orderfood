<template>
	<div class="div1">
		<fieldset class="my-fieldset">
			<legend class="my-legend">条件查询:</legend>
			用户昵称:<el-input v-model="textinput.nickname" placeholder="请输入用户昵称:" style="width: 200px;"></el-input>
			店铺名称:<el-input v-model="textinput.shopName" placeholder="请输入店铺名称:" style="width: 200px;"></el-input>
			评价内容:<el-input v-model="textinput.content" placeholder="请输入评价内容:" style="width: 200px;"></el-input>
			<el-button type="primary" @click="sharebylike()">搜索</el-button>
			<el-button @click="restake()">重置</el-button>
			<el-button type="primary" @click="downexcel">导出excel</el-button>
		</fieldset>
	</div>
	<el-table :data="tableData" style="width: 95%;margin-left: 10px;">
		<el-table-column prop="id" label="序号" width="60px" />
		<el-table-column label="用户头像">
			<template #default="scope">
				<img :src="'http://localhost:8080/imeageserver/'+scope.row.avatar" width="50" height="50" v-if="scope.row.avatar"/>
				<span v-else>无头像</span>
			</template>
		</el-table-column>
		<el-table-column prop="nickname" label="用户昵称" />
		<el-table-column prop="shopName" label="店铺名称" />
		<el-table-column label="评分">
			<template #default="scope">
				<el-rate :model-value="calculateAverageRating(scope.row)" disabled show-score text-color="#ff9900" score-template="{value}分" />
				<div style="font-size: 12px; color: #666;">
					菜品:{{ scope.row.dishScore || 0 }} | 服务:{{ scope.row.serviceScore || 0 }} | 环境:{{ scope.row.environmentScore || 0 }}
				</div>
			</template>
		</el-table-column>
		<el-table-column prop="content" label="评价内容" width="300">
			<template #default="scope">
				<div class="review-content">{{ scope.row.content }}</div>
			</template>
		</el-table-column>
		<el-table-column label="评价图片">
			<template #default="scope">
				<div v-if="scope.row.imageUrls">
					<el-image
						:src="'http://localhost:8080/imeageserver/' + scope.row.imageUrls.split(',')[0]"
						:preview-src-list="scope.row.imageUrls.split(',').map(url => 'http://localhost:8080/imeageserver/' + url)"
						style="width: 50px; height: 50px; margin-right: 5px;"
						fit="cover"
					/>
					<span v-if="scope.row.imageUrls.split(',').length > 1">+{{ scope.row.imageUrls.split(',').length - 1 }}</span>
				</div>
				<span v-else>无图片</span>
			</template>
		</el-table-column>
		<el-table-column prop="createTime" label="评价时间" width="180">
			<template #default="scope">
				{{ formatDate(scope.row.createTime) }}
			</template>
		</el-table-column>
		<el-table-column label="操作" width="100">
			<template #default="scope">
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
</template>

<script setup>
import {
	ref,
	onMounted
} from 'vue'
import EvaluationApi from '../../../../api/EvaluationApi.js';
import {
	ElMessageBox,
	ElMessage
} from 'element-plus'

let textinput = ref({
	nickname: '',
	shopName: '',
	content: '',
});
const tableData = ref([])
const total = ref(0)
const selectwhere = ref({
	page: 1,
	where: []
})

function sharebylike() {
	selectwhere.value.where = [];
	if (textinput.value.nickname) {
		selectwhere.value.where.push({
			"column": "nickname",
			"type": "like",
			"value": textinput.value.nickname
		});
	}
	if (textinput.value.shopName) {
		selectwhere.value.where.push({
			"column": "name",
			"type": "like",
			"value": textinput.value.shopName
		});
	}
	if (textinput.value.content) {
		selectwhere.value.where.push({
			"column": "content",
			"type": "like",
			"value": textinput.value.content
		});
	}
	handleData();
}

async function handleData() {
	try {
		let res = await EvaluationApi.EvaluationSelect(selectwhere.value);
		console.log("获取评价数据成功:", res);
		tableData.value = res.data.pageInfo.list;
		total.value = res.data.pageInfo.total;
	} catch (error) {
		console.error("获取评价数据失败:", error);
		ElMessage.error("获取评价数据失败");
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
	ElMessageBox.confirm('确定要删除这条评价吗?', '警告', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning'
	}).then(async () => {
		try {
			let rs = await EvaluationApi.deleteEvaluation(id);
			if (rs.code === 200) {
				ElMessage.success(rs.data.status || '删除成功');
				handleData(); // 重新加载数据
			} else {
				ElMessage.error(rs.message || '删除失败');
			}
		} catch (error) {
			console.error('删除评价错误:', error);
			ElMessage.error('删除失败: ' + (error.message || '未知错误'));
		}
	}).catch(() => {
		// 用户取消删除，不执行任何操作
	});
}



function restake() {
	textinput.value.nickname = "";
	textinput.value.shopName = "";
	textinput.value.content = "";
	sharebylike();
}

// 计算平均评分
function calculateAverageRating(review) {
	if (!review) return 0;
	const dishScore = review.dishScore || 0;
	const serviceScore = review.serviceScore || 0;
	const environmentScore = review.environmentScore || 0;
	const average = (dishScore + serviceScore + environmentScore) / 3;
	return parseFloat(average.toFixed(2));
}

// 格式化日期
function formatDate(dateString) {
	if (!dateString) return '';
	const date = new Date(dateString);
	return date.toLocaleString('zh-CN', {
		year: 'numeric',
		month: '2-digit',
		day: '2-digit',
		hour: '2-digit',
		minute: '2-digit',
		second: '2-digit'
	});
}
import * as XLSX from 'xlsx'
async function downexcel(){
  try {
    console.log("开始导出Excel...");
    
    // 获取数据
    let res1 = await EvaluationApi.selectall();
    console.log("获取数据结果:", res1);
  let  alllist = res1.data.list;
    console.log("数据列表:", alllist);
    
    // 确保有数据
    if (!alllist || alllist.length === 0) {
      console.log("没有数据可导出");
      return;
    }
    
    // 获取表头
    let res = await EvaluationApi.loadbiao();
    console.log("获取表头结果:", res);
    
    // 创建工作簿
    const wb = XLSX.utils.book_new();
    
    // 检查是否有表头数据
    if (res && res.data && res.data.listbiao && Object.keys(res.data.listbiao).length > 0) {
      console.log("使用后端表头:", res.data.listbiao);
      
      // 获取后端返回的表头，按A1, B1, C1...顺序
      const headerEntries = Object.entries(res.data.listbiao)
        .sort(([keyA], [keyB]) => {
          // 按列字母排序：A1, B1, C1...
          const colA = keyA.replace(/\d+/, '');
          const colB = keyB.replace(/\d+/, '');
          return colA.localeCompare(colB);
        });
      
      // 提取表头名称
      const headers = headerEntries.map(([_, headerName]) => headerName);
      console.log("排序后的表头:", headers);
      
      // 使用aoa_to_sheet（数组的数组）方法，更可靠
      // 1. 创建表头行
      const dataArray = [headers];
      
      // 2. 添加数据行
      alllist.forEach(row => {
        // 将对象转换为数组，按表头顺序
        const rowArray = [];
        
        // 获取原始数据的值数组
        const rowValues = Object.values(row);
        
        // 按表头数量填充数据
        for (let i = 0; i < headers.length; i++) {
          if (i < rowValues.length) {
            rowArray.push(rowValues[i]);
          } else {
            rowArray.push(''); // 空值填充
          }
        }
        
        dataArray.push(rowArray);
      });
      
      console.log("生成的数据数组:", dataArray);
      
      // 创建工作表
      const ws = XLSX.utils.aoa_to_sheet(dataArray);
      XLSX.utils.book_append_sheet(wb, ws, "Sheet1");
      console.log("使用aoa_to_sheet成功");
    } else {
      console.log("后端表头为空，使用原始数据导出");
      // 确保至少有原始数据
      const ws = XLSX.utils.json_to_sheet(alllist);
      XLSX.utils.book_append_sheet(wb, ws, "Sheet1");
    }
    
    XLSX.writeFile(wb, "所有评论数据.xlsx");
    console.log("Excel文件生成成功");
    
  } catch (error) {
    console.error("导出Excel失败:", error);
  }
}
</script>

<style>
.pagination-container {
	display: flex;
	justify-content: center;
	margin-top: 20px;
}

.my-fieldset {
	border: 1px solid #dcdfe6;
	padding: 10px;
	margin: 10px;
	border-radius: 4px;
}

.my-legend {
	padding: 0 10px;
	color: #606266;
	font-size: 14px;
}

.review-content {
	word-break: break-word;
	line-height: 1.5;
	max-height: 60px;
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 3;
	-webkit-box-orient: vertical;
}

.div1 {
	margin: 10px;
}
</style>