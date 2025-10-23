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
					<a-form-item label="手机号" name="userPhone">
						<a-input v-model:value="searchFormState.userPhone" placeholder="请输入用户手机号" />
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
						v-if="hasPerm('wineUserAccountBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchWineUserAccount"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'userAvatar'">
					<a-avatar :src="record.userAvatar" style="margin-bottom: -5px; margin-top: -5px" />
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a-popconfirm title="确定要删除吗？" @confirm="deleteWineUserAccount(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('wineUserAccountDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<ImportModel ref="importModelRef" />
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="useraccount">
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import ImportModel from './importModel.vue'
	import downloadUtil from '@/utils/downloadUtil'
	import wineUserAccountApi from '@/api/wine/wineUserAccountApi'
	import tool from '@/utils/tool'
	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const importModelRef = ref()
	const formRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }

	const columns = [
		{
			title: '头像',
			dataIndex: 'userAvatar'
		},
		{
			title: '昵称',
			dataIndex: 'userNickname'
		},
		{
			title: '手机号',
			dataIndex: 'userPhone'
		},
		{
			title: '是否代理商',
			dataIndex: 'userIsAccount'
		},
		{
			title: '总余额(元)',
			dataIndex: 'totalBalance'
		},
		{
			title: '可用余额(元)',
			dataIndex: 'availableBalance'
		},
		{
			title: '冻结余额(元)',
			dataIndex: 'frozenBalance'
		},
		{
			title: '累计佣金(元)',
			dataIndex: 'totalCommission'
		},
		{
			title: '累计提现(元)',
			dataIndex: 'totalWithdraw'
		},
		{
			title: '最后佣金时间',
			dataIndex: 'lastCommissionTime'
		},
		{
			title: '最后提现时间',
			dataIndex: 'lastWithdrawTime'
		},
		{
			title: '备注',
			dataIndex: 'remark'
		}
	]
	// 操作栏通过权限判断是否显示
	if (hasPerm(['wineUserAccountEdit', 'wineUserAccountDelete'])) {
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
		return wineUserAccountApi.wineUserAccountPage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteWineUserAccount = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		wineUserAccountApi.wineUserAccountDelete(params).then(() => {
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
			wineUserAccountApi.wineUserAccountExport(params).then((res) => {
				downloadUtil.resultDownload(res)
			})
		} else {
			wineUserAccountApi.wineUserAccountExport([]).then((res) => {
				downloadUtil.resultDownload(res)
			})
		}
	}
	// 批量删除
	const deleteBatchWineUserAccount = (params) => {
		wineUserAccountApi.wineUserAccountDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>
