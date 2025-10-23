<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="用户ID" name="clientUserId">
						<a-input v-model:value="searchFormState.clientUserId" placeholder="请输入用户ID" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="代理商编号" name="agentCode">
						<a-input v-model:value="searchFormState.agentCode" placeholder="请输入代理商编号" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="子商户号" name="subMerId">
						<a-input v-model:value="searchFormState.subMerId" placeholder="请输入子商户号" />
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
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('wineAgentAdd')">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
					<a-button @click="importModelRef.onOpen()" v-if="hasPerm('wineAgentImport')">
						<template #icon><import-outlined /></template>
						<span>导入</span>
					</a-button>
					<a-button @click="exportData" v-if="hasPerm('wineAgentExport')">
						<template #icon><export-outlined /></template>
						<span>导出</span>
					</a-button>
					<xn-batch-button
						v-if="hasPerm('wineAgentBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchWineAgent"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)" v-if="hasPerm('wineAgentEdit')">编辑</a>
						<a-divider type="vertical" v-if="hasPerm(['wineAgentEdit', 'wineAgentDelete'], 'and')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteWineAgent(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('wineAgentDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<ImportModel ref="importModelRef" />
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="agent">
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import ImportModel from './importModel.vue'
	import downloadUtil from '@/utils/downloadUtil'
	import wineAgentApi from '@/api/wine/wineAgentApi'
	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const importModelRef = ref()
	const formRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	const columns = [
		{
			title: '用户ID',
			dataIndex: 'clientUserId'
		},
		{
			title: '代理商编号',
			dataIndex: 'agentCode'
		},
		{
			title: '用户名',
			dataIndex: 'clientNickname'
		},
		{
			title: '手机号',
			dataIndex: 'clientPhone'
		},
		{
			title: '子商户号',
			dataIndex: 'subMerId'
		},
		{
			title: '子应用ID',
			dataIndex: 'subAppId'
		},
		{
			title: '子商户分账比例',
			dataIndex: 'profitSharingMaxRate'
		}
	]
	// 操作栏通过权限判断是否显示
	if (hasPerm(['wineAgentEdit', 'wineAgentDelete'])) {
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
		return wineAgentApi.wineAgentPage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteWineAgent = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		wineAgentApi.wineAgentDelete(params).then(() => {
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
			wineAgentApi.wineAgentExport(params).then((res) => {
				downloadUtil.resultDownload(res)
			})
		} else {
			wineAgentApi.wineAgentExport([]).then((res) => {
				downloadUtil.resultDownload(res)
			})
		}
	}
	// 批量删除
	const deleteBatchWineAgent = (params) => {
		wineAgentApi.wineAgentDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>
