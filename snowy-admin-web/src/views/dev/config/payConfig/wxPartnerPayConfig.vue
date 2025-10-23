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
			<!-- 基础配置 -->
			<a-divider orientation="left">基础配置</a-divider>
			<a-form-item label="微信服务商AppId：" name="SNOWY_PAY_WX_PARTNER_APP_ID">
				<a-input v-model:value="formData.SNOWY_PAY_WX_PARTNER_APP_ID" placeholder="请输入微信服务商AppId" />
			</a-form-item>

			<a-form-item label="微信服务商AppSecret：" name="SNOWY_PAY_WX_PARTNER_APP_SECRET">
				<a-input v-model:value="formData.SNOWY_PAY_WX_PARTNER_APP_SECRET" placeholder="请输入微信服务商AppSecret" />
			</a-form-item>

			<a-form-item label="微信服务商商户号：" name="SNOWY_PAY_WX_PARTNER_MCH_ID">
				<a-input v-model:value="formData.SNOWY_PAY_WX_PARTNER_MCH_ID" placeholder="请输入微信服务商商户号" />
			</a-form-item>

			<a-form-item label="微信服务商商户V2密钥：" name="SNOWY_PAY_WX_PARTNER_MCH_KEY">
				<a-textarea
					v-model:value="formData.SNOWY_PAY_WX_PARTNER_MCH_KEY"
					placeholder="请输入微信服务商商户V2密钥"
					:auto-size="{ minRows: 3, maxRows: 5 }"
				/>
			</a-form-item>

			<!-- 证书配置 -->
			<a-divider orientation="left">证书配置</a-divider>

			<a-form-item label="微信服务商p12证书路径：" name="SNOWY_PAY_WX_PARTNER_KEY_PATH">
				<xn-upload v-model:value="formData.SNOWY_PAY_WX_PARTNER_KEY_PATH" uploadMode="file" uploadResultType="path" />
			</a-form-item>

			<a-form-item label="微信服务商apiClientKey路径：" name="SNOWY_PAY_WX_PARTNER_PRIVATE_KEY_PATH">
				<xn-upload
					v-model:value="formData.SNOWY_PAY_WX_PARTNER_PRIVATE_KEY_PATH"
					uploadMode="file"
					uploadResultType="path"
				/>
			</a-form-item>

			<a-form-item label="微信服务商apiClientCert路径：" name="SNOWY_PAY_WX_PARTNER_PRIVATE_CERT_PATH">
				<xn-upload
					v-model:value="formData.SNOWY_PAY_WX_PARTNER_PRIVATE_CERT_PATH"
					uploadMode="file"
					uploadResultType="path"
				/>
			</a-form-item>

			<a-form-item label="微信服务商ApiV3证书序列号：" name="SNOWY_PAY_WX_PARTNER_CERT_SERIAL_NO">
				<a-input
					v-model:value="formData.SNOWY_PAY_WX_PARTNER_CERT_SERIAL_NO"
					placeholder="请输入微信服务商ApiV3证书序列号"
				/>
			</a-form-item>

			<a-form-item label="微信服务商ApiV3密钥：" name="SNOWY_PAY_WX_PARTNER_API_V3_KEY">
				<a-textarea
					v-model:value="formData.SNOWY_PAY_WX_PARTNER_API_V3_KEY"
					placeholder="请输入微信服务商ApiV3密钥"
					:auto-size="{ minRows: 3, maxRows: 5 }"
				/>
			</a-form-item>

			<!-- 回调地址配置 -->
			<a-divider orientation="left">回调地址配置</a-divider>

			<a-form-item label="微信服务商支付回调地址：" name="SNOWY_PAY_WX_PARTNER_NOTIFY_URL">
				<a-input v-model:value="formData.SNOWY_PAY_WX_PARTNER_NOTIFY_URL" placeholder="请输入微信服务商支付回调地址" />
			</a-form-item>

			<a-form-item label="微信服务商退款回调地址：" name="SNOWY_PAY_WX_PARTNER_REFUND_NOTIFY_URL">
				<a-input
					v-model:value="formData.SNOWY_PAY_WX_PARTNER_REFUND_NOTIFY_URL"
					placeholder="请输入微信服务商退款回调地址"
				/>
			</a-form-item>

			<a-form-item label="微信服务商转账回调地址：" name="SNOWY_PAY_WX_PARTNER_TRANSFER_NOTIFY_URL">
				<a-input
					v-model:value="formData.SNOWY_PAY_WX_PARTNER_TRANSFER_NOTIFY_URL"
					placeholder="请输入微信服务商转账回调地址"
				/>
			</a-form-item>

			<!-- 操作按钮 -->
			<a-form-item>
				<a-space>
					<a-button type="primary" :loading="submitLoading" @click="onSubmit()">保存</a-button>
					<a-button @click="() => formRef.resetFields()">重置</a-button>
				</a-space>
			</a-form-item>
		</a-form>
	</a-spin>
