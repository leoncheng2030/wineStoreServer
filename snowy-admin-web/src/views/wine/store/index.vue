<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="门店名称" name="storeName">
						<a-input v-model:value="searchFormState.storeName" placeholder="请输入门店名称" />
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
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('wineStoreAdd')">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
					<a-button @click="importModelRef.onOpen()" v-if="hasPerm('wineStoreImport')">
                        <template #icon><import-outlined /></template>
                        <span>导入</span>
                    </a-button>
                    <a-button @click="exportData" v-if="hasPerm('wineStoreExport')">
                        <template #icon><export-outlined /></template>
                        <span>导出</span>
                    </a-button>
					<xn-batch-button
						v-if="hasPerm('wineStoreBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchWineStore"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'status'">
					{{ $TOOL.dictTypeData('COMMON_STATUS', record.status) }}
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)" v-if="hasPerm('wineStoreEdit')">编辑</a>
						<a-divider type="vertical" v-if="hasPerm(['wineStoreEdit', 'wineStoreDelete'], 'and')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteWineStore(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('wineStoreDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<ImportModel ref="importModelRef" />
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="store">
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import ImportModel from './importModel.vue'
    import downloadUtil from '@/utils/downloadUtil'
	import wineStoreApi from '@/api/wine/wineStoreApi'
	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const importModelRef = ref()
	const formRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	const columns = [
		{
			title: '门店名称',
			dataIndex: 'storeName'
		},
		{
			title: '门店编码',
			dataIndex: 'storeCode'
		},
		{
			title: '门店电话',
			dataIndex: 'storePhone'
		},
		{
			title: '门店管理员姓名',
			dataIndex: 'storeManagerName'
		},
		{
			title: '详细地址',
			dataIndex: 'detailAddress'
		},
		{
			title: '联系电话',
			dataIndex: 'contactPhone'
		},
		{
			title: '营业时间',
			dataIndex: 'businessHours'
		},
		{
			title: '门店面积(平方米)',
			dataIndex: 'storeArea'
		},
		{
			title: '门店状态',
			dataIndex: 'status'
		},
		{
			title: '排序码',
			dataIndex: 'sortCode'
		},
		{
			title: '备注',
			dataIndex: 'remark'
		}
	]
	// 操作栏通过权限判断是否显示
	if (hasPerm(['wineStoreEdit', 'wineStoreDelete'])) {
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
		return wineStoreApi.wineStorePage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteWineStore = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		wineStoreApi.wineStoreDelete(params).then(() => {
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
            wineStoreApi.wineStoreExport(params).then((res) => {
                downloadUtil.resultDownload(res)
            })
        } else {
            wineStoreApi.wineStoreExport([]).then((res) => {
                downloadUtil.resultDownload(res)
            })
        }
    }
	// 批量删除
	const deleteBatchWineStore = (params) => {
		wineStoreApi.wineStoreDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>
