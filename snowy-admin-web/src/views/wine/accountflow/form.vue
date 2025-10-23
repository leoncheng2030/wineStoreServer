<template>
	<xn-form-container
		:title="formData.id ? '编辑账户流水' : '增加账户流水'"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="流水号：" name="flowNo">
						<a-input v-model:value="formData.flowNo" placeholder="请输入流水号" allow-clear />
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
					<a-form-item label="流水类型(COMMISSION:佣金收入,WITHDRAW:提现支出,REFUND:退款,TRANSFER:转账)：" name="flowType">
						<a-input v-model:value="formData.flowType" placeholder="请输入流水类型(COMMISSION:佣金收入,WITHDRAW:提现支出,REFUND:退款,TRANSFER:转账)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="金额(元)：" name="amount">
						<a-input v-model:value="formData.amount" placeholder="请输入金额(元)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="余额变动(元) - 正数表示增加，负数表示减少：" name="balanceChange">
						<a-input v-model:value="formData.balanceChange" placeholder="请输入余额变动(元) - 正数表示增加，负数表示减少" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="变动前余额(元)：" name="beforeBalance">
						<a-input v-model:value="formData.beforeBalance" placeholder="请输入变动前余额(元)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="变动后余额(元)：" name="afterBalance">
						<a-input v-model:value="formData.afterBalance" placeholder="请输入变动后余额(元)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="关联ID(订单ID、提现ID等)：" name="relatedId">
						<a-input v-model:value="formData.relatedId" placeholder="请输入关联ID(订单ID、提现ID等)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="关联类型(ORDER:订单,WITHDRAW:提现,MANUAL:手动调整)：" name="relatedType">
						<a-input v-model:value="formData.relatedType" placeholder="请输入关联类型(ORDER:订单,WITHDRAW:提现,MANUAL:手动调整)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="关联单号：" name="relatedNo">
						<a-input v-model:value="formData.relatedNo" placeholder="请输入关联单号" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="流水描述：" name="description">
						<a-input v-model:value="formData.description" placeholder="请输入流水描述" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="流水状态(SUCCESS:成功,FAILED:失败,PENDING:处理中)：" name="status">
						<a-input v-model:value="formData.status" placeholder="请输入流水状态(SUCCESS:成功,FAILED:失败,PENDING:处理中)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="交易时间：" name="transactionTime">
						<a-date-picker v-model:value="formData.transactionTime" value-format="YYYY-MM-DD HH:mm:ss" show-time placeholder="请选择交易时间" style="width: 100%" />
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

<script setup name="wineAccountFlowForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import wineAccountFlowApi from '@/api/wine/wineAccountFlowApi'
	// 抽屉状态
	const open = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	const submitLoading = ref(false)
	const statusOptions = tool.dictTypeList("CLIENT_USER_STATUS")
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
				wineAccountFlowApi
					.wineAccountFlowSubmitForm(formDataParam, formDataParam.id)
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
