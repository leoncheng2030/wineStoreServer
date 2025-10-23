<template>
	<xn-form-container
		:title="formData.id ? '编辑用户订阅消息授权表' : '增加用户订阅消息授权表'"
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
					<a-form-item label="订阅状态(1已授权/0已取消)：" name="subscriptionStatus">
						<a-input v-model:value="formData.subscriptionStatus" placeholder="请输入订阅状态(1已授权/0已取消)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="授权时间：" name="subscriptionTime">
						<a-date-picker v-model:value="formData.subscriptionTime" value-format="YYYY-MM-DD HH:mm:ss" show-time placeholder="请选择授权时间" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="过期时间：" name="expireTime">
						<a-date-picker v-model:value="formData.expireTime" value-format="YYYY-MM-DD HH:mm:ss" show-time placeholder="请选择过期时间" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="剩余发送次数：" name="remainingTimes">
						<a-input v-model:value="formData.remainingTimes" placeholder="请输入剩余发送次数" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="已发送次数：" name="totalSent">
						<a-input v-model:value="formData.totalSent" placeholder="请输入已发送次数" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="最后发送时间：" name="lastSentTime">
						<a-date-picker v-model:value="formData.lastSentTime" value-format="YYYY-MM-DD HH:mm:ss" show-time placeholder="请选择最后发送时间" style="width: 100%" />
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

<script setup name="wechatUserSubscriptionForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import wechatUserSubscriptionApi from '@/api/dev/wechatUserSubscriptionApi'
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
				wechatUserSubscriptionApi
					.wechatUserSubscriptionSubmitForm(formDataParam, formDataParam.id)
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
