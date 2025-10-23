<template>
	<xn-form-container
		:title="formData.id ? '编辑订单表' : '增加订单表'"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="订单号：" name="orderNo">
						<a-input v-model:value="formData.orderNo" placeholder="请输入订单号" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="用户ID：" name="userId">
						<a-input v-model:value="formData.userId" placeholder="请输入用户ID" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="设备ID：" name="deviceId">
						<a-input v-model:value="formData.deviceId" placeholder="请输入设备ID" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="酒品ID：" name="productId">
						<a-input v-model:value="formData.productId" placeholder="请输入酒品ID" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="酒品名称：" name="productName">
						<a-input v-model:value="formData.productName" placeholder="请输入酒品名称" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="出酒量(ml)：" name="quantity">
						<a-input v-model:value="formData.quantity" placeholder="请输入出酒量(ml)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="单价(元/ml)：" name="unitPrice">
						<a-input v-model:value="formData.unitPrice" placeholder="请输入单价(元/ml)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="总金额：" name="totalAmount">
						<a-input v-model:value="formData.totalAmount" placeholder="请输入总金额" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="订单状态：" name="status">
						<a-input v-model:value="formData.status" placeholder="请输入订单状态" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="支付时间：" name="payTime">
						<a-date-picker v-model:value="formData.payTime" value-format="YYYY-MM-DD HH:mm:ss" show-time placeholder="请选择支付时间" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="开始出酒时间：" name="dispenseStartTime">
						<a-date-picker v-model:value="formData.dispenseStartTime" value-format="YYYY-MM-DD HH:mm:ss" show-time placeholder="请选择开始出酒时间" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="出酒完成时间：" name="dispenseEndTime">
						<a-date-picker v-model:value="formData.dispenseEndTime" value-format="YYYY-MM-DD HH:mm:ss" show-time placeholder="请选择出酒完成时间" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="取消时间：" name="cancelTime">
						<a-date-picker v-model:value="formData.cancelTime" value-format="YYYY-MM-DD HH:mm:ss" show-time placeholder="请选择取消时间" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="取消原因：" name="cancelReason">
						<a-input v-model:value="formData.cancelReason" placeholder="请输入取消原因" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="退款时间：" name="refundTime">
						<a-date-picker v-model:value="formData.refundTime" value-format="YYYY-MM-DD HH:mm:ss" show-time placeholder="请选择退款时间" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="退款金额：" name="refundAmount">
						<a-input v-model:value="formData.refundAmount" placeholder="请输入退款金额" allow-clear />
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

<script setup name="wineOrderForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import wineOrderApi from '@/api/wine/wineOrderApi'
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
		orderNo: [required('请输入订单号')],
		userId: [required('请输入用户ID')],
		deviceId: [required('请输入设备ID')],
		productId: [required('请输入酒品ID')],
		productName: [required('请输入酒品名称')],
		quantity: [required('请输入出酒量(ml)')],
		unitPrice: [required('请输入单价(元/ml)')],
		totalAmount: [required('请输入总金额')],
		status: [required('请输入订单状态')],
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				const formDataParam = cloneDeep(formData.value)
				wineOrderApi
					.wineOrderSubmitForm(formDataParam, formDataParam.id)
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
