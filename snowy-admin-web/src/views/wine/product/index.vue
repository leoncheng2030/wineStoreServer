<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="酒品名称" name="productName">
						<a-input v-model:value="searchFormState.productName" placeholder="请输入酒品名称" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="酒品分类" name="productType">
						<a-select
							v-model:value="searchFormState.categoryId"
							placeholder="请选择酒品分类"
							:options="categoryOptions"
							:field-names="{ label: 'categoryName', value: 'id' }"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="状态" name="status">
						<a-select v-model:value="searchFormState.status" placeholder="请选择状态" :options="statusOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-button type="primary" @click="tableRef.refresh()">查询</a-button>
					<a-button style="margin: 0 8px" @click="reset">重置</a-button>
				</a-col>
			</a-row>
		</a-form>
		<s-table
			ref="tableRef"
			:columns="columns"
			:data="loadData"
			:alert="options.alert.show"
			bordered
			:row-key="(record) => record.id"
			:tool-config="toolConfig"
			:row-selection="options.rowSelection"
		>
			<template #operator class="table-operator">
				<a-space>
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('wineProductAdd')">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
					<a-button @click="importModelRef.onOpen()" v-if="hasPerm('wineProductImport')">
						<template #icon><import-outlined /></template>
						<span>导入</span>
					</a-button>
					<a-button @click="exportData" v-if="hasPerm('wineProductExport')">
						<template #icon><export-outlined /></template>
						<span>导出</span>
					</a-button>
					<xn-batch-button
						v-if="hasPerm('wineProductBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchWineProduct"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'productType'">
					{{ $TOOL.dictTypeData('COMMON_STATUS', record.productType) }}
				</template>
				<template v-if="column.dataIndex === 'status'">
					{{ $TOOL.dictTypeData('COMMON_STATUS', record.status) }}
				</template>
				<template v-if="column.dataIndex === 'imageUrl'">
					<div class="tableInfo">
						<div class="image">
							<img :src="record.imageUrl" style="width: 60px; height: 60px" />
						</div>
						<div class="info">
							<div class="name">{{ record.productName }}</div>
							<div class="description">{{ record.productCode }}</div>
							<div class="description">{{ record.manufacturer }}</div>
						</div>
					</div>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)" v-if="hasPerm('wineProductEdit')">编辑</a>
						<a-divider type="vertical" v-if="hasPerm(['wineProductEdit', 'wineProductDelete'], 'and')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteWineProduct(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('wineProductDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<ImportModel ref="importModelRef" />
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="product">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import ImportModel from './importModel.vue'
	import downloadUtil from '@/utils/downloadUtil'
	import wineProductApi from '@/api/wine/wineProductApi'
	import wineProductCategoryApi from '@/api/wine/wineProductCategoryApi'
	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const importModelRef = ref()
	const formRef = ref()
	const categoryOptions = ref([])
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	const columns = [
		{
			title: '酒品',
			dataIndex: 'imageUrl'
		},
		{
			title: '酒品分类',
			dataIndex: 'categoryName'
		},
		{
			title: '酒精含量(%)',
			dataIndex: 'alcoholContent'
		},
		{
			title: '产地',
			dataIndex: 'origin'
		},
		{
			title: '成本价(元/ml)',
			dataIndex: 'costPrice'
		},
		{
			title: '单价(元/ml)',
			dataIndex: 'unitPrice'
		},
		{
			title: '酒品描述',
			dataIndex: 'description'
		},
		{
			title: '状态',
			dataIndex: 'status'
		},
		{
			title: '排序码',
			dataIndex: 'sortCode'
		}
	]
	// 操作栏通过权限判断是否显示
	if (hasPerm(['wineProductEdit', 'wineProductDelete'])) {
		columns.push({
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 150
		})
	}
	const selectedRowKeys = ref([])
	// 列表选择配置
	const options = {
		// columns数字类型字段加入 needTotal: true 可以勾选自动算账
		alert: {
			show: true,
			clear: () => {
				selectedRowKeys.value = ref([])
			}
		},
		rowSelection: {
			onChange: (selectedRowKey, selectedRows) => {
				selectedRowKeys.value = selectedRowKey
			}
		}
	}
	const loadData = (parameter) => {
		wineProductCategoryApi.wineProductCategoryTree().then((data) => {
			categoryOptions.value = data
		})
		const searchFormParam = cloneDeep(searchFormState.value)
		return wineProductApi.wineProductPage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteWineProduct = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		wineProductApi.wineProductDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 导出
	const exportData = () => {
		if (selectedRowKeys.value.length > 0) {
			const params = selectedRowKeys.value.map((m) => {
				return {
					id: m
				}
			})
			wineProductApi.wineProductExport(params).then((res) => {
				downloadUtil.resultDownload(res)
			})
		} else {
			wineProductApi.wineProductExport([]).then((res) => {
				downloadUtil.resultDownload(res)
			})
		}
	}
	// 批量删除
	const deleteBatchWineProduct = (params) => {
		wineProductApi.wineProductDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}

	const statusOptions = tool.dictList('COMMON_STATUS')
</script>
