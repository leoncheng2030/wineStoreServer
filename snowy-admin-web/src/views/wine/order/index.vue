<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="订单号" name="orderNo">
						<a-input v-model:value="searchFormState.orderNo" placeholder="请输入订单号" />
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
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('wineOrderAdd')">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
					<a-button @click="importModelRef.onOpen()" v-if="hasPerm('wineOrderImport')">
						<template #icon><import-outlined /></template>
						<span>导入</span>
					</a-button>
					<a-button @click="exportData" v-if="hasPerm('wineOrderExport')">
						<template #icon><export-outlined /></template>
						<span>导出</span>
					</a-button>
					<xn-batch-button
						v-if="hasPerm('wineOrderBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchWineOrder"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'status'">
					{{ $TOOL.dictTypeData('ORDER_STATUS', record.status) }}
				</template>
				<template v-if="column.dataIndex === 'profitSharingStatus'">
					<a-tag :color="getProfitSharingStatusColor(record.profitSharingStatus)">
						{{ getProfitSharingStatusText(record.profitSharingStatus) }}
					</a-tag>
				</template>
				<template v-if="column.dataIndex === 'profitSharingFailureReason'">
					<span
						v-if="record.profitSharingFailureReason"
						:title="record.profitSharingFailureReason"
						style="color: #ff4d4f; cursor: help"
					>
						{{ record.profitSharingFailureReason }}
					</span>
					<span v-else style="color: #999">-</span>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a
							@click="doManualProfitSharing(record)"
							v-if="canManualProfitSharing(record) && hasPerm('wineOrderManualProfitSharing')"
							>手动分账</a
						>
						<a-divider
							type="vertical"
							v-if="
								canManualProfitSharing(record) && hasPerm('wineOrderManualProfitSharing') && hasPerm('wineOrderEdit')
							"
						/>
						<a @click="formRef.onOpen(record)" v-if="hasPerm('wineOrderEdit')">编辑</a>
						<a-divider type="vertical" v-if="hasPerm(['wineOrderEdit', 'wineOrderDelete'], 'and')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteWineOrder(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('wineOrderDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<ImportModel ref="importModelRef" />
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="order">
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import ImportModel from './importModel.vue'
	import downloadUtil from '@/utils/downloadUtil'
	import wineOrderApi from '@/api/wine/wineOrderApi'
	import payApi from '@/api/pay/payApi'
	import { message as $message } from 'ant-design-vue'
	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const importModelRef = ref()
	const formRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	const columns = [
		{
			title: '订单号',
			dataIndex: 'orderNo',
			width: 110
		},
		{
			title: '用户昵称',
			dataIndex: 'clientName'
		},

		{
			title: '门店',
			dataIndex: 'storeName',
			width: 200
		},
		{
			title: '设备编号',
			dataIndex: 'deviceCode',
			width: 80
		},
		{
			title: '酒品',
			dataIndex: 'productName'
		},
		{
			title: '出酒量(ml)',
			dataIndex: 'quantity',
			width: 100
		},
		{
			title: '单价(元/ml)',
			dataIndex: 'unitPrice',
			width: 100
		},
		{
			title: '总金额',
			dataIndex: 'totalAmount',
			width: 100
		},
		{
			title: '订单状态',
			dataIndex: 'status',
			width: 100
		},
		{
			title: '分账状态',
			dataIndex: 'profitSharingStatus',
			width: 100
		},
		{
			title: '分账失败原因',
			dataIndex: 'profitSharingFailureReason',
			width: 200,
			ellipsis: true
		},
		{
			title: '支付时间',
			dataIndex: 'payTime'
		},
		{
			title: '开始出酒时间',
			dataIndex: 'dispenseStartTime'
		},
		{
			title: '出酒完成时间',
			dataIndex: 'dispenseEndTime'
		},
		{
			title: '备注',
			dataIndex: 'remark',
			ellipsis: true
		}
	]
	// 操作栏通过权限判断是否显示
	if (hasPerm(['wineOrderEdit', 'wineOrderDelete'])) {
		columns.push({
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 200
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
		return wineOrderApi.wineOrderPage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}
	const doRefund = (record) => {
		payApi.doRefund({ outTradeNo: record.orderNo, refundAmount: record.totalAmount }).then(() => {
			tableRef.value.refresh(true)
		})
	}

	// 手动修复退款状态
	const fixRefundStatus = (record) => {
		$message.loading('正在修复退款状态...', 0)
		wineOrderApi
			.fixRefundStatus({ id: record.id })
			.then((res) => {
				$message.destroy()
				if (res) {
					$message.success(res || '退款状态修复成功')
					tableRef.value.refresh(true)
				} else {
					$message.error('退款状态修复失败')
				}
			})
			.catch((error) => {
				$message.destroy()
				$message.error('退款状态修复失败：' + (error.message || '未知错误'))
			})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteWineOrder = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		wineOrderApi.wineOrderDelete(params).then(() => {
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
			wineOrderApi.wineOrderExport(params).then((res) => {
				downloadUtil.resultDownload(res)
			})
		} else {
			wineOrderApi.wineOrderExport([]).then((res) => {
				downloadUtil.resultDownload(res)
			})
		}
	}
	// 批量删除
	const deleteBatchWineOrder = (params) => {
		wineOrderApi.wineOrderDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}

	// 手动分账
	const doManualProfitSharing = (record) => {
		$message.loading('正在执行分账...', 0)
		wineOrderApi
			.manualProfitSharing({ id: record.id })
			.then(() => {
				$message.destroy()
				$message.success('分账执行成功')
				tableRef.value.refresh(true)
			})
			.catch((error) => {
				$message.destroy()
				console.error('分账执行失败:', error)
				$message.error('分账执行失败：' + (error.message || '未知错误'))
			})
	}

	// 判断是否可以手动分账
	const canManualProfitSharing = (record) => {
		// 只有分账失败或分账异常的订单才能手动分账
		return record.profitSharingStatus === 'FAILED' || record.profitSharingStatus === 'ERROR'
	}

	// 获取分账状态文本
	const getProfitSharingStatusText = (status) => {
		const statusMap = {
			NO_NEED: '无需分账',
			PENDING: '待分账',
			PROCESSING: '分账中',
			SUCCESS: '分账成功',
			FAILED: '分账失败',
			ERROR: '分账异常'
		}
		return statusMap[status] || '未知状态'
	}

	// 获取分账状态颜色
	const getProfitSharingStatusColor = (status) => {
		const colorMap = {
			NO_NEED: 'default',
			PENDING: 'orange',
			PROCESSING: 'blue',
			SUCCESS: 'green',
			FAILED: 'red',
			ERROR: 'red'
		}
		return colorMap[status] || 'default'
	}
</script>
