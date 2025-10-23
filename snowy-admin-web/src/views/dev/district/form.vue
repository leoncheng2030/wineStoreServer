<template>
	<xn-form-container
		:title="formData.id ? '编辑地区表' : '增加地区表'"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="父级地区编码：" name="parentId">
				<a-select v-model:value="formData.parentId" placeholder="请选择父级地区编码" :options="parentIdOptions" />
			</a-form-item>
			<a-form-item label="等级：" name="level">
				<a-select v-model:value="formData.level" placeholder="请选择等级" :options="levelOptions" />
			</a-form-item>
			<a-form-item label="名称：" name="name">
				<a-input v-model:value="formData.name" placeholder="请输入名称" allow-clear />
			</a-form-item>
			<a-form-item label="简称：" name="shortName">
				<a-input v-model:value="formData.shortName" placeholder="请输入简称" allow-clear />
			</a-form-item>
			<a-form-item label="地区编码：" name="cityCode">
				<a-input v-model:value="formData.cityCode" placeholder="请输入地区编码" allow-clear />
			</a-form-item>
			<a-form-item label="邮政编码：" name="zipCode">
				<a-input v-model:value="formData.zipCode" placeholder="请输入邮政编码" allow-clear />
			</a-form-item>
			<a-form-item label="纬度：" name="gcj02Lng">
				<a-input v-model:value="formData.gcj02Lng" placeholder="请输入纬度" allow-clear />
			</a-form-item>
			<a-form-item label="经度：" name="gcj02Lat">
				<a-input v-model:value="formData.gcj02Lat" placeholder="请输入经度" allow-clear />
			</a-form-item>
			<a-form-item label="纬度：" name="db09Lng">
				<a-input v-model:value="formData.db09Lng" placeholder="请输入纬度" allow-clear />
			</a-form-item>
			<a-form-item label="经度：" name="db09Lat">
				<a-input v-model:value="formData.db09Lat" placeholder="请输入经度" allow-clear />
			</a-form-item>
			<a-form-item label="REMARK1：" name="remark1">
				<a-input v-model:value="formData.remark1" placeholder="请输入REMARK1" allow-clear />
			</a-form-item>
			<a-form-item label="REMARK2：" name="remark2">
				<a-input v-model:value="formData.remark2" placeholder="请输入REMARK2" allow-clear />
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit" :loading="submitLoading">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="devDistrictForm">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import devDistrictApi from '@/api/dev/devDistrictApi'
	// 抽屉状态
	const open = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	const submitLoading = ref(false)
	const parentIdOptions = ref([])
	const levelOptions = ref([])

	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		if (record) {
			let recordData = cloneDeep(record)
			formData.value = Object.assign({}, recordData)
		}
		parentIdOptions.value = tool.dictList('COMMON_STATUS')
		levelOptions.value = tool.dictList('COMMON_STATUS')
	}
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		formData.value = {}
		open.value = false
	}
	// 默认要校验的
	const formRules = {
		parentId: [required('请输入父级地区编码')],
		level: [required('请输入等级')],
		name: [required('请输入名称')],
		shortName: [required('请输入简称')],
		cityCode: [required('请输入地区编码')],
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				const formDataParam = cloneDeep(formData.value)
				devDistrictApi
					.devDistrictSubmitForm(formDataParam, formDataParam.id)
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
