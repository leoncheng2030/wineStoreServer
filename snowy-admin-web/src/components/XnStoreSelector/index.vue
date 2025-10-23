<template>
	<!-- 这是引入后展示的样式 -->
	<a-flex wrap="wrap" gap="small" v-if="props.storeShow">
		<div
			class="store-container"
			v-for="(store, index) in storeObj"
			:key="store.id"
			@mouseover="onMouseEnter(index)"
			@mouseleave="onMouseLeave(index)"
		>
			<span class="store-delete">
				<CloseCircleFilled
					:class="index === deleteShow ? 'show-delete-icon' : ''"
					class="delete-icon"
					@click="deleteStore(store)"
				/>
				<a-avatar :src="store.storeImage" >
					<template #icon><ShopOutlined /></template>
				</a-avatar>
			</span>
			<span class="store-name">{{ store.storeName }}</span>
		</div>
		<a-button shape="circle" @click="openModal" v-if="(props.radioModel ? storeObj.length !== 1 : true) && addShow">
			<PlusOutlined />
		</a-button>
		<slot name="button"></slot>
	</a-flex>

	<!-- 以下是弹窗内容 -->
	<a-modal
		v-model:open="visible"
		title="门店选择"
		:width="1000"
		:mask-closable="false"
		:destroy-on-close="true"
		@ok="handleOk"
		@cancel="handleClose"
	>
		<a-row :gutter="10">
			<a-col :span="18">
				<div class="table-operator xn-mb10">
					<a-form ref="searchFormRef" name="advanced_search" class="ant-advanced-search-form" :model="searchFormState">
						<a-row :gutter="24">
							<a-col :span="12">
								<a-form-item name="searchKey">
									<a-input v-model:value="searchFormState.searchKey" placeholder="请输入门店名称" />
								</a-form-item>
							</a-col>
							<a-col :span="12">
								<a-button type="primary" class="xn-mr-10" @click="loadData()"> 查询 </a-button>
								<a-button @click="reset()"> 重置 </a-button>
							</a-col>
						</a-row>
					</a-form>
				</div>
				<div class="store-table">
					<a-table
						ref="tableRef"
						size="small"
						:columns="commons"
						:data-source="tableData"
						:expand-row-by-click="true"
						:loading="pageLoading"
						bordered
						:pagination="false"
					>
						<template #title>
							<span>待选择列表 {{ tableRecordNum }} 条</span>
							<div v-if="!radioModel" class="xn-fdr">
								<a-button type="dashed" size="small" @click="addAllPageRecord">添加当前数据</a-button>
							</div>
						</template>
						<template #bodyCell="{ column, record }">
							<template v-if="column.dataIndex === 'storeImage'">
								<a-avatar :src="record.storeImage" style="margin-bottom: -5px; margin-top: -5px">
									<template #icon><ShopOutlined /></template>
								</a-avatar>
							</template>
							<template v-if="column.dataIndex === 'action'">
								<a-button type="dashed" size="small" @click="addRecord(record)"><PlusOutlined /></a-button>
							</template>
							<template v-if="column.dataIndex === 'province'">
								{{ record.province }}
							</template>
							<template v-if="column.dataIndex === 'city'">
								{{ record.city }}
							</template>
						</template>
					</a-table>
					<div class="mt-2">
						<a-pagination
							v-if="!isEmpty(tableData)"
							v-model:current="current"
							v-model:page-size="pageSize"
							:total="total"
							size="small"
							showSizeChanger
							@change="paginationChange"
						/>
					</div>
				</div>
			</a-col>
			<a-col :span="6">
				<div class="store-table">
					<a-table
						ref="selectedTable"
						size="small"
						:columns="selectedCommons"
						:data-source="selectedData"
						:expand-row-by-click="true"
						:loading="selectedTableListLoading"
						bordered
					>
						<template #title>
							<span>已选择: {{ selectedData.length }}</span>
							<div v-if="!radioModel" class="xn-fdr">
								<a-button type="dashed" danger size="small" @click="delAllRecord">全部移除</a-button>
							</div>
						</template>
						<template #bodyCell="{ column, record }">
							<template v-if="column.dataIndex === 'action'">
								<a-button type="dashed" danger size="small" @click="delRecord(record)"><MinusOutlined /></a-button>
							</template>
						</template>
					</a-table>
				</div>
			</a-col>
		</a-row>
	</a-modal>
