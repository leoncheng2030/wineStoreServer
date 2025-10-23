<template>
	<xn-form-container
		:title="formData.id ? '编辑数据源' : '增加数据源'"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="名称：" name="poolName">
						<a-input v-model:value="formData.poolName" placeholder="请输入名称" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="分类：" name="category">
						<a-select v-model:value="formData.category" placeholder="请选择分类" :options="categoryOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="驱动名称：" name="driverName">
						<a-select v-model:value="formData.driverName" placeholder="请选择驱动名称" :options="driverNameOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="24">
					<a-form-item label="连接URL：" name="url">
						<a-textarea v-model:value="formData.url" placeholder="请输入连接URL" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="用户名：" name="username">
						<a-input v-model:value="formData.username" placeholder="请输入用户名" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="密码：" name="password">
						<a-input-password v-model="formData.password" type="password" placeholder="请输入密码"></a-input-password>
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

<script setup name="extDatabaseForm">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import extDatabaseApi from '@/api/dbs/extDatabaseApi'
	// 抽屉状态
	const open = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	const submitLoading = ref(false)
	const driverNameOptions = ref([])
	const categoryOptions = ref([])

	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		if (record) {
			let recordData = cloneDeep(record)
			formData.value = Object.assign({}, recordData)
		}
		driverNameOptions.value = tool.dictList('DATABASE_DRIVE_TYPE')
		categoryOptions.value = tool.dictList('DBS_CATEGORY')
	}
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		formData.value = {}
		open.value = false
	}
	// 默认要校验的
	const formRules = {
		poolName: [required('请输入名称')],
		url: [required('请输入连接URL')],
		username: [required('请输入用户名')],
		password: [required('请输入密码')],
		driverName: [required('请输入驱动名称')]
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				const formDataParam = cloneDeep(formData.value)
				extDatabaseApi
					.extDatabaseSubmitForm(formDataParam, formDataParam.id)
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
