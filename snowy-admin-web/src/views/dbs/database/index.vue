<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="名称" name="poolName">
						<a-input v-model:value="searchFormState.poolName" placeholder="请输入名称" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="驱动名称" name="driverName">
						<a-select
							v-model:value="searchFormState.driverName"
							placeholder="请选择驱动名称"
							:options="driverNameOptions"
						/>
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
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('extDatabaseAdd')">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
					<a-button @click="importModelRef.onOpen()" v-if="hasPerm('extDatabaseImport')">
						<template #icon><import-outlined /></template>
						<span>导入</span>
					</a-button>
					<a-button @click="exportData" v-if="hasPerm('extDatabaseExport')">
						<template #icon><export-outlined /></template>
						<span>导出</span>
					</a-button>
					<xn-batch-button
						v-if="hasPerm('extDatabaseBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchExtDatabase"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'driverName'">
					{{ $TOOL.dictTypeData('DATABASE_DRIVE_TYPE', record.driverName) }}
				</template>
				<template v-if="column.dataIndex === 'category'">
					{{ $TOOL.dictTypeData('DBS_CATEGORY', record.category) }}
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)" v-if="hasPerm('extDatabaseEdit')">编辑</a>
						<a-divider type="vertical" v-if="hasPerm(['extDatabaseEdit', 'extDatabaseDelete'], 'and')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteExtDatabase(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('extDatabaseDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<ImportModel ref="importModelRef" />
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="database">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import ImportModel from './importModel.vue'
	import downloadUtil from '@/utils/downloadUtil'
	import extDatabaseApi from '@/api/dbs/extDatabaseApi'
	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const importModelRef = ref()
	const formRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	const columns = [
		{
			title: '名称',
			dataIndex: 'poolName'
		},
		{
			title: '连接URL',
			dataIndex: 'url'
		},
		{
			title: '用户名',
			dataIndex: 'username'
		},
		{
			title: '密码',
			dataIndex: 'password'
		},
		{
			title: '驱动名称',
			dataIndex: 'driverName'
		},
		{
			title: '分类',
			dataIndex: 'category'
		},
		{
			title: '排序码',
			dataIndex: 'sortCode'
		}
	]
	// 操作栏通过权限判断是否显示
	if (hasPerm(['extDatabaseEdit', 'extDatabaseDelete'])) {
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
		return extDatabaseApi.extDatabasePage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteExtDatabase = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		extDatabaseApi.extDatabaseDelete(params).then(() => {
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
			extDatabaseApi.extDatabaseExport(params).then((res) => {
				downloadUtil.resultDownload(res)
			})
		} else {
			extDatabaseApi.extDatabaseExport([]).then((res) => {
				downloadUtil.resultDownload(res)
			})
		}
	}
	// 批量删除
	const deleteBatchExtDatabase = (params) => {
		extDatabaseApi.extDatabaseDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	const driverNameOptions = tool.dictList('COMMON_STATUS')
</script>
