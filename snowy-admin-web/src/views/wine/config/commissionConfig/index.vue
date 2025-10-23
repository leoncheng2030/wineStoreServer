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
				<a-form-item label="分佣时机：" name="COMMISSION_DISTRIBUTION">
					<a-radio-group v-model:value="formData.COMMISSION_DISTRIBUTION">
						<a-radio value="1">支付成功</a-radio>
						<a-radio value="2">出酒完成</a-radio>
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
	import tool from '@/utils/tool'
	import { QuestionCircleOutlined } from '@ant-design/icons-vue'

	const formRef = ref()
	const formData = ref({})
	const submitLoading = ref(false)
	const loadSpinning = ref(true)
	// 查询此界面的配置项,并转为表单
	const param = {
		category: 'COMMISSION'
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
		PLATFORM: [required('请输入平台')],
		STORE: [required('请输入门店')],
		QUDAO: [required('请输入渠道')]
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
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
