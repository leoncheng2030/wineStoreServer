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
				<a-form-item label="平台测试员：" name="PLATFORM_TEST_ID">
					<xn-client-user-selector
						v-model:value="formData.PLATFORM_TEST_ID"
						:radioModel="false"
						:user-list-by-id-list-api="selectorApiFunction.userListByIdListApi"
						:user-page-api="selectorApiFunction.userPageApi"
					/>
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
					.then(() => {
						message.success('保存成功')
					})
					.catch(() => {
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