</template>

<script setup name="wxPartnerPayForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import { message } from 'ant-design-vue'
	import configApi from '@/api/dev/configApi'

	const formRef = ref()
	const formData = ref({})
	const submitLoading = ref(false)
	const testLoading = ref(false)
	const loadSpinning = ref(true)

	// 查询此界面的配置项,并转为表单
	const param = {
		category: 'PAY'
	}

	configApi.configList(param).then((data) => {
		loadSpinning.value = false
		if (data) {
			// 过滤出服务商相关的配置项
			const partnerConfigs = data.filter((item) => item.configKey.includes('PARTNER'))
			partnerConfigs.forEach((item) => {
				// 处理布尔值类型的配置
				if (item.configKey === 'SNOWY_PAY_WX_PARTNER_ENABLED') {
					formData.value[item.configKey] = item.configValue === 'true'
				} else {
					formData.value[item.configKey] = item.configValue
				}
			})
		} else {
			message.warning('表单项不存在，请先执行数据库初始化脚本')
		}
	})

	// 表单验证规则
	const formRules = {
		SNOWY_PAY_WX_PARTNER_APP_ID: [required('请输入微信服务商AppId')],
		SNOWY_PAY_WX_PARTNER_APP_SECRET: [required('请输入微信服务商AppSecret')],
		SNOWY_PAY_WX_PARTNER_MCH_ID: [required('请输入微信服务商商户号')],
		SNOWY_PAY_WX_PARTNER_MCH_KEY: [required('请输入微信服务商商户V2密钥')],
		SNOWY_PAY_WX_PARTNER_KEY_PATH: [required('请上传微信服务商p12证书')],
		SNOWY_PAY_WX_PARTNER_PRIVATE_KEY_PATH: [required('请上传微信服务商apiClientKey文件')],
		SNOWY_PAY_WX_PARTNER_PRIVATE_CERT_PATH: [required('请上传微信服务商apiClientCert文件')],
		SNOWY_PAY_WX_PARTNER_CERT_SERIAL_NO: [required('请输入微信服务商ApiV3证书序列号')],
		SNOWY_PAY_WX_PARTNER_API_V3_KEY: [required('请输入微信服务商ApiV3密钥')],
		SNOWY_PAY_WX_PARTNER_NOTIFY_URL: [required('请输入微信服务商支付回调地址')]
	}

	// 验证并提交数据
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			submitLoading.value = true
			let submitParam = cloneDeep(formData.value)

			// 处理布尔值转换
			if (typeof submitParam.SNOWY_PAY_WX_PARTNER_ENABLED === 'boolean') {
				submitParam.SNOWY_PAY_WX_PARTNER_ENABLED = submitParam.SNOWY_PAY_WX_PARTNER_ENABLED.toString()
			}

			const param = Object.entries(submitParam).map((item) => {
				return {
					configKey: item[0],
					configValue: item[1] || ''
				}
			})

			configApi
				.configEditForm(param)
				.then(() => {
					message.success('微信服务商支付配置保存成功')
				})
				.catch((error) => {
					message.error('保存失败：' + error.message)
				})
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

<style scoped>
	.ant-divider-horizontal.ant-divider-with-text-left::before {
		width: 5%;
	}
</style>
