<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="订单ID" name="orderId">
						<a-input v-model:value="searchFormState.orderId" placeholder="请输入订单ID" />
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
					<a-button @click="exportData" v-if="hasPerm('wineCommissionRecordExport')">
						<template #icon><export-outlined /></template>
						<span>导出</span>
					</a-button>
					<xn-batch-button
						v-if="hasPerm('wineCommissionRecordBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchWineCommissionRecord"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'commissionType'">
					{{ $TOOL.dictTypeData('COMMISSION_TYPE', record.commissionType) }}
				</template>
				<template v-if="column.dataIndex === 'status'">
					{{ getCommissionStatusText(record.status) }}
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a-popconfirm title="确定要删除吗？" @confirm="deleteWineCommissionRecord(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('wineCommissionRecordDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<ImportModel ref="importModelRef" />
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="commissionrecord">
	import { cloneDeep } from 'lodash-es'
	import { ref, onMounted } from 'vue'
	import Form from './form.vue'
	import ImportModel from './importModel.vue'
	import downloadUtil from '@/utils/downloadUtil'
	import wineCommissionRecordApi from '@/api/wine/wineCommissionRecordApi'
	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const importModelRef = ref()
	const formRef = ref()
	const isPartnerMode = ref(false)
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	
	// 获取支付模式配置
	const getPaymentMode = () => {
		wineCommissionRecordApi.wineCommissionRecordPaymentMode().then((data) => {
			if (data) {
				isPartnerMode.value = data.isPartnerMode
			}
		})
	}
	
	// 获取佣金状态文本（根据支付模式智能显示）
	const getCommissionStatusText = (status) => {
		const statusMap = {
			'PENDING': '待计算',
			'CALCULATED': '已计算',
			'WAIT_PROFIT_SHARING': '待分账',
			'PROFIT_SHARING': '分账中',
			'PROFIT_SHARED': '分账成功',
			'PROFIT_FAILED': '分账失败',
			'SETTLED': '已发放',
			'FROZEN': '已冻结',
			'CANCELLED': '已取消'
		}
		
		// 如果是普通模式且状态为PROFIT_SHARED，显示为已发放
		if (!isPartnerMode.value && status === 'PROFIT_SHARED') {
			return '已发放'
		}
		
		return statusMap[status] || status
	}
	const columns = [
		{
			title: '订单号',
			dataIndex: 'orderNo'
		},
		{
			title: '用户昵称',
			dataIndex: 'userNickname'
		},
		{
			title: '设备编码',
			dataIndex: 'deviceId'
		},
		{
			title: '酒品名称',
			dataIndex: 'wineName'
		},
		{
			title: '订单金额(元)',
			dataIndex: 'orderAmount'
		},
		{
			title: '佣金类型',
			dataIndex: 'commissionType'
		},
		{
			title: '佣金比例(%)',
			dataIndex: 'commissionRate'
		},
		{
			title: '佣金金额(元)',
			dataIndex: 'commissionAmount'
		},
		{
			title: '状态',
			dataIndex: 'status'
		},
		{
			title: '计算时间',
			dataIndex: 'calculateTime'
		},
		{
			title: '发放时间',
			dataIndex: 'settleTime'
		},
		{
			title: '备注',
			dataIndex: 'remark'
		},
		{
			title: '扩展信息',
			dataIndex: 'extJson'
		}
	]
	// 操作栏通过权限判断是否显示
	if (hasPerm(['wineCommissionRecordEdit', 'wineCommissionRecordDelete'])) {
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
		return wineCommissionRecordApi.wineCommissionRecordPage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteWineCommissionRecord = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		wineCommissionRecordApi.wineCommissionRecordDelete(params).then(() => {
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
			wineCommissionRecordApi.wineCommissionRecordExport(params).then((res) => {
				downloadUtil.resultDownload(res)
			})
		} else {
			wineCommissionRecordApi.wineCommissionRecordExport([]).then((res) => {
				downloadUtil.resultDownload(res)
			})
		}
	}
	// 批量删除
	const deleteBatchWineCommissionRecord = (params) => {
		wineCommissionRecordApi.wineCommissionRecordDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	
	// 页面初始化
	onMounted(() => {
		getPaymentMode()
	})
</script>
