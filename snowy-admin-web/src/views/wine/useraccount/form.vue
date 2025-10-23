<template>
	<xn-form-container
		:title="formData.id ? '编辑账户列表' : '增加账户列表'"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
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
					<a-form-item label="总余额(元)：" name="totalBalance">
						<a-input v-model:value="formData.totalBalance" placeholder="请输入总余额(元)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="可用余额(元)：" name="availableBalance">
						<a-input v-model:value="formData.availableBalance" placeholder="请输入可用余额(元)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="冻结余额(元)：" name="frozenBalance">
						<a-input v-model:value="formData.frozenBalance" placeholder="请输入冻结余额(元)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="累计佣金(元)：" name="totalCommission">
						<a-input v-model:value="formData.totalCommission" placeholder="请输入累计佣金(元)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="累计提现(元)：" name="totalWithdraw">
						<a-input v-model:value="formData.totalWithdraw" placeholder="请输入累计提现(元)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="账户状态(NORMAL:正常,FROZEN:冻结,DISABLED:禁用)：" name="status">
						<a-input v-model:value="formData.status" placeholder="请输入账户状态(NORMAL:正常,FROZEN:冻结,DISABLED:禁用)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="最后佣金时间：" name="lastCommissionTime">
						<a-date-picker v-model:value="formData.lastCommissionTime" value-format="YYYY-MM-DD HH:mm:ss" show-time placeholder="请选择最后佣金时间" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="最后提现时间：" name="lastWithdrawTime">
						<a-date-picker v-model:value="formData.lastWithdrawTime" value-format="YYYY-MM-DD HH:mm:ss" show-time placeholder="请选择最后提现时间" style="width: 100%" />
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

<script setup name="wineUserAccountForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import wineUserAccountApi from '@/api/wine/wineUserAccountApi'
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
		userId: [required('请输入用户ID')],
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				const formDataParam = cloneDeep(formData.value)
				wineUserAccountApi
					.wineUserAccountSubmitForm(formDataParam, formDataParam.id)
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
