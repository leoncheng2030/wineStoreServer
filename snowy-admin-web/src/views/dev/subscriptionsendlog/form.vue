<template>
	<xn-form-container
		:title="formData.id ? '编辑订阅消息发送记录表' : '增加订阅消息发送记录表'"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="用户ID：" name="clientUserId">
						<a-input v-model:value="formData.clientUserId" placeholder="请输入用户ID" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="微信openid：" name="openid">
						<a-input v-model:value="formData.openid" placeholder="请输入微信openid" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="模板ID：" name="templateId">
						<a-input v-model:value="formData.templateId" placeholder="请输入模板ID" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="关联的站内信ID：" name="messageId">
						<a-input v-model:value="formData.messageId" placeholder="请输入关联的站内信ID" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="发送的数据内容JSON：" name="sendData">
						<a-input v-model:value="formData.sendData" placeholder="请输入发送的数据内容JSON" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="发送状态(1成功/0失败)：" name="sendStatus">
						<a-input v-model:value="formData.sendStatus" placeholder="请输入发送状态(1成功/0失败)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="微信返回的消息ID：" name="wechatMsgId">
						<a-input v-model:value="formData.wechatMsgId" placeholder="请输入微信返回的消息ID" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="错误码：" name="errorCode">
						<a-input v-model:value="formData.errorCode" placeholder="请输入错误码" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="错误信息：" name="errorMsg">
						<a-input v-model:value="formData.errorMsg" placeholder="请输入错误信息" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="发送时间：" name="sendTime">
						<a-date-picker v-model:value="formData.sendTime" value-format="YYYY-MM-DD HH:mm:ss" show-time placeholder="请选择发送时间" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="微信接口返回数据：" name="responseData">
						<a-input v-model:value="formData.responseData" placeholder="请输入微信接口返回数据" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="业务类型：" name="businessType">
						<a-input v-model:value="formData.businessType" placeholder="请输入业务类型" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="业务ID：" name="businessId">
						<a-input v-model:value="formData.businessId" placeholder="请输入业务ID" allow-clear />
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

<script setup name="wechatSubscriptionSendLogForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import wechatSubscriptionSendLogApi from '@/api/dev/wechatSubscriptionSendLogApi'
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
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				const formDataParam = cloneDeep(formData.value)
				wechatSubscriptionSendLogApi
					.wechatSubscriptionSendLogSubmitForm(formDataParam, formDataParam.id)
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
