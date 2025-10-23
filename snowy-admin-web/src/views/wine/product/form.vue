<template>
	<xn-form-container
		:title="formData.id ? '编辑酒品列表' : '增加酒品列表'"
		:width="900"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="24">
					<a-form-item label="酒品图片：" name="imageUrl">
						<xn-upload uploadMode="image" v-model:value="formData.imageUrl" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="分类ID：" name="categoryId">
						<a-select
							v-model:value="formData.categoryId"
							placeholder="请输入分类ID"
							:options="categoryIdOptions"
							:field-names="{ label: 'categoryName', value: 'id' }"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="酒品编码：" name="productCode">
						<a-input v-model:value="formData.productCode" placeholder="请输入酒品编码" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="酒品名称：" name="productName">
						<a-input v-model:value="formData.productName" placeholder="请输入酒品名称" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="酒精度数：" name="alcoholContent">
						<a-input v-model:value="formData.alcoholContent" placeholder="请输入酒精度数">
							<template #addonAfter> % </template>
						</a-input>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="产地：" name="origin">
						<a-input v-model:value="formData.origin" placeholder="请输入产地" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="生产厂家：" name="manufacturer">
						<a-input v-model:value="formData.manufacturer" placeholder="请输入生产厂家" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="成本价：" name="costPrice">
						<a-input v-model:value="formData.costPrice" placeholder="请输入成本价">
							<template #addonAfter> 元/ml </template>
						</a-input>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="零售单价：" name="unitPrice">
						<a-input v-model:value="formData.unitPrice" placeholder="请输入零售单价">
							<template #addonAfter> 元/ml </template>
						</a-input>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="状态：" name="status">
						<a-select v-model:value="formData.status" placeholder="请选择状态" :options="statusOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="24">
					<a-form-item label="备注：" name="remark">
						<a-textarea v-model:value="formData.remark" placeholder="请输入备注" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="24">
					<a-form-item label="酒品描述：" name="description">
						<xn-editor
							v-model:value="formData.description"
							placeholder="请输入酒品详细描述"
							:height="500"
							:file-upload-function="apiFunction.fileUploadApi"
						/>
					</a-form-item>
				</a-col>
			</a-row>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit" :loading="submitLoading">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="wineProductForm">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import wineProductApi from '@/api/wine/wineProductApi'
	import wineProductCategoryApi from '@/api/wine/wineProductCategoryApi'
	import XnEditor from '@/components/Editor/index.vue'
	import fileApi from "@/api/dev/fileApi";
	// 抽屉状态
	const open = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	const submitLoading = ref(false)
	const statusOptions = ref([])
	const categoryIdOptions = ref([])
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		loadCategoryTree()
		if (record) {
			let recordData = cloneDeep(record)
			formData.value = Object.assign({}, recordData)
		}
		statusOptions.value = tool.dictList('COMMON_STATUS')
	}
	// 获取酒品分类树
	const loadCategoryTree = () => {
		wineProductCategoryApi.wineProductCategoryTree().then((res) => {
			categoryIdOptions.value = res
		})
	}
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		formData.value = {}
		open.value = false
	}
	// 默认要校验的
	const formRules = {
		categoryId: [required('请输入分类ID')],
		productName: [required('请输入酒品名称')],
		alcoholContent: [required('请输入酒精度数')],
		costPrice: [required('请输入成本价')],
		unitPrice: [required('请输入零售单价')],
		imageUrl: [required('请输入酒品图片URL')],
		status: [required('请输入状态')]
	}
	// 传递文件上传需要的API
	const apiFunction = {
		fileUploadApi: (param) => {
			return fileApi.fileUploadDynamicReturnUrl(param).then((data) => {
				return Promise.resolve(data)
			})
		}
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				const formDataParam = cloneDeep(formData.value)
				wineProductApi
					.wineProductSubmitForm(formDataParam, formDataParam.id)
					.then(() => {
						onClose()
						emit('successful')
					})
					.finally(() => {
						submitLoading.value = false
					})
			})
			.catch(() => {})
	}
	// 抛出函数
	defineExpose({
		onOpen
	})
</script>
