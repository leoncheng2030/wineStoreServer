<template>
	<xn-form-container
		:title="formData.id ? '编辑提现记录表' : '增加提现记录表'"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="提现单号：" name="withdrawNo">
						<a-input v-model:value="formData.withdrawNo" placeholder="请输入提现单号" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="用户ID：" name="userId">
						<a-input v-model:value="formData.userId" placeholder="请输入用户ID" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="用户昵称：" name="userNickname">
						<a-input v-model:value="formData.userNickname" placeholder="请输入用户昵称" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="提现金额(元)：" name="withdrawAmount">
						<a-input v-model:value="formData.withdrawAmount" placeholder="请输入提现金额(元)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="手续费(元)：" name="serviceFee">
						<a-input v-model:value="formData.serviceFee" placeholder="请输入手续费(元)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="实际到账金额(元)：" name="actualAmount">
						<a-input v-model:value="formData.actualAmount" placeholder="请输入实际到账金额(元)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="提现方式：" name="withdrawType">
						<a-input v-model:value="formData.withdrawType" placeholder="请输入提现方式" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="账户信息：" name="accountInfo">
						<a-input v-model:value="formData.accountInfo" placeholder="请输入账户信息" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="提现状态：" name="status">
						<a-input v-model:value="formData.status" placeholder="请输入提现状态" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="申请时间：" name="applyTime">
						<a-date-picker v-model:value="formData.applyTime" value-format="YYYY-MM-DD HH:mm:ss" show-time placeholder="请选择申请时间" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="审核时间：" name="approveTime">
						<a-date-picker v-model:value="formData.approveTime" value-format="YYYY-MM-DD HH:mm:ss" show-time placeholder="请选择审核时间" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="处理时间：" name="processTime">
						<a-date-picker v-model:value="formData.processTime" value-format="YYYY-MM-DD HH:mm:ss" show-time placeholder="请选择处理时间" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="完成时间：" name="completeTime">
						<a-date-picker v-model:value="formData.completeTime" value-format="YYYY-MM-DD HH:mm:ss" show-time placeholder="请选择完成时间" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="审核用户：" name="approveUser">
						<a-input v-model:value="formData.approveUser" placeholder="请输入审核用户" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="处理用户：" name="processUser">
						<a-input v-model:value="formData.processUser" placeholder="请输入处理用户" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="失败原因：" name="failReason">
						<a-input v-model:value="formData.failReason" placeholder="请输入失败原因" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="第三方交易号：" name="transactionId">
						<a-input v-model:value="formData.transactionId" placeholder="请输入第三方交易号" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="备注：" name="remark">
						<a-input v-model:value="formData.remark" placeholder="请输入备注" allow-clear />
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

<script setup name="wineWithdrawRecordForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import wineWithdrawRecordApi from '@/api/wine/wineWithdrawRecordApi'
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
		withdrawNo: [required('请输入提现单号')],
		userId: [required('请输入用户ID')],
		userNickname: [required('请输入用户昵称')],
		withdrawAmount: [required('请输入提现金额(元)')],
		serviceFee: [required('请输入手续费(元)')],
		withdrawType: [required('请输入提现方式')],
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				const formDataParam = cloneDeep(formData.value)
				wineWithdrawRecordApi
					.wineWithdrawRecordSubmitForm(formDataParam, formDataParam.id)
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