</template>

<script setup name="storeSelector">
	// 注意：此组件是参考XnClientUserSelector创建的门店选择器
	// 修改原因：为门店管理提供选择界面和功能
	// 每一步改动：1. 更改标题为"门店选择"；2. 使用wineStoreApi.wineStorePage作为分页API；3. 门店不需要树形结构，简化为直接搜索
	import { message } from 'ant-design-vue'
	import { remove, isEmpty, cloneDeep } from 'lodash-es'
	import { CloseCircleFilled, ShopOutlined, PlusOutlined, MinusOutlined } from '@ant-design/icons-vue'
	import wineStoreApi from '@/api/wine/wineStoreApi' // 门店API
	
	// 弹窗是否打开
	const visible = ref(false)
	const deleteShow = ref('')
	
	// 主表格common
	const commons = [
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 50
		},
		{
			title: '图片',
			dataIndex: 'storeImage',
			width: 50
		},
		{
			title: '门店名称',
			dataIndex: 'storeName',
			ellipsis: true
		},
		{
			title: '省份',
			dataIndex: 'province'
		},
		{
			title: '城市',
			dataIndex: 'city'
		}
	]
	
	// 选中表格的表格common
	const selectedCommons = [
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 50
		},
		{
			title: '门店名称',
			dataIndex: 'storeName',
			ellipsis: true
		}
	]
	
	const props = defineProps({
		radioModel: {
			type: Boolean,
			default: () => false
		},
		dataIsConverterFlw: {
			type: Boolean,
			default: () => false
		},
		storePageApi: {
			type: Function,
			default: () => wineStoreApi.wineStorePage // 默认使用门店分页API
		},
		storeListByIdListApi: {
			type: Function
		},
		value: {
			default: () => ''
		},
		dataType: {
			type: String,
			default: () => 'string'
		},
		storeShow: {
			type: Boolean,
			default: () => true
		},
		addShow: {
			type: Boolean,
			default: () => true
		},
		// 添加placeholder属性定义，解决Vue警告
		placeholder: {
			type: String,
			default: () => '请选择门店'
		}
	})
	
	// 主表格的ref 名称
	const tableRef = ref()
	// 选中表格的ref 名称
	const selectedTable = ref()
	const tableRecordNum = ref()
	const searchFormState = ref({})
	const searchFormRef = ref()
	
	const pageLoading = ref(false)
	const selectedTableListLoading = ref(false)
	
	const emit = defineEmits(['update:value', 'onBack'])
	const tableData = ref([])
	const selectedData = ref([])
	const recordIds = ref([])
	
	// 分页相关
	const current = ref(0) // 当前页数
	const pageSize = ref(20) // 每页条数
	const total = ref(0) // 数据总数
	
	// 获取选中列表的api
	const storeListByIdList = (param) => {
		if (typeof props.storeListByIdListApi === 'function') {
			return props.storeListByIdListApi(param)
		} else {
			// 如果没有提供专门的批量查询API，使用分页API模拟
			return props.storePageApi({ idList: param.idList })
		}
	}
	
	// 打开弹框
	const showStoreModal = (ids = []) => {
		const data = goDataConverter(ids)
		recordIds.value = data
		getStoreById(data)
		openModal()
	}
	
	const onMouseEnter = (index) => {
		deleteShow.value = index
	}
	
	const onMouseLeave = (index) => {
		deleteShow.value = ''
	}
	
	const openModal = () => {
		if (typeof props.storePageApi !== 'function') {
			message.warning('未配置选择器需要的storePageApi接口')
			return
		}
		visible.value = true
		
		searchFormState.value.size = pageSize.value
		loadData()
		if (isEmpty(recordIds.value)) {
			return
		}
		const param = {
			idList: recordIds.value
		}
		selectedTableListLoading.value = true
		storeListByIdList(param)
			.then((data) => {
				selectedData.value = data.records || data
			})
			.finally(() => {
				selectedTableListLoading.value = false
			})
	}
	
	// 点击删除门店
	const deleteStore = (store) => {
		// 删除显示的
		remove(storeObj.value, (item) => item.id === store.id)
		// 删除缓存的
		remove(recordIds.value, (item) => item === store.id)
		const value = []
		const showStore = []
		storeObj.value.forEach((item) => {
			const obj = {
				id: item.id,
				storeName: item.storeName
			}
			value.push(item.id)
			// 拷贝一份obj数据
			const objClone = cloneDeep(obj)
			objClone.storeImage = item.storeImage
			showStore.push(objClone)
		})
		storeObj.value = showStore
		// 判断是否做数据的转换为工作流需要的
		const resultData = outDataConverter(value)
		emit('update:value', resultData)
		emit('onBack', resultData)
	}
	
	// 查询主表格数据
	const loadData = () => {
		pageLoading.value = true
		// 设置分页参数
		searchFormState.value.current = current.value
		searchFormState.value.size = pageSize.value
		// 设置搜索参数，门店使用storeName字段搜索
		if (searchFormState.value.searchKey) {
			searchFormState.value.storeName = searchFormState.value.searchKey
		}
		
		props
			.storePageApi(searchFormState.value)
			.then((data) => {
				current.value = data.current
				total.value = data.total
				// 重置、赋值
				tableData.value = []
				tableRecordNum.value = 0
				tableData.value = data.records
				if (data.records) {
					tableRecordNum.value = data.records.length
				} else {
					tableRecordNum.value = 0
				}
			})
			.finally(() => {
				pageLoading.value = false
			})
	}
	
	// pageSize改变回调分页事件
	const paginationChange = (page, pageSize) => {
		current.value = page
		searchFormState.value.current = page
		searchFormState.value.size = pageSize
		loadData()
	}
	
	const judge = () => {
		return !(props.radioModel && selectedData.value.length > 0)
	}
	
	// 添加记录
	const addRecord = (record) => {
		if (!judge()) {
			message.warning('只可选择一条')
			return
		}
		const selectedRecord = selectedData.value.filter((item) => item.id === record.id)
		if (selectedRecord.length === 0) {
			selectedData.value.push(record)
		} else {
			message.warning('该记录已存在')
		}
	}
	
	// 添加全部
	const addAllPageRecord = () => {
		let newArray = selectedData.value.concat(tableData.value)
		let list = []
		for (let item1 of newArray) {
			let flag = true
			for (let item2 of list) {
				if (item1.id === item2.id) {
					flag = false
				}
			}
			if (flag) {
				list.push(item1)
			}
		}
		selectedData.value = list
	}
	
	// 删减记录
	const delRecord = (record) => {
		remove(selectedData.value, (item) => item.id === record.id)
	}
	
	// 删减记录
	const delAllRecord = () => {
		selectedData.value = []
	}
	
	const storeObj = ref([])
	
	// 确定
	const handleOk = () => {
		storeObj.value = []
		const value = []
		const showStore = []
		selectedData.value.forEach((item) => {
			const obj = {
				id: item.id,
				storeName: item.storeName
			}
			value.push(item.id)
			// 拷贝一份obj数据
			const objClone = cloneDeep(obj)
			objClone.storeImage = item.storeImage
			showStore.push(objClone)
		})
		storeObj.value = showStore
		// 判断是否做数据的转换为工作流需要的
		const resultData = outDataConverter(value)
		emit('update:value', resultData)
		emit('onBack', resultData)
		handleClose()
	}
	
	// 重置
	const reset = () => {
		delete searchFormState.value.searchKey
		delete searchFormState.value.storeName
		loadData()
	}
	
	const handleClose = () => {
		searchFormState.value = {}
		tableRecordNum.value = 0
		tableData.value = []
		current.value = 0
		pageSize.value = 20
		total.value = 0
		selectedData.value = []
		visible.value = false
	}
	
	// 数据进入后转换
	const goDataConverter = (data) => {
		if (props.dataIsConverterFlw) {
			const resultData = []
			// 处理对象
			if (!isEmpty(data.value)) {
				const values = data.value.split(',')
				if (values.length > 0) {
					values.forEach((id) => {
						resultData.push(id)
					})
				} else {
					resultData.push(data.value)
				}
			} else {
				// 处理数组
				if (!isEmpty(data) && !isEmpty(data[0]) && !isEmpty(data[0].value)) {
					const values = data[0].value.split(',')
					for (let i = 0; i < values.length; i++) {
						resultData.push(values[i])
					}
				}
			}
			return resultData
		} else {
			if (getValueType() !== 'string') {
				return data
			}
			if (data.length > 1) {
				const resultData = []
				data.split(',').forEach((id) => {
					resultData.push(id)
				})
				return resultData
			} else {
				return data
			}
		}
	}
	
	// 数据出口转换器
	const outDataConverter = (data) => {
		if (props.dataIsConverterFlw) {
			data = storeObj.value
			let label = ''
			let value = ''
			const obj = {}
			for (let i = 0; i < data.length; i++) {
				if (i === data.length - 1) {
					label = label + data[i].storeName
					value = value + data[i].id
				} else {
					label = label + data[i].storeName + ','
					value = value + data[i].id + ','
				}
			}
			obj.key = 'STORE'
			obj.label = label
			obj.value = value
			obj.extJson = ''
			return obj
		} else {
			if (getValueType() !== 'string') {
				return data
			}
			let resultData = ''
			data.forEach((id) => {
				resultData = resultData + ',' + id
			})
			resultData = resultData.substring(1, resultData.length)
			return resultData
		}
	}
	
	// 获取数据类型
	const getValueType = () => {
		if (props.dataType) {
			return props.dataType
		} else {
			if (props.radioModel) {
				return 'string'
			}
			return typeof typeof props.value
		}
	}
	
	const getStoreById = (ids) => {
		if (isEmpty(storeObj.value) && !isEmpty(ids)) {
			const param = {
				idList: recordIds.value
			}
			// 这里必须转为数组类型的
			storeListByIdList(param).then((data) => {
				storeObj.value = data.records || data
			})
		}
	}
	
	watch(
		() => props.value,
		(newValue) => {
			if (!isEmpty(props.value)) {
				const ids = goDataConverter(newValue)
				recordIds.value = ids
				getStoreById(ids)
			} else {
				storeObj.value = []
				selectedData.value = []
			}
		},
		{
			immediate: true // 立即执行
		}
	)
	
	defineExpose({
		showStoreModal
	})
</script>

<style lang="less" scoped>
	.xn-mr-5 {
		margin-right: 5px;
	}
	.xn-mr-10 {
		margin-right: 10px;
	}
	.selectorTreeDiv {
		max-height: 500px;
		overflow: auto;
	}
	.ant-form-item {
		margin-bottom: 0 !important;
	}
	.store-table {
		overflow: auto;
		max-height: 450px;
	}

	.store-container {
		display: flex;
		align-items: center; /* 垂直居中 */
		flex-direction: column;
		margin-right: 10px;
		text-align: center;
	}
	.store-avatar {
		width: 30px;
		border-radius: 50%; /* 设置为50%以创建圆形头像 */
	}
	.store-name {
		font-size: 12px;
		max-width: 50px;
		white-space: nowrap;
		overflow: hidden;
	}
	.store-delete {
		z-index: 99;
		color: rgba(0, 0, 0, 0.25);
		position: relative;
		display: flex;
		flex-direction: column;
	}
	.delete-icon {
		position: absolute;
		right: -2px;
		z-index: 5;
		top: -3px;
		cursor: pointer;
		visibility: hidden;
	}
	.show-delete-icon {
		visibility: visible;
	}
</style>