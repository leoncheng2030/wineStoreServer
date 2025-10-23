<template>
	<xn-form-container
		:title="formData.id ? '编辑酒品分类表' : '增加酒品分类表'"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="分类名称：" name="categoryName">
						<a-input v-model:value="formData.categoryName" placeholder="请输入分类名称" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="父级分类ID，0表示顶级分类：" name="parentId">
						<a-input v-model:value="formData.parentId" placeholder="请输入父级分类ID，0表示顶级分类" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="分类级别，1为一级分类：" name="categoryLevel">
						<a-input v-model:value="formData.categoryLevel" placeholder="请输入分类级别，1为一级分类" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="分类描述：" name="description">
						<a-input v-model:value="formData.description" placeholder="请输入分类描述" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="状态：" name="status">
						<a-select v-model:value="formData.status" placeholder="请选择状态" :options="statusOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="排序码，数字越小越靠前：" name="sortCode">
						<a-input v-model:value="formData.sortCode" placeholder="请输入排序码，数字越小越靠前" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="备注：" name="remark">
						<a-input v-model:value="formData.remark" placeholder="请输入备注" allow-clear />
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

<script setup name="wineProductCategoryForm">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import wineProductCategoryApi from '@/api/wine/wineProductCategoryApi'
	// 抽屉状态
	const open = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	const submitLoading = ref(false)
	const statusOptions = ref([])

	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		if (record) {
			let recordData = cloneDeep(record)
			formData.value = Object.assign({}, recordData)
		}
		statusOptions.value = tool.dictList('COMMON_STATUS')
	}
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		formData.value = {}
		open.value = false
	}
	// 默认要校验的
	const formRules = {
		categoryName: [required('请输入分类名称')],
		parentId: [required('请输入父级分类ID，0表示顶级分类')],
		status: [required('请输入状态')],
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				const formDataParam = cloneDeep(formData.value)
				wineProductCategoryApi
					.wineProductCategorySubmitForm(formDataParam, formDataParam.id)
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
