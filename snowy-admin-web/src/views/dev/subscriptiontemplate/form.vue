<template>
	<xn-form-container
		:title="formData.id ? '编辑微信订阅消息模板表' : '增加微信订阅消息模板表'"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="微信模板ID：" name="templateId">
						<a-input v-model:value="formData.templateId" placeholder="请输入微信模板ID" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="模板标题：" name="templateTitle">
						<a-input v-model:value="formData.templateTitle" placeholder="请输入模板标题" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="模板内容示例：" name="templateContent">
						<a-input v-model:value="formData.templateContent" placeholder="请输入模板内容示例" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="模板类型：" name="templateType">
						<a-select v-model:value="formData.templateType" placeholder="请选择模板类型" allow-clear>
							<a-select-option value="STOCK_ALERT">库存预警通知</a-select-option>
							<a-select-option value="ORDER_STATUS">订单状态通知</a-select-option>
							<a-select-option value="SYSTEM_NOTICE">系统通知</a-select-option>
							<a-select-option value="PAYMENT_SUCCESS">支付成功通知</a-select-option>
							<a-select-option value="REFUND_NOTICE">退款通知</a-select-option>
							<a-select-option value="ACTIVITY_NOTICE">活动通知</a-select-option>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="状态：" name="status">
						<a-select v-model:value="formData.status" placeholder="请选择状态" :options="statusOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="关键词配置JSON：" name="keywordConfig">
						<a-input v-model:value="formData.keywordConfig" placeholder="请输入关键词配置JSON" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="跳转页面路径：" name="jumpPage">
						<a-input v-model:value="formData.jumpPage" placeholder="请输入跳转页面路径" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="排序码：" name="sortCode">
						<a-input v-model:value="formData.sortCode" placeholder="请输入排序码" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="扩展信息：" name="extJson">
						<a-input v-model:value="formData.extJson" placeholder="请输入扩展信息" allow-clear />
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

<script setup name="wechatSubscriptionTemplateForm">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import wechatSubscriptionTemplateApi from '@/api/dev/wechatSubscriptionTemplateApi'
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
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				const formDataParam = cloneDeep(formData.value)
				wechatSubscriptionTemplateApi
					.wechatSubscriptionTemplateSubmitForm(formDataParam, formDataParam.id)
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
