<template>
	<xn-form-container
		:title="formData.id ? '编辑佣金记录表' : '增加佣金记录表'"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="订单ID：" name="orderId">
						<a-input v-model:value="formData.orderId" placeholder="请输入订单ID" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="订单号：" name="orderNo">
						<a-input v-model:value="formData.orderNo" placeholder="请输入订单号" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="受益用户ID：" name="userId">
						<a-input v-model:value="formData.userId" placeholder="请输入受益用户ID" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="用户昵称：" name="userNickname">
						<a-input v-model:value="formData.userNickname" placeholder="请输入用户昵称" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="设备ID：" name="deviceId">
						<a-input v-model:value="formData.deviceId" placeholder="请输入设备ID" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="设备编码：" name="deviceCode">
						<a-input v-model:value="formData.deviceCode" placeholder="请输入设备编码" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="酒品ID：" name="wineId">
						<a-input v-model:value="formData.wineId" placeholder="请输入酒品ID" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="酒品名称：" name="wineName">
						<a-input v-model:value="formData.wineName" placeholder="请输入酒品名称" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="订单金额(元)：" name="orderAmount">
						<a-input v-model:value="formData.orderAmount" placeholder="请输入订单金额(元)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="佣金类型：" name="commissionType">
						<a-input v-model:value="formData.commissionType" placeholder="请输入佣金类型" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="佣金比例(%)：" name="commissionRate">
						<a-input v-model:value="formData.commissionRate" placeholder="请输入佣金比例(%)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="佣金金额(元)：" name="commissionAmount">
						<a-input v-model:value="formData.commissionAmount" placeholder="请输入佣金金额(元)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="佣金状态(PENDING:待计算,CALCULATED:已计算,WAIT_PROFIT:待分账,PROFIT_SHARING:分账中,PROFIT_SHARED:分账成功,PROFIT_FAILED:分账失败,SETTLED:已发放,FROZEN:已冻结,CANCELLED:已取消)：" name="status">
						<a-input v-model:value="formData.status" placeholder="请输入佣金状态" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="计算时间：" name="calculateTime">
						<a-date-picker v-model:value="formData.calculateTime" value-format="YYYY-MM-DD HH:mm:ss" show-time placeholder="请选择计算时间" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="发放时间：" name="settleTime">
						<a-date-picker v-model:value="formData.settleTime" value-format="YYYY-MM-DD HH:mm:ss" show-time placeholder="请选择发放时间" style="width: 100%" />
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

<script setup name="wineCommissionRecordForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import wineCommissionRecordApi from '@/api/wine/wineCommissionRecordApi'
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
		orderId: [required('请输入订单ID')],
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				const formDataParam = cloneDeep(formData.value)
				wineCommissionRecordApi
					.wineCommissionRecordSubmitForm(formDataParam, formDataParam.id)
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
