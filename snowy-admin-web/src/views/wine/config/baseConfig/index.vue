<template>
	<a-card>
		<a-spin :spinning="loadSpinning">
			<a-form
				ref="formRef"
				:model="formData"
				:rules="formRules"
				layout="vertical"
				:label-col="{ ...layout.labelCol, offset: 0 }"
				:wrapper-col="{ ...layout.wrapperCol, offset: 0 }"
			>
				<a-form-item label="微信小程序名称：" name="MINI_PROGRAM_NAME">
					<a-input v-model:value="formData.MINI_PROGRAM_NAME" style="width: 50%"></a-input>
				</a-form-item>
				<a-form-item label="微信小程序描述：" name="MINI_PROGRAM_DESC">
					<a-textarea v-model:value="formData.MINI_PROGRAM_DESC" style="width: 50%"></a-textarea>
				</a-form-item>
				<a-form-item label="微信小程序logo：" name="MINI_PROGRAM_LOGO">
					<xn-upload v-model:value="formData.MINI_PROGRAM_LOGO" uploadMode="image"></xn-upload>
				</a-form-item>
				<a-form-item label="库存预警值：" name="STOCK_WARNING">
					<a-input v-model:value="formData.STOCK_WARNING" style="width: 50%">
						<template #addonAfter> ml </template>
					</a-input>
				</a-form-item>
				<a-form-item label="最小提现金额：" name="MIN_WITHDRAW">
					<a-input v-model:value="formData.MIN_WITHDRAW" style="width: 50%">
						<template #addonAfter> 元 </template>
					</a-input>
				</a-form-item>
				<a-form-item label="提现手续费：" name="WITHDRAW_SERVICE_RATIO">
					<a-input v-model:value="formData.WITHDRAW_SERVICE_RATIO" style="width: 50%">
						<template #addonAfter> 元 </template>
					</a-input>
				</a-form-item>
				<a-form-item label="脉冲比：" name="PULSE_RATIO">
					<a-input v-model:value="formData.PULSE_RATIO" style="width: 50%">
						<template #addonAfter> % </template>
					</a-input>
				</a-form-item>
				<a-form-item label="平台管理员：" name="PLATFORM_MANAGE_ID">
					<xn-client-user-selector
						v-model:value="formData.PLATFORM_MANAGE_ID"
						:radioModel="true"
						:user-list-by-id-list-api="selectorApiFunction.userListByIdListApi"
						:user-page-api="selectorApiFunction.userPageApi"
					/>
				</a-form-item>
				<a-form-item label="是否启用服务商模式：" name="SNOWY_PAY_WX_PARTNER_ENABLED">
					<a-radio-group v-model:value="formData.SNOWY_PAY_WX_PARTNER_ENABLED">
						<a-radio :value="true">启用</a-radio>
						<a-radio :value="false">禁用</a-radio>
					</a-radio-group>
				</a-form-item>
				<a-form-item>
					<a-button type="primary" :loading="submitLoading" @click="onSubmit()">保存</a-button>
					<a-button class="xn-ml10" @click="() => formRef.resetFields()">重置</a-button>
				</a-form-item>
			</a-form>
		</a-spin>
	</a-card>
</template>

<script setup name="cForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import { message } from 'ant-design-vue'
	import configApi from '@/api/dev/configApi'
	import clientUserApi from '@/api/client/clientUserApi'
	import wineDeviceApi from '@/api/wine/wineDeviceApi'

	const formRef = ref()
	const formData = ref({})
	const submitLoading = ref(false)
	const loadSpinning = ref(true)
	// 查询此界面的配置项,并转为表单
	const param = {
		category: 'BASE_CONFIG'
	}
	configApi.configList(param).then((data) => {
		loadSpinning.value = false
		if (data) {
			data.forEach((item) => {
				formData.value[item.configKey] = transferBooleanInValue(item.configValue)
			})
		} else {
			message.warning('表单项不存在，请初始化数据库')
		}
	})
	// 传递设计器需要的API
	const selectorApiFunction = {
		userPageApi: (param) => {
			console.log(param)
			return clientUserApi.clientUserSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		userListByIdListApi: (param) => {
			return clientUserApi.userListByIds(param).then((data) => {
				return Promise.resolve(data)
			})
		}
	}
	// 转换值
	const transferBooleanInValue = (value) => {
		if (value === 'true' || value === 'false') {
			return value === 'true'
		} else {
			return value
		}
	}
	// 默认要校验的
	const formRules = {
		MINI_PROGRAM_DESC: [required('请输入微信小程序描述')],
		MINI_PROGRAM_LOGO: [required('请上传微信小程序logo')],
		MINI_PROGRAM_NAME: [required('请输入微信小程序名称')],
		PLATFORM_MANAGE_ID: [required('请选择门店')],
		PULSE_RATIO: [required('请输入脉冲比')],
		STOCK_WARNING: [required('请输入库存预警值')],
		MIN_WITHDRAW: [required('请输入最小提现金额')],
		// APP_ID: [required('请输入为微信小程序appid')],
		// APP_SECRET: [required('请输入微信小程序密钥')]
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				let submitParam = cloneDeep(formData.value)

				// 检查是否更新了脉冲比配置
				const pulseRatioParam = submitParam.PULSE_RATIO

				const param = Object.entries(submitParam).map((item) => {
					return {
						configKey: item[0],
						configValue: item[1]
					}
				})

				// 先保存配置
				configApi
					.configEditForm(param)
					.then(() => {
						message.success('配置保存成功')

						// 如果更新了脉冲比，则批量更新所有设备的脉冲数
						if (pulseRatioParam && pulseRatioParam !== '') {
							const pulseRatioValue = parseFloat(pulseRatioParam)
							if (!isNaN(pulseRatioValue) && pulseRatioValue > 0) {
								message.loading('正在更新设备脉冲数...', 0)

								wineDeviceApi.updateAllDevicesPulseRatio(pulseRatioValue)
									.then(() => {
										message.destroy()
										message.success('脉冲比配置及设备脉冲数更新成功')
									})
									.catch((error) => {
										message.destroy()
										console.error('更新设备脉冲数失败:', error)
										message.warning('配置保存成功，但更新设备脉冲数失败，请手动更新')
									})
							}
						}
					})
					.catch((error) => {
						console.error('保存失败:', error)
						message.error('保存失败')
					})
					.finally(() => {
						submitLoading.value = false
					})
			})
			.catch(() => {})
	}
	const layout = {
		labelCol: {
			span: 4
		},
		wrapperCol: {
			span: 12
		}
	}
</script>
