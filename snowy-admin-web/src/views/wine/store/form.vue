<template>
	<xn-form-container
		v-model:open="open"
		:destroy-on-close="true"
		:title="formData.id ? '编辑门店管理表' : '增加门店管理表'"
		:width="900"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-tabs v-model:activeKey="activeTab" type="card">
				<!-- 基本信息（必须） -->
				<a-tab-pane key="basic" tab="基本信息">
					<a-row :gutter="16">
						<!-- 门店管理员 -->
						<a-col :span="24">
							<a-form-item label="门店管理员：" name="storeManagerId">
								<xn-client-user-selector
									v-model:value="formData.storeManagerId"
									:radioModel="true"
									:user-list-by-id-list-api="selectorApiFunction.userListByIdListApi"
									:user-page-api="selectorApiFunction.userPageApi"
								/>
							</a-form-item>
						</a-col>

						<!-- 门店基本信息 -->
						<a-col :span="8">
							<a-form-item label="门店名称：" name="storeName">
								<a-input v-model:value="formData.storeName" allow-clear placeholder="请输入门店名称" />
							</a-form-item>
						</a-col>
						<a-col :span="8">
							<a-form-item label="门店编码：" name="storeCode">
								<a-input v-model:value="formData.storeCode" allow-clear placeholder="请输入门店编码" />
							</a-form-item>
						</a-col>
						<a-col :span="8">
							<a-form-item label="门店状态：" name="status">
								<a-select v-model:value="formData.status" placeholder="请选择状态" :options="statusOptions" />
							</a-form-item>
						</a-col>

						<!-- 地址信息 -->
						<!-- 隐藏的省份、城市、县区字段，但保留数据绑定 -->
						<a-col :span="0" style="display: none">
							<a-form-item name="province">
								<a-input v-model:value="formData.province" />
							</a-form-item>
						</a-col>
						<a-col :span="0" style="display: none">
							<a-form-item name="city">
								<a-input v-model:value="formData.city" />
							</a-form-item>
						</a-col>
						<a-col :span="0" style="display: none">
							<a-form-item name="district">
								<a-input v-model:value="formData.district" />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="详细地址：" name="detailAddress">
								<a-input v-model:value="formData.detailAddress" readonly placeholder="请通过地图选择获取地址">
									<template #addonAfter>
										<a @click="locationPickerRef.show()"> 地图选择 </a>
									</template>
								</a-input>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="纬度：" name="latitude">
								<a-input v-model:value="formData.latitude" allow-clear placeholder="请输入纬度" />
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="经度：" name="longitude">
								<a-input v-model:value="formData.longitude" allow-clear placeholder="请输入经度" />
							</a-form-item>
						</a-col>

						<!-- 主要联系信息 -->
						<a-col :span="12">
							<a-form-item label="门店电话：" name="storePhone">
								<a-input v-model:value="formData.storePhone" allow-clear placeholder="请输入门店电话" />
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="联系人：" name="contactPerson">
								<a-input v-model:value="formData.contactPerson" allow-clear placeholder="请输入联系人" />
							</a-form-item>
						</a-col>
					</a-row>
				</a-tab-pane>

				<!-- 详细信息（可选） -->
				<a-tab-pane key="detail" tab="详细信息">
					<a-row :gutter="16">
						<!-- 门店图片 -->
						<a-col :span="24">
							<a-form-item label="门店图片：" name="imageUrl">
								<xn-upload v-model:value="formData.imageUrl" uploadMode="image">
									<a-button type="primary">上传文件</a-button>
								</xn-upload>
							</a-form-item>
						</a-col>

						<!-- 其他联系信息 -->
						<a-col :span="12">
							<a-form-item label="联系电话：" name="contactPhone">
								<a-input v-model:value="formData.contactPhone" allow-clear placeholder="请输入联系电话" />
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="联系邮箱：" name="contactEmail">
								<a-input v-model:value="formData.contactEmail" allow-clear placeholder="请输入联系邮箱" />
							</a-form-item>
						</a-col>

						<!-- 营业信息 -->
						<a-col :span="8">
							<a-form-item label="营业时间：" name="businessHours">
								<a-time-range-picker
									v-model:value="formData.businessHours"
									format="HH:mm"
									:placeholder="['开始时间', '结束时间']"
									style="width: 100%"
								/>
							</a-form-item>
						</a-col>
						<a-col :span="8">
							<a-form-item label="门店面积(平方米)：" name="storeArea">
								<a-input v-model:value="formData.storeArea" allow-clear placeholder="请输入门店面积(平方米)" />
							</a-form-item>
						</a-col>
						<a-col :span="8">
							<a-form-item label="营业执照号：" name="businessLicense">
								<a-input v-model:value="formData.businessLicense" allow-clear placeholder="请输入营业执照号" />
							</a-form-item>
						</a-col>

						<!-- 其他信息 -->
						<a-col :span="24">
							<a-form-item label="门店描述：" name="description">
								<a-textarea v-model:value="formData.description" allow-clear placeholder="请输入门店描述" />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="备注：" name="remark">
								<a-textarea v-model:value="formData.remark" allow-clear placeholder="请输入备注" />
							</a-form-item>
						</a-col>
					</a-row>
				</a-tab-pane>
			</a-tabs>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button :loading="submitLoading" type="primary" @click="onSubmit">保存</a-button>
		</template>
	</xn-form-container>
	<LocationPicker
		api-key="5C0uS5aCGaW5Xhig7VGWJ96Hm6c4uFJI"
		:latitude="formData.latitude"
		:longitude="formData.longitude"
		:address="formData.detailAddress"
		:province="formData.province"
		:city="formData.city"
		:district="formData.district"
		@select="handleLocationSelect"
		ref="locationPickerRef"
	></LocationPicker>
