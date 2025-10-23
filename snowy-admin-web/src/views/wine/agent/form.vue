<template>
	<xn-form-container
		:title="formData.id ? '编辑代理商' : '增加代理商'"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="用户ID：" name="clientUserId">
						<a-input v-model:value="formData.clientUserId" placeholder="请输入用户ID" readOnly />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="代理商编号：" name="agentCode">
						<a-input v-model:value="formData.agentCode" placeholder="请输入代理商编号" readOnly />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="子商户号：" name="subMerId">
						<a-input v-model:value="formData.subMerId" placeholder="请输入子商户号" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="子应用ID：" name="subAppId">
						<a-input v-model:value="formData.subAppId" placeholder="请输入子应用ID" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="子商户分账比例：" name="profitSharingMaxRate">
						<a-input v-model:value="formData.profitSharingMaxRate" placeholder="请输入子商户分账比例" readOnly />
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

<script setup name="wineAgentForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import wineAgentApi from '@/api/wine/wineAgentApi'
	// 抽屉状态
	const open = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	const submitLoading = ref(false)

	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		if (record) {
			let recordData = cloneDeep(record)
			formData.value = Object.assign({}, recordData)
		}
	}
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		formData.value = {}
		open.value = false
	}
	// 默认要校验的
	const formRules = {
		clientUserId: [required('请输入用户ID')],
		agentCode: [required('请输入代理商编号')],
		subMerId: [required('请输入子商户号')]
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				const formDataParam = cloneDeep(formData.value)
				wineAgentApi
					.wineAgentSubmitForm(formDataParam, formDataParam.id)
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
