<template>
	<!-- 这是引入后展示的样式 -->
	<a-flex wrap="wrap" gap="small" v-if="props.wineShow">
		<div
			class="wine-container"
			v-for="(wine, index) in wineObj"
			:key="wine.id"
			@mouseover="onMouseEnter(index)"
			@mouseleave="onMouseLeave(index)"
		>
			<span class="wine-delete">
				<CloseCircleFilled
					:class="index === deleteShow ? 'show-delete-icon' : ''"
					class="delete-icon"
					@click="deleteWine(wine)"
				/>
				<a-avatar :src="wine.productImage" />
			</span>
			<span class="wine-name">{{ wine.productName }}</span>
		</div>
		<a-button shape="circle" @click="openModal" v-if="(props.radioModel ? wineObj.length !== 1 : true) && addShow">
			<PlusOutlined />
		</a-button>
		<slot name="button"></slot>
	</a-flex>

	<!-- 以下是弹窗内容 -->
	<a-modal
		v-model:open="visible"
		title="酒品选择"
		:width="1000"
		:mask-closable="false"
		:destroy-on-close="true"
		@ok="handleOk"
		@cancel="handleClose"
	>
		<a-row :gutter="10">
			<a-col :span="6">
				<div class="selectorTreeDiv">
					<a-tree
						v-if="treeData.length > 0"
						v-model:selectedKeys="selectedKeys"
						:tree-data="treeData"
						:field-names="{ children: 'children', title: 'title', key: 'id' }"
						@select="treeSelect"
					/>
				</div>
			</a-col>
			<a-col :span="12">
				<div class="table-operator xn-mb10">
					<a-form ref="searchFormRef" name="advanced_search" class="ant-advanced-search-form" :model="searchFormState">
						<a-row :gutter="24">
							<a-col :span="12">
								<a-form-item name="searchKey">
									<a-input v-model:value="searchFormState.searchKey" placeholder="请输入酒品名称" />
								</a-form-item>
							</a-col>
							<a-col :span="12">
								<a-button type="primary" class="xn-mr-10" @click="loadData()"> 查询 </a-button>
								<a-button @click="reset()"> 重置 </a-button>
							</a-col>
						</a-row>
					</a-form>
				</div>
				<div class="wine-table">
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
							<template v-if="column.dataIndex === 'productImage'">
								<a-avatar :src="record.productImage" style="margin-bottom: -5px; margin-top: -5px" />
							</template>
							<template v-if="column.dataIndex === 'action'">
								<a-button type="dashed" size="small" @click="addRecord(record)"><PlusOutlined /></a-button>
							</template>
							<template v-if="column.dataIndex === 'categoryName'">
								{{ record.categoryName }}
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
				<div class="wine-table">
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

