<template>
	<xn-form-container
		:title="formData.id ? '编辑设备信息表' : '增加设备信息表'"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="设备编码：" name="deviceCode">
						<a-input v-model:value="formData.deviceCode" placeholder="请输入设备编码" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="设备名称：" name="deviceName">
						<a-input v-model:value="formData.deviceName" placeholder="请输入设备名称" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="脉冲比：" name="pulseRatio">
						<a-input v-model:value="formData.pulseRatio" placeholder="请输入设备脉冲比" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="运营方式：" name="operationType">
						<a-select
							v-model:value="formData.operationType"
							:options="operationTypeOptions"
							placeholder="请选择运营方式"
							allow-clear
						/>
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item label="投放门店：" name="storeId">
						<XnStoreSelector
							v-model:value="formData.storeId"
							:radioModel="true"
							:store-page-api="selectorApiFunction.storePageApi"
							:store-list-by-id-list-api="selectorApiFunction.storeListByIdListApi"
							placeholder="请选择投放门店"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item label="绑定酒品：" name="currentProductId">
						<XnWineSelector
							v-model:value="formData.currentProductId"
							:radioModel="true"
							:wine-page-api="selectorApiFunction.winePageApi"
							:wine-list-by-id-list-api="selectorApiFunction.wineListByIdListApi"
							:category-tree-api="selectorApiFunction.categoryTreeApi"
							placeholder="请选择当前绑定的酒品"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="8" v-if="formData.operationType === 'OPERATION_TYPE_AGENT'">
					<a-form-item label="代理商：" name="agentUserId">
						<XnClientUserSelector
							v-model:value="formData.agentUserId"
							:radioModel="true"
							:user-list-by-id-list-api="selectorApiFunction.userListByIdListApi"
							:user-page-api="selectorApiFunction.agentUserPageApi"
							placeholder="请选择代理商（可空，支持扫码绑定）"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="24">
					<a-form-item label="备注：" name="remark">
						<a-textarea v-model:value="formData.remark" placeholder="请输入备注" allow-clear />
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

<script setup name="wineDeviceForm">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import wineDeviceApi from '@/api/wine/wineDeviceApi'
	// 导入酒品选择器组件
	import XnWineSelector from '@/components/XnWineSelector/index.vue'
	// 导入客户端用户选择器组件
	import XnClientUserSelector from '@/components/XnClientUserSelector/index.vue'
	// 导入门店选择器组件
	import XnStoreSelector from '@/components/XnStoreSelector/index.vue'
	// 导入客户端用户API
	import clientUserApi from '@/api/client/clientUserApi'
	// 导入门店API
	import wineStoreApi from '@/api/wine/wineStoreApi'
	// 导入酒品API
	import wineProductApi from '@/api/wine/wineProductApi'
	// 导入酒品分类API
	import wineProductCategoryApi from '@/api/wine/wineProductCategoryApi'

	// 抽屉状态
	const open = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	const submitLoading = ref(false)
	const operationTypeOptions = tool.dictList('OPERATION_TYPE')
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		if (record) {
			let recordData = cloneDeep(record)
			formData.value = Object.assign({}, recordData)
		}
	}
	const selectorApiFunction = {
		agentUserPageApi: (param) => {
			param.isAgent = 'YES'
			return clientUserApi.clientUserSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		// 客户端用户选择器API
		userPageApi: (param) => {
			return clientUserApi.clientUserSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		userListByIdListApi: (param) => {
			return clientUserApi.userListByIds(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		// 门店选择器API
		storePageApi: (param) => {
			return wineStoreApi.wineStorePage(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		// 门店批量查询API - 新增
		storeListByIdListApi: (param) => {
			return wineStoreApi.wineStoreIdList(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		// 酒品选择器API
		winePageApi: (param) => {
			return wineProductApi.wineProductPage(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		// 酒品批量查询API - 新增
		wineListByIdListApi: (param) => {
			return wineProductApi.wineProductIdList(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		// 酒品分类树API
		categoryTreeApi: () => {
			return wineProductCategoryApi.wineProductCategoryTree().then((data) => {
				return Promise.resolve(data)
			})
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
		deviceCode: [required('请输入设备编码')],
		deviceName: [required('请输入设备名称')],
		storeId: [required('请选择投放门店')],
		currentProductId: [required('请选择绑定酒品')],
		operationType: [required('请选择运营方式')]
		// 注：代理商不再强制要求，支持扫码绑定
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				const formDataParam = cloneDeep(formData.value)
				// 处理代理商相关字段，只有在代理商模式下才提交
				if (formDataParam.operationType !== 'OPERATION_TYPE_AGENT') {
					delete formDataParam.agentUserId
					delete formDataParam.storeCommissionRate
					delete formDataParam.managerCommissionRate
				}
				wineDeviceApi
					.wineDeviceSubmitForm(formDataParam, formDataParam.id)
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
