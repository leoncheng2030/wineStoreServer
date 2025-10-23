<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="分类名称" name="categoryName">
						<a-input v-model:value="searchFormState.categoryName" placeholder="请输入分类名称" />
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
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('wineProductCategoryAdd')">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
					<a-button @click="importModelRef.onOpen()" v-if="hasPerm('wineProductCategoryImport')">
                        <template #icon><import-outlined /></template>
                        <span>导入</span>
                    </a-button>
                    <a-button @click="exportData" v-if="hasPerm('wineProductCategoryExport')">
                        <template #icon><export-outlined /></template>
                        <span>导出</span>
                    </a-button>
					<xn-batch-button
						v-if="hasPerm('wineProductCategoryBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchWineProductCategory"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'status'">
					{{ $TOOL.dictTypeData('COMMON_STATUS', record.status) }}
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)" v-if="hasPerm('wineProductCategoryEdit')">编辑</a>
						<a-divider type="vertical" v-if="hasPerm(['wineProductCategoryEdit', 'wineProductCategoryDelete'], 'and')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteWineProductCategory(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('wineProductCategoryDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<ImportModel ref="importModelRef" />
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="productcategory">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import ImportModel from './importModel.vue'
    import downloadUtil from '@/utils/downloadUtil'
	import wineProductCategoryApi from '@/api/wine/wineProductCategoryApi'
	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const importModelRef = ref()
	const formRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	const columns = [
		{
			title: '分类名称',
			dataIndex: 'categoryName'
		},
		{
			title: '父级分类ID，0表示顶级分类',
			dataIndex: 'parentId'
		},
		{
			title: '分类级别，1为一级分类',
			dataIndex: 'categoryLevel'
		},
		{
			title: '分类描述',
			dataIndex: 'description'
		},
		{
			title: '状态',
			dataIndex: 'status'
		},
		{
			title: '排序码，数字越小越靠前',
			dataIndex: 'sortCode'
		},
		{
			title: '备注',
			dataIndex: 'remark'
		},
	]
	// 操作栏通过权限判断是否显示
	if (hasPerm(['wineProductCategoryEdit', 'wineProductCategoryDelete'])) {
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
		const searchFormParam = cloneDeep(searchFormState.value)
		return wineProductCategoryApi.wineProductCategoryPage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteWineProductCategory = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		wineProductCategoryApi.wineProductCategoryDelete(params).then(() => {
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
            wineProductCategoryApi.wineProductCategoryExport(params).then((res) => {
                downloadUtil.resultDownload(res)
            })
        } else {
            wineProductCategoryApi.wineProductCategoryExport([]).then((res) => {
                downloadUtil.resultDownload(res)
            })
        }
    }
	// 批量删除
	const deleteBatchWineProductCategory = (params) => {
		wineProductCategoryApi.wineProductCategoryDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	const statusOptions = tool.dictList('COMMON_STATUS')
</script>