<script setup name="wineSelector">
	// 注意：此组件是参考XnClientUserSelector创建的酒品选择器
	// 修改原因：为酒品管理提供选择界面和功能
	// 每一步改动：1. 更改标题为"酒品选择"；2. 使用wineProductApi.wineProductPage作为分页API；3. 使用wineProductCategoryApi.wineProductCategoryTree作为树形API
	import { message } from 'ant-design-vue'
	import { remove, isEmpty, cloneDeep } from 'lodash-es'
	import { CloseCircleFilled, PlusOutlined, MinusOutlined } from '@ant-design/icons-vue'
	import wineProductApi from '@/api/wine/wineProductApi' // 酒品API
	import wineProductCategoryApi from '@/api/wine/wineProductCategoryApi' // 酒品分类API
	
	// 弹窗是否打开
	const visible = ref(false)
	const deleteShow = ref('')
	const selectedKeys = ref([])
	const treeData = ref([])
	
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
			dataIndex: 'productImage',
			width: 50
		},
		{
			title: '酒品名称',
			dataIndex: 'productName',
			ellipsis: true
		},
		{
			title: '分类',
			dataIndex: 'categoryName'
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
			title: '酒品名称',
			dataIndex: 'productName',
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
		categoryTreeApi: {
			type: Function,
			default: () => wineProductCategoryApi.wineProductCategoryTree // 默认使用酒品分类树API
		},
		winePageApi: {
			type: Function,
			default: () => wineProductApi.wineProductPage // 默认使用酒品分页API
		},
		wineListByIdListApi: {
			type: Function
		},
		value: {
			default: () => ''
		},
		dataType: {
			type: String,
			default: () => 'string'
		},
		wineShow: {
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
			default: () => '请选择酒品'
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
	const wineListByIdList = (param) => {
		if (typeof props.wineListByIdListApi === 'function') {
			return props.wineListByIdListApi(param)
		} else {
			// 如果没有提供专门的批量查询API，使用分页API模拟
			return props.winePageApi({ idList: param.idList })
		}
	}
	
	// 打开弹框
	const showWineModal = (ids = []) => {
		const data = goDataConverter(ids)
		recordIds.value = data
		getWineById(data)
		openModal()
	}
	
	const onMouseEnter = (index) => {
		deleteShow.value = index
	}
	
	const onMouseLeave = (index) => {
		deleteShow.value = ''
	}
	
	// 树形选择事件
	const treeSelect = (selectedKeys) => {
		if (selectedKeys.length > 0) {
			searchFormState.value.categoryId = selectedKeys[0]
		} else {
			delete searchFormState.value.categoryId
		}
		loadData()
	}
	
	const openModal = () => {
		if (typeof props.winePageApi !== 'function') {
			message.warning('未配置选择器需要的winePageApi接口')
			return
		}
		visible.value = true
		
		// 加载分类树
		loadCategoryTree()
		
		searchFormState.value.size = pageSize.value
		loadData()
		if (isEmpty(recordIds.value)) {
			return
		}
		const param = {
			idList: recordIds.value
		}
		selectedTableListLoading.value = true
		wineListByIdList(param)
			.then((data) => {
				selectedData.value = data.records || data
			})
			.finally(() => {
				selectedTableListLoading.value = false
			})
	}
	
	// 加载分类树
	const loadCategoryTree = () => {
		if (typeof props.categoryTreeApi === 'function') {
			props.categoryTreeApi().then((data) => {
				treeData.value = data
			})
		}
	}
	
	// 点击删除酒品
	const deleteWine = (wine) => {
		// 删除显示的
		remove(wineObj.value, (item) => item.id === wine.id)
		// 删除缓存的
		remove(recordIds.value, (item) => item === wine.id)
		const value = []
		const showWine = []
		wineObj.value.forEach((item) => {
			const obj = {
				id: item.id,
				productName: item.productName
			}
			value.push(item.id)
			// 拷贝一份obj数据
			const objClone = cloneDeep(obj)
			objClone.productImage = item.productImage
			showWine.push(objClone)
		})
		wineObj.value = showWine
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
		
		props
			.winePageApi(searchFormState.value)
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
	
	const wineObj = ref([])
	
	// 确定
	const handleOk = () => {
		wineObj.value = []
		const value = []
		const showWine = []
		selectedData.value.forEach((item) => {
			const obj = {
				id: item.id,
				productName: item.productName
			}
			value.push(item.id)
			// 拷贝一份obj数据
			const objClone = cloneDeep(obj)
			objClone.productImage = item.productImage
			showWine.push(objClone)
		})
		wineObj.value = showWine
		// 判断是否做数据的转换为工作流需要的
		const resultData = outDataConverter(value)
		emit('update:value', resultData)
		emit('onBack', resultData)
		handleClose()
	}
	
	// 重置
	const reset = () => {
		delete searchFormState.value.searchKey
		delete searchFormState.value.categoryId
		selectedKeys.value = []
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
		selectedKeys.value = []
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
			data = wineObj.value
			let label = ''
			let value = ''
			const obj = {}
			for (let i = 0; i < data.length; i++) {
				if (i === data.length - 1) {
					label = label + data[i].productName
					value = value + data[i].id
				} else {
					label = label + data[i].productName + ','
					value = value + data[i].id + ','
				}
			}
			obj.key = 'WINE'
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
	
	const getWineById = (ids) => {
		if (isEmpty(wineObj.value) && !isEmpty(ids)) {
			const param = {
				idList: recordIds.value
			}
			// 这里必须转为数组类型的
			wineListByIdList(param).then((data) => {
				wineObj.value = data.records || data
			})
		}
	}
	
	watch(
		() => props.value,
		(newValue) => {
			if (!isEmpty(props.value)) {
				const ids = goDataConverter(newValue)
				recordIds.value = ids
				getWineById(ids)
			} else {
				wineObj.value = []
				selectedData.value = []
			}
		},
		{
			immediate: true // 立即执行
		}
	)
	
	defineExpose({
		showWineModal
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
	.wine-table {
		overflow: auto;
		max-height: 450px;
	}

	.wine-container {
		display: flex;
		align-items: center; /* 垂直居中 */
		flex-direction: column;
		margin-right: 10px;
		text-align: center;
	}
	.wine-avatar {
		width: 30px;
		border-radius: 50%; /* 设置为50%以创建圆形头像 */
	}
	.wine-name {
		font-size: 12px;
		max-width: 50px;
		white-space: nowrap;
		overflow: hidden;
	}
	.wine-delete {
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