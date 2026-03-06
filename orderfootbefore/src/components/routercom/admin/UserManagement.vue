<template>
<div class="div1">
		<fieldset class="my-fieldset">
			<legend class="my-legend">条件查询:</legend>
			手机号:<el-input v-model="textinput.phone" placeholder="请输入手机号:" style="width: 200px;"></el-input>
			昵称:<el-input v-model="textinput.nickname" placeholder="请输入昵称:" style="width: 200px;"></el-input>
			状态:<el-select v-model="textinput.status" style="width: 200px">
				<el-option value="" label="全部"></el-option>
				<el-option value="0" label="禁用"></el-option>
				<el-option value="1" label="正常"></el-option>
			</el-select>
			<el-button type="primary" @click="sharebylike()">搜索</el-button>
			<el-button @click="restake()">重置</el-button>
			<el-button type="primary" @click="downexcel">导出excel</el-button>
		</fieldset>
	</div>
	<el-table :data="tableData" style="width: 95%;margin-left: 10px;">
		<el-table-column prop="id" label="序号" width="60px" />
		<el-table-column prop="phone" label="手机号" />
		<el-table-column label="头像">
			<template #default="scope">
				<img :src="'http://localhost:8080/imeageserver/'+scope.row.avatar" class="avatar-table" />
			</template>
		</el-table-column>
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
		<el-dialog v-model="dialogFormVisible" title="添加用户" width="500">
			<el-form :model="form" :rules="loginRules">
				<el-form-item label="手机号" prop="phone">
					<el-input v-model="form.phone" placeholder="请输入手机号" prefix-icon="Phone" clearable>
					</el-input>
				</el-form-item>
				<el-form-item label="密码" prop="password">
					<el-input v-model="form.password" placeholder="请输入密码" prefix-icon="Lock" clearable show-password>
					</el-input>
				</el-form-item>
				<el-form-item label="昵称" prop="nickname">
					<el-input v-model="form.nickname" placeholder="请输入昵称" prefix-icon="User" clearable>
					</el-input>
				</el-form-item>
				<el-form-item label="性别">
					<el-radio-group v-model="form.gender">
						<el-radio :value="0">未知</el-radio>
						<el-radio :value="1">男</el-radio>
						<el-radio :value="2">女</el-radio>
					</el-radio-group>
				</el-form-item>
				<el-form-item label="状态">
					<el-radio-group v-model="form.status">
						<el-radio :value="0">禁用</el-radio>
						<el-radio :value="1">正常</el-radio>
					</el-radio-group>
				</el-form-item>
				<el-form-item label="上传图片">

	             <el-upload ref="uploadRef" action="http://localhost:8080/user/updateimage" :on-success="handleAvatarSuccess"  :show-file-list="false" list-type="picture-card" :limit="1" class="avatar" :on-remove="handleAvatarRemove">
	                  <div>
	                     <img v-if="imageUrl" :src="imageUrl" alt="" class="avatar-uploader-icon" style="max-width: 178px; max-height: 178px;"/>
	             	<el-icon v-else ><Plus /></el-icon>
	             	</div>
	               </el-upload>
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
		<el-dialog v-model="dialogFormVisibleup" title="修改用户" width="500">
				<el-form :model="formup" :rules="loginRules">
					<el-form-item label="手机号" prop="phone">
						<el-input v-model="formup.phone" placeholder="请输入手机号" prefix-icon="Phone" clearable>
						</el-input>
					</el-form-item>
					<el-form-item label="密码" prop="password">
						<el-input v-model="formup.password" placeholder="请输入密码" prefix-icon="Lock" clearable show-password>
						</el-input>
					</el-form-item>
					<el-form-item label="昵称" prop="nickname">
						<el-input v-model="formup.nickname" placeholder="请输入昵称" prefix-icon="User" clearable>
						</el-input>
					</el-form-item>
					<el-form-item label="性别">
						<el-radio-group v-model="formup.gender">
							<el-radio :value="0">未知</el-radio>
							<el-radio :value="1">男</el-radio>
							<el-radio :value="2">女</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="状态">
						<el-radio-group v-model="formup.status">
							<el-radio :value="0">禁用</el-radio>
							<el-radio :value="1">正常</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item  label="头像">
		<el-upload ref="uploadRefup" action="http://localhost:8080/imgupdate/updateimage" list-type="picture-card" :show-file-list="false" :on-success="handleAvatarSuccessup" :limit="1" :on-remove="handleAvatarRemoveup">
	        <img v-if="formup.avatar" class="avatar-upload" :src="'http://localhost:8080/imeageserver/'+formup.avatar" alt="" />
		</el-upload>
	   
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
	import * as XLSX from 'xlsx'
	import UserApi from '../../../../api/UserApi.js';
	import {
		ElMessageBox,
		ElMessage
	} from 'element-plus' 

     const imageUrl = ref('')
     const uploadRef = ref(null)
     const uploadRefup = ref(null)
     let imageUrlup=ref('formup.avatar')

const beforeAvatarUpload = (rawFile) => {
	console.log("上传的图片",rawFile);
  if (rawFile.type !== 'image/jpeg') {
    ElMessage.error('Avatar picture must be JPG format!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('Avatar picture size can not exceed 2MB!')
    return false
  }
  return true
}

	let dialogFormVisible = ref(false);
	let textinput = ref({
		phone: '',
		nickname: '',
		status: '',
	});
	const tableData = ref([])

	let form = ref({
		id: '',
		phone: '',
		password: '',
		nickname: '',
		gender: 0,
		status: 1,
		avatar:''
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
		selectwhere.value.where = [];
		if (textinput.value.phone) {
			selectwhere.value.where.push({
				"column": "phone",
				"type": "like",
				"value": textinput.value.phone
			});
		}
		if (textinput.value.nickname) {
			selectwhere.value.where.push({
				"column": "nickname",
				"type": "like",
				"value": textinput.value.nickname
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

	function del(index, id) {
		ElMessageBox.confirm('确定要删除这个用户吗?', '警告', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning'
		}).then(async () => {
			try {
				let rs = await UserApi.deleteUser(id);
				if (rs.code === 200) {
					ElMessage.success(rs.data.status || '删除成功');
					handleData(); // 重新加载数据
				} else {
					ElMessage.error(rs.message || '删除失败');
				}
			} catch (error) {
				console.error('删除用户错误:', error);
				ElMessage.error('删除失败: ' + (error.message || '未知错误'));
			}
		}).catch(() => {
			// 用户取消删除，不执行任何操作
		});
	}

	async function handleadd() {
		try {
			let rs = await UserApi.addUser(form.value);
			if (rs.code == 200) {
				ElMessage.success("添加成功");
				handleData();
				dialogFormVisible.value = false;
			} else {
				ElMessage.error(rs.message || '添加失败');
			}
		} catch (error) {
			console.error('添加用户错误:', error);
			ElMessage.error('添加失败: ' + (error.message || '未知错误'));
		}
	}

	async function takeadd() {
		imageUrl.value="";
		dialogFormVisible.value = true;
		form.value = {
			id: '',
			phone: '',
			password: '',
			nickname: '',
			gender: 0,
			status: 1
		};
	}
	
	let dialogFormVisibleup = ref(false);
	let formup = ref({
		id: '',
		phone: '',
		password: '',
		nickname: '',
		gender: 0,
		status: 1,
		avatar:""
	})

	async function handleEdit(row) {
		try {
			let res = await UserApi.getUserById(row.id);
			if (res.code === 200) {
				dialogFormVisibleup.value = true;
				formup.value = {
					...res.data.entity,
					password: res.data.entity.password || '' // 密码也要绑定上去
				};
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
	
	function handleAvatarSuccess(imgSuccess){
		imageUrl.value='http://localhost:8080/imeageserver/'+imgSuccess.data.imgpath;
     form.value.avatar = imgSuccess.data.imgpath;
     // 上传成功后清空文件列表，允许更换图片
     if (uploadRef.value) {
       uploadRef.value.clearFiles();
     }
	}

	function handleAvatarSuccessup(imgSuccessup){
	imageUrlup.value='http://localhost:8080/imeageserver/'+imgSuccessup.data.imgpath;
     formup.value.avatar=imgSuccessup.data.imgpath;
     // 上传成功后清空文件列表，允许更换图片
     if (uploadRefup.value) {
       uploadRefup.value.clearFiles();
     }
	}

	const handleAvatarRemove = () => {
  imageUrl.value = '';
  form.value.avatar = '';
   }

const handleAvatarRemoveup = () => {
  imageUrlup.value = '';
  formup.value.avatar = '';
}



async function downexcel(){
  try {
    console.log("开始导出Excel...");
    
    // 获取数据
    let res1 = await UserApi.selectall();
    console.log("获取数据结果:", res1);
  let  alllist = res1.data.list;
    console.log("数据列表:", alllist);
    
    // 确保有数据
    if (!alllist || alllist.length === 0) {
      console.log("没有数据可导出");
      return;
    }
    
    // 获取表头
    let res = await UserApi.loadbiao();
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
    
    XLSX.writeFile(wb, "所有用户数据.xlsx");
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
	.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}
.avatar-table {
  width: 50px;
  height: 50px;
  border-radius: 0;
  object-fit: cover;
  display: block;
}
</style>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}

.avatar-upload {
  max-width: 100%;
  max-height: 100%;
  width: auto;
  height: auto;
  border-radius: 6px;
  object-fit: cover;
}
</style>