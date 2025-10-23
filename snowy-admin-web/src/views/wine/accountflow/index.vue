<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="用户ID" name="userId">
						<a-input v-model:value="searchFormState.userId" placeholder="请输入用户ID" />
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
			<template #operator>
				<a-space>
					<xn-batch-button
						v-if="hasPerm('wineAccountFlowBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchWineAccountFlow"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'flowType'">
					{{ $TOOL.dictTypeData('ACCOUNT_FLOW_TYPE', record.flowType) }}
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a-popconfirm title="确定要删除吗？" @confirm="deleteWineAccountFlow(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('wineAccountFlowDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<ImportModel ref="importModelRef" />
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="accountflow">
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import ImportModel from './importModel.vue'
	import downloadUtil from '@/utils/downloadUtil'
	import wineAccountFlowApi from '@/api/wine/wineAccountFlowApi'
	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const importModelRef = ref()
	const formRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	const columns = [
		{
			title: '流水号',
			dataIndex: 'flowNo'
		},
		{
			title: '用户昵称',
			dataIndex: 'userNickname'
		},
		{
			title: '流水类型',
			dataIndex: 'flowType'
		},
		{
			title: '金额(元)',
			dataIndex: 'amount'
		},
		{
			title: '余额变动',
			dataIndex: 'balanceChange'
		},
		{
			title: '变动前(元)',
			dataIndex: 'beforeBalance'
		},
		{
			title: '变动后(元)',
			dataIndex: 'afterBalance'
		},
		{
			title: '关联单号',
			dataIndex: 'relatedNo'
		},
		{
			title: '流水描述',
			dataIndex: 'description'
		},
		{
			title: '交易时间',
			dataIndex: 'transactionTime'
		}
	]
	// 操作栏通过权限判断是否显示
	if (hasPerm(['wineAccountFlowEdit', 'wineAccountFlowDelete'])) {
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
		return wineAccountFlowApi.wineAccountFlowPage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteWineAccountFlow = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		wineAccountFlowApi.wineAccountFlowDelete(params).then(() => {
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
			wineAccountFlowApi.wineAccountFlowExport(params).then((res) => {
				downloadUtil.resultDownload(res)
			})
		} else {
			wineAccountFlowApi.wineAccountFlowExport([]).then((res) => {
				downloadUtil.resultDownload(res)
			})
		}
	}
	// 批量删除
	const deleteBatchWineAccountFlow = (params) => {
		wineAccountFlowApi.wineAccountFlowDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>
