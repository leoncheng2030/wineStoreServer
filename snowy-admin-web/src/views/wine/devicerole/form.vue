<template>
	<xn-form-container
		:title="formData.id ? '编辑设备角色定义表' : '增加设备角色定义表'"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="角色编码：" name="roleCode">
						<a-input v-model:value="formData.roleCode" placeholder="请输入角色编码" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="角色名称：" name="roleName">
						<a-input v-model:value="formData.roleName" placeholder="请输入角色名称" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="角色描述：" name="roleDescription">
						<a-input v-model:value="formData.roleDescription" placeholder="请输入角色描述" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="权限列表（JSON格式存储权限代码数组）：" name="permissions">
						<a-input v-model:value="formData.permissions" placeholder="请输入权限列表（JSON格式存储权限代码数组）" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="默认佣金比例（百分比）：" name="defaultCommissionRate">
						<a-input v-model:value="formData.defaultCommissionRate" placeholder="请输入默认佣金比例（百分比）" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="角色级别（数字越小级别越高）：" name="roleLevel">
						<a-input v-model:value="formData.roleLevel" placeholder="请输入角色级别（数字越小级别越高）" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="状态（ENABLE-启用, DISABLE-禁用）：" name="status">
						<a-select v-model:value="formData.status" placeholder="请选择状态（ENABLE-启用, DISABLE-禁用）" :options="statusOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="排序码：" name="sortCode">
						<a-input v-model:value="formData.sortCode" placeholder="请输入排序码" allow-clear />
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

<script setup name="wineDeviceRoleForm">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import wineDeviceRoleApi from '@/api/wine/wineDeviceRoleApi'
	// 抽屉状态
	const open = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	const submitLoading = ref(false)
	const statusOptions = ref([])

	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		if (record) {
			let recordData = cloneDeep(record)
			formData.value = Object.assign({}, recordData)
		}
		statusOptions.value = tool.dictList('COMMON_STATUS')
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
				wineDeviceRoleApi
					.wineDeviceRoleSubmitForm(formDataParam, formDataParam.id)
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