</template>

<script name="wineStoreForm" setup>
	// 导入XnClientUserSelector组件
	// 改动原因：需要在表单中使用该组件选择客户端用户
	// 每一步改动：添加import语句以引入组件
	import XnClientUserSelector from '@/components/XnClientUserSelector/index.vue'
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import wineStoreApi from '@/api/wine/wineStoreApi'
	import clientUserApi from '@/api/client/clientUserApi'
	import tool from '@/utils/tool'
	import dayjs from 'dayjs'
	import LocationPicker from '@/components/Map/LocationPicker.vue'
	// 抽屉状态
	const open = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	const submitLoading = ref(false)
	const statusOptions = ref([])
	const locationPickerRef = ref()
	// 当前激活的tab
	const activeTab = ref('basic')
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		// 重置到基本信息tab
		activeTab.value = 'basic'
		if (record) {
			let recordData = cloneDeep(record)
			// 如果有营业时间数据且为字符串格式，则转换为dayjs对象数组
			if (
				recordData.businessHours &&
				typeof recordData.businessHours === 'string' &&
				recordData.businessHours.includes('-')
			) {
				const [startTime, endTime] = recordData.businessHours.split('-')
				recordData.businessHours = [dayjs(startTime, 'HH:mm'), dayjs(endTime, 'HH:mm')]
			}
			formData.value = Object.assign({}, recordData)
		}
		// 加载门店状态选项
		statusOptions.value = tool.dictList('COMMON_STATUS')
	}
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
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		formData.value = {}
		open.value = false
	}
	// 默认要校验的
	const formRules = {
		storeName: [required('请输入门店名称')],
		detailAddress: [required('请输入详细地址')]
	}
	// 处理地图选点事件
	const handleLocationSelect = (location) => {
		formData.value.latitude = location.latitude
		formData.value.longitude = location.longitude
		// 如果有地址信息，也可以更新详细地址字段
		if (location.address) {
			formData.value.detailAddress = location.address
		}
		// 更新省份、城市、县区信息
		if (location.province) {
			formData.value.province = location.province
		}
		if (location.city) {
			formData.value.city = location.city
		}
		if (location.district) {
			formData.value.district = location.district
		}
		console.log('地图选点结果:', location)
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				const formDataParam = cloneDeep(formData.value)
				// 格式化营业时间字段
				if (
					formDataParam.businessHours &&
					Array.isArray(formDataParam.businessHours) &&
					formDataParam.businessHours.length === 2
				) {
					const [start, end] = formDataParam.businessHours
					formDataParam.businessHours = `${start.format('HH:mm')}-${end.format('HH:mm')}`
				}
				wineStoreApi
					.wineStoreSubmitForm(formDataParam, formDataParam.id)
					.then(() => {
						onClose()
						emit('successful')
					})
					.finally(() => {
						submitLoading.value = false
					})
			})
			.catch(() => {
				// 如果验证失败，切换到包含错误字段的tab
				// 这里可以根据具体的验证错误来判断应该切换到哪个tab
				// 暂时切换到基本信息tab，因为必须字段都在那里
				activeTab.value = 'basic'
			})
	}
	// 抛出函数
	defineExpose({
		onOpen
	})
</script>

<style scoped>
	/* 自定义tab样式 */
	:deep(.ant-tabs-tab) {
		font-weight: 500;
	}

	:deep(.ant-tabs-tab-active) {
		font-weight: 600;
	}

	/* 表单项间距调整 */
	:deep(.ant-form-item) {
		margin-bottom: 16px;
	}

	/* tab内容区域样式 */
	:deep(.ant-tabs-tabpane) {
		padding-top: 16px;
	}
</style>
