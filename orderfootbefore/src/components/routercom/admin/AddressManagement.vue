<template>
<div class="div1">
		<fieldset class="my-fieldset">
			<legend class="my-legend">条件查询:</legend>
			收货人:<el-input v-model="textinput.receiver" placeholder="请输入收货人:" style="width: 200px;"></el-input>
			省份:<el-input v-model="textinput.province" placeholder="请输入省份:" style="width: 200px;"></el-input>
			是否默认地址:<el-select v-model="textinput.isDefault" style="width: 200px">
				<el-option value="0" label="否"></el-option>
				<el-option value="1" label="是"></el-option>
			</el-select>
			<el-button type="primary" @click="sharebylike()">搜索</el-button>
			<el-button @click="restake()">重置</el-button>
			<el-button type="primary" @click="downexcel">导出excel</el-button>
		</fieldset>
	</div>
	<el-table :data="tableData" style="width: 95%;margin-left: 10px;">
		<el-table-column prop="id" label="序号" width="60px" />
		<el-table-column prop="receiver" label="收货人" />
		<el-table-column prop="phone" label="手机号" />
		<el-table-column prop="city" label="城市" />
		<el-table-column prop="district" label="区域" />
		<el-table-column label="是否默认">
			<template #default="scope">
				<span v-if="scope.row.isDefault==0">否</span>
				<span v-else-if="scope.row.isDefault==1">是</span>
				<span v-else>未知状态</span>
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
		<el-pagination background layout="prev, pager, next" :total="total" :page-size="6"
			@current-change="handchange" />
	</div>
	<teleport to="body">
		<el-dialog v-model="dialogFormVisible" title="添加地址" width="500">
			<el-form :model="form" :rules="loginRules">
				<el-form-item label="用户" prop="userId">
				<el-select v-model="form.userId" filterable>
					<el-option v-for="nickname in nicknamemenu" :value="nickname.id" :label="nickname.nickname" key=""></el-option>
				</el-select>
				</el-form-item>
				<el-form-item label="收货人" prop="receiver">
					<el-input v-model="form.receiver" placeholder="请输入收货人" prefix-icon="User" clearable>
					</el-input>
				</el-form-item>
				<el-form-item label="联系电话" prop="phone">
					<el-input v-model="form.phone" placeholder="请输入联系电话" prefix-icon="Phone" clearable>
					</el-input>
				</el-form-item>
				<el-form-item label="地址">
					 <el-cascader
	      v-model="form.city"
	      :options="options"
	      :props="defaultprops"
	      @change="(value) => handleChange(value, 'add')"
	    />
				</el-form-item>
				<el-form-item label="详细地址" prop="detailAddress">
					<el-input v-model="form.detailAddress" placeholder="请输入详细地址" prefix-icon="address" clearable>
					</el-input>
				</el-form-item>
				<el-form-item label="是否设为默认">
					<el-radio-group v-model="form.isDefault">
						<el-radio value="0">否</el-radio>
						<el-radio value="1">是</el-radio>
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
		<el-dialog v-model="dialogFormVisibleup" title="修改地址" width="500">
				<el-form :model="formup" :rules="loginRules">
					<el-form-item label="用户" prop="userId">
					<el-select v-model="formup.userId" filterable>
						<el-option v-for="nickname in nicknamemenu" :value="nickname.id" :label="nickname.nickname" key=""></el-option>
					</el-select>
					</el-form-item>
					<el-form-item label="收货人" prop="receiver">
						<el-input v-model="formup.receiver" placeholder="请输入收货人" prefix-icon="User" clearable>
						</el-input>
					</el-form-item>
					<el-form-item label="联系电话" prop="phone">
						<el-input v-model="formup.phone" placeholder="请输入联系电话" prefix-icon="Phone" clearable>
						</el-input>
					</el-form-item>
					<el-form-item label="地址" prop="city">
						 <el-cascader
			  v-model="formup.city"
			  :options="options"
			  :props="defaultprops"
			  @change="(value) => handleChange(value, 'edit')"
			/>
					</el-form-item>
					<el-form-item label="详细地址" prop="detailAddress">
						<el-input v-model="formup.detailAddress" placeholder="请输入详细地址" prefix-icon="address" clearable>
						</el-input>
					</el-form-item>
					<el-form-item label="是否设为默认">
						<el-radio-group v-model="formup.isDefault">
							<el-radio value="0">否</el-radio>
							<el-radio value="1">是</el-radio>
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
	import { regionData } from 'element-china-area-data';
	import AddressApi from '../../../../api/AddressApi'
	import {
		ElMessageBox,
		ElMessage
	} from 'element-plus'
	import UserApi from '../../../../api/UserApi.js'
	let nicknamemenu=ref();
	let savetext = ref("添加")
	let dialogFormVisible = ref(false);
	let options=ref();
	let defaultprops=ref({ value: 'label', label: 'label', checkStrictly: true });
	let textinput = ref({
		receiver: '',
		province: '',
		isDefault: '',
	});
	const tableData = ref([{
		id: '',
		receiver: '',
		phone: '',
		province: '',
		city: '',
	}])

	let form = ref({
		id: '',
		userId:'',
		receiver: '',
		phone: '',
		city: '',
		district:'',
		detailAddress: '',
		isDefault: '0'
	})
	const loginRules = {
		userId:{
			required:true,
			message: '请输入用户',
			trigger: 'blur'
		},
		receiver: [{
			required: true,
			message: '请输入部门名字',
			trigger: 'blur'
		}],
		phone: [{
			required: true,
			message: '请输入部门编码',
			trigger: 'blur'
		}],
		city: [{
			required: true,
			message: '请选择地址',
			trigger: 'blur'
		}],
		detailAddress: [{
			required: true,
			message: '请输入详细信息',
			trigger: 'blur'
		}]
	};
	let total = ref(0);
	const selectwhere = ref({
		page: 1,
		where: []
	})

	function sharebylike() {
		selectwhere.value.where = [{
			"column": "receiver",
			"type": "like",
			"value": textinput.value.receiver
		}, {
			"column": "province",
			"type": "like",
			"value": textinput.value.province
		}, {
			"column": "is_default",
			"type": "like",
			"value": textinput.value.isDefault
		}];
		handleData();
	}

	async function handleData() {
		try {
			console.log(regionData)
			let res = await AddressApi.AddressSelect(selectwhere.value);
			tableData.value = res.data.pageInfo.list;
			total.value = res.data.pageInfo.total;
		} catch (error) {
			console.error("获取用户数据失败:", error);
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
		ElMessageBox.confirm('确定要删除这个地址吗?', '警告', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning'
		}).then(async () => {
			try {
				let rs = await AddressApi.DelAddress(id);
				if (rs.code === 200) {
					ElMessage.success(rs.data.status || '删除成功');
					handleData(); // 重新加载数据
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

	async function handleadd() {
		let rs = await AddressApi.AddAddress(form.value);
		if (rs.code == 200) {
			ElMessage.success("添加成功");
			handleData();
			dialogFormVisible.value = false;
		}
	}

	async function takeadd() {
		savetext.value = "添加"
		dialogFormVisible.value = true;
		form.value.id = "";
		form.value.receiver = "";
		form.value.phone = "";
		form.value.city = "";
		form.value.district = "";
		form.value.detailAddress="";
		form.value.isDefault = "0";
		options.value=regionData;
		let res=await UserApi.UserSelect();
		nicknamemenu.value=res.data.pageInfo.list;
	}
	let dialogFormVisibleup = ref(false);
	let formup = ref({
		id: '',
		userId:'',
		receiver: '',
		phone: '',
		city: '',
		district:'',
		detailAddress: '',
		isDefault: '0'
	})

	async function handleEdit(row) {
		let res2 = await AddressApi.selectbyid(row.id);
		dialogFormVisibleup.value = true;
		options.value=regionData;
		let res=await UserApi.UserSelect();
		nicknamemenu.value=res.data.pageInfo.list;
		console.log("修改查询对象:", res2.data.entity);
		
		// 将数据库中的city和district转换为级联菜单需要的数组格式
		const entity = res2.data.entity;
		let cascaderValue = [];
		
		// 在regionData中查找匹配的地址路径
		if (entity.city) {
			for (const province of regionData) {
				if (province.children) {
					for (const city of province.children) {
						if (city.label === entity.city) {
							// 找到城市，先设置省份和城市
							cascaderValue = [province.label, city.label];
							
							// 尝试查找匹配的区域
							if (entity.district && city.children) {
								const foundDistrict = city.children.find(district => district.label === entity.district);
								if (foundDistrict) {
									cascaderValue.push(foundDistrict.label);
								} else {
									// 如果找不到完全匹配的区域，尝试部分匹配或留空
									cascaderValue.push('');
								}
							} else if (city.children && city.children.length > 0) {
								// 如果没有district数据，使用第一个区域作为默认值
								cascaderValue.push(city.children[0].label);
							} else {
								cascaderValue.push('');
							}
							break;
						}
					}
					if (cascaderValue.length > 0) break;
				}
			}
		}
		
		// 如果没找到匹配的城市，设置空数组
		if (cascaderValue.length === 0) {
			cascaderValue = ['', '', ''];
		}
		
		formup.value = {
			...entity,
			isDefault: String(entity.isDefault),
			city: cascaderValue, // 设置级联菜单的值（数组格式）
			_city: entity.city, // 保持原始城市字段
			_district: entity.district // 保持原始区域字段
		}
	}

	async function handlechange() {
		// 准备发送的数据，将数组格式的city转换为数据库需要的格式
		const dataToSend = {
			...formup.value,
			city: formup.value._city || (Array.isArray(formup.value.city) ? formup.value.city[1] : formup.value.city),
			district: formup.value._district || (Array.isArray(formup.value.city) ? formup.value.city[2] : '')
		};
		// 删除临时字段
		delete dataToSend._city;
		delete dataToSend._district;
		
		let rs = await AddressApi.UpdateAddress(dataToSend);
		if (rs.code == 200) {
			ElMessage.success("修改成功");
			handleData();
			dialogFormVisibleup.value = false;
		}

	}
	//重新加载所有信息
	function restake(){
		textinput.value.receiver="";
		textinput.value.province="";
		textinput.value.isDefault="";
		sharebylike();
	}
	function handleChange(value, formType = 'add') {
  // 获取选中的城市和区域
  const selectedNodes = options.value.find(node => node.label === value[0]);
  if (selectedNodes) {
    if (formType === 'add') {
      form.value.city = value[1]; // 城市
      form.value.district = value[2] || ''; // 区域
    } else if (formType === 'edit') {
      // 对于修改表单，city字段存储的是数组格式，需要同时更新数组和数据库字段
      formup.value.city = value; // 保持数组格式用于级联菜单显示
      // 同时更新数据库需要的字段
      if (value.length >= 2) {
        formup.value._city = value[1]; // 实际的城市名
        formup.value._district = value[2] || ''; // 实际的区域名
      }
    }
  }
}
import * as XLSX from 'xlsx'
async function downexcel(){
  try {
    console.log("开始导出Excel...");
    
    // 获取数据
    let res1 = await AddressApi.selectall();
    console.log("获取数据结果:", res1);
  let  alllist = res1.data.list;
    console.log("数据列表:", alllist);
    
    // 确保有数据
    if (!alllist || alllist.length === 0) {
      console.log("没有数据可导出");
      return;
    }
    
    // 获取表头
    let res = await AddressApi.loadbiao();
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
    
    XLSX.writeFile(wb, "所有地址数据.xlsx");
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
</style>