<template>
	<a-spin :spinning="loadSpinning">
		<a-form
			ref="formRef"
			:model="formData"
			:rules="formRules"
			layout="vertical"
			:label-col="{ ...layout.labelCol, offset: 0 }"
			:wrapper-col="{ ...layout.wrapperCol, offset: 0 }"
		>
			<a-form-item label="微信支付应用ID：" name="SNOWY_PAY_WX_APP_ID">
				<a-input v-model:value="formData.SNOWY_PAY_WX_APP_ID" placeholder="请输入微信支付应用ID" />
			</a-form-item>
			<a-form-item label="微信支付公众号AppSecret：" name="SNOWY_PAY_WX_APP_SECRET">
				<a-input v-model:value="formData.SNOWY_PAY_WX_APP_SECRET" placeholder="请输入微信支付公众号AppSecret" />
			</a-form-item>
			<a-form-item label="微信支付商户号：" name="SNOWY_PAY_WX_MCH_ID">
				<a-input v-model:value="formData.SNOWY_PAY_WX_MCH_ID" placeholder="请输入微信支付商户号" />
			</a-form-item>
			<a-form-item label="微信支付商户V2密钥：" name="SNOWY_PAY_WX_MCH_KEY">
				<a-textarea
					v-model:value="formData.SNOWY_PAY_WX_MCH_KEY"
					placeholder="请输入微信支付商户V2密钥"
					:auto-size="{ minRows: 3, maxRows: 5 }"
				/>
			</a-form-item>
			<a-form-item label="微信支付p12证书路径：" name="SNOWY_PAY_WX_KEY_PATH">
				<xn-upload v-model:value="formData.SNOWY_PAY_WX_KEY_PATH" uploadMode="file" uploadResultType="path"></xn-upload>
			</a-form-item>
			<a-form-item label="微信支付apiClientKey路径：" name="SNOWY_PAY_WX_PRIVATE_KEY_PATH">
				<xn-upload
					v-model:value="formData.SNOWY_PAY_WX_PRIVATE_KEY_PATH"
					uploadMode="file"
					uploadResultType="path"
				></xn-upload>
			</a-form-item>
			<a-form-item label="微信支付apiClientCert路径：" name="SNOWY_PAY_WX_PRIVATE_CERT_PATH">
				<xn-upload
					v-model:value="formData.SNOWY_PAY_WX_PRIVATE_CERT_PATH"
					uploadMode="file"
					uploadResultType="path"
				></xn-upload>
			</a-form-item>
			<a-form-item label="微信支付ApiV3证书序列号值：" name="SNOWY_PAY_WX_CERT_SERIAL_NO">
				<a-input v-model:value="formData.SNOWY_PAY_WX_CERT_SERIAL_NO" placeholder="请输入微信支付ApiV3证书序列号值" />
			</a-form-item>
			<a-form-item label="微信支付ApiV3密钥值：" name="SNOWY_PAY_WX_API_V3_KEY">
				<a-textarea
					v-model:value="formData.SNOWY_PAY_WX_API_V3_KEY"
					placeholder="请输入微信支付ApiV3密钥值"
					:auto-size="{ minRows: 3, maxRows: 5 }"
				/>
			</a-form-item>
			<a-form-item label="微信支付回调地址：" name="SNOWY_PAY_WX_NOTIFY_URL">
				<a-input v-model:value="formData.SNOWY_PAY_WX_NOTIFY_URL" placeholder="请输入微信支付回调地址" />
			</a-form-item>
			<a-form-item label="微信支付退款回调地址：" name="SNOWY_PAY_WX_REFUND_NOTIFY_URL">
				<a-input v-model:value="formData.SNOWY_PAY_WX_REFUND_NOTIFY_URL" placeholder="请输入微信支付退款回调地址" />
			</a-form-item>
			<a-form-item label="微信转账通知地址：" name="SNOWY_PAY_WX_TRANSFER_NOTIFY_URL">
				<a-input v-model:value="formData.SNOWY_PAY_WX_TRANSFER_NOTIFY_URL" placeholder="" />
			</a-form-item>
			<a-form-item>
				<a-button type="primary" :loading="submitLoading" @click="onSubmit()">保存</a-button>
				<a-button style="margin-left: 10px" @click="() => formRef.resetFields()">重置</a-button>
			</a-form-item>
		</a-form>
	</a-spin>
</template>

<script setup name="wxPayForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import { message } from 'ant-design-vue'
	import configApi from '@/api/dev/configApi'

	const formRef = ref()
	const formData = ref({})
	const submitLoading = ref(false)
	const loadSpinning = ref(true)

	// 查询此界面的配置项,并转为表单
	const param = {
		category: 'PAY_WX'
	}
	configApi.configList(param).then((data) => {
		loadSpinning.value = false
		if (data) {
			data.forEach((item) => {
				formData.value[item.configKey] = item.configValue
			})
		} else {
			message.warning('表单项不存在，请初始化数据库')
		}
	})
	// 默认要校验的
	const formRules = {
		SNOWY_PAY_WX_APP_ID: [required('请输入微信支付应用ID')],
		SNOWY_PAY_WX_APP_SECRET: [required('请输入微信支付公众号AppSecret')],
		SNOWY_PAY_WX_MCH_ID: [required('请输入微信支付商户号')],
		SNOWY_PAY_WX_MCH_KEY: [required('请输入微信支付商户V2密钥')],
		SNOWY_PAY_WX_KEY_PATH: [required('请输入微信支付p12证书路径')],
		SNOWY_PAY_WX_PRIVATE_KEY_PATH: [required('请输入微信支付apiClientKey路径')],
		SNOWY_PAY_WX_PRIVATE_CERT_PATH: [required('请输入微信支付apiClientCert路径')],
		SNOWY_PAY_WX_CERT_SERIAL_NO: [required('请输入微信支付ApiV3证书序列号值')],
		SNOWY_PAY_WX_API_V3_KEY: [required('请输入微信支付ApiV3密钥值')],
		SNOWY_PAY_WX_NOTIFY_URL: [required('请输入微信支付回调地址')],
		SNOWY_PAY_WX_REFUND_NOTIFY_URL: [required('请输入微信支付退款回调地址')]
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			submitLoading.value = true
			let submitParam = cloneDeep(formData.value)
			const param = Object.entries(submitParam).map((item) => {
				return {
					configKey: item[0],
					configValue: item[1]
				}
			})
			configApi
				.configEditForm(param)
				.then(() => {})
				.finally(() => {
					submitLoading.value = false
				})
		})
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
