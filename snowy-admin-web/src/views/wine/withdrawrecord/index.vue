<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="提现单号" name="withdrawNo">
						<a-input v-model:value="searchFormState.withdrawNo" placeholder="请输入提现单号" />
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
					<xn-batch-button
						v-if="hasPerm('wineWithdrawRecordBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchWineWithdrawRecord"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'status'">
					{{ $TOOL.dictTypeData('WITHDRAW_STATUS', record.status) }}
				</template>
				<template v-if="column.dataIndex === 'withdrawType'">
					{{ $TOOL.dictTypeData('WITHDRAW_TYPE', record.withdrawType) }}
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a-button
							type="link"
							size="small"
							@click="showAuditModal(record)"
							v-if="record.status === 'WAIT' && hasPerm('wineWithdrawRecordApprove')"
							>审核</a-button
						>
						<a-divider type="vertical" v-if="record.status === 'WAIT' && hasPerm('wineWithdrawRecordApprove')" />
						<a
							type="link"
							size="small"
							@click="showRejectModal(record)"
							v-if="record.status === 'WAIT' && hasPerm('wineWithdrawRecordReject')"
							>拒绝</a
						>
						<a-divider type="vertical" v-if="record.status === 'WAIT' && hasPerm('wineWithdrawRecordReject')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteWineWithdrawRecord(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('wineWithdrawRecordDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<ImportModel ref="importModelRef" />
	<Form ref="formRef" @successful="tableRef.refresh()" />

	<!-- 审核模态框 -->
	<a-modal v-model:open="auditModalVisible" title="提现审核" @ok="handleAudit" :confirm-loading="auditConfirmLoading">
		<a-form ref="auditFormRef" :model="auditFormState" layout="vertical">
			<a-form-item label="提现单号">
				{{ auditFormState.withdrawNo }}
			</a-form-item>
			<a-form-item label="用户昵称">
				{{ auditFormState.userNickname }}
			</a-form-item>
			<a-form-item label="提现金额"> {{ auditFormState.withdrawAmount }} 元 </a-form-item>
			<a-form-item label="手续费"> {{ auditFormState.serviceFee }} 元 </a-form-item>
			<a-form-item label="实际到账金额"> {{ auditFormState.actualAmount }} 元 </a-form-item>
			<a-form-item label="确认审核通过吗？"> </a-form-item>
		</a-form>
	</a-modal>

	<!-- 拒绝模态框 -->
	<a-modal
		v-model:open="rejectModalVisible"
		title="拒绝提现申请"
		@ok="handleReject"
		:confirm-loading="rejectConfirmLoading"
	>
		<a-form ref="rejectFormRef" :model="rejectFormState" layout="vertical">
			<a-form-item label="提现单号">
				{{ rejectFormState.withdrawNo }}
			</a-form-item>
			<a-form-item label="用户昵称">
				{{ rejectFormState.userNickname }}
			</a-form-item>
			<a-form-item label="提现金额"> {{ rejectFormState.withdrawAmount }} 元 </a-form-item>
			<a-form-item label="手续费"> {{ rejectFormState.serviceFee }} 元 </a-form-item>
			<a-form-item label="实际到账金额"> {{ rejectFormState.actualAmount }} 元 </a-form-item>
			<a-form-item label="拒绝原因" required>
				<a-textarea v-model:value="rejectFormState.failReason" placeholder="请输入拒绝原因" :rows="3" />
			</a-form-item>
		</a-form>
	</a-modal>
</template>

<script setup name="withdrawrecord">
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import ImportModel from './importModel.vue'
	import downloadUtil from '@/utils/downloadUtil'
	import wineWithdrawRecordApi from '@/api/wine/wineWithdrawRecordApi'
	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const importModelRef = ref()
	const formRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }

	// 审核模态框相关
	const auditModalVisible = ref(false)
	const auditConfirmLoading = ref(false)
	const auditFormRef = ref()
	const auditFormState = ref({})

	// 拒绝模态框相关
	const rejectModalVisible = ref(false)
	const rejectConfirmLoading = ref(false)
	const rejectFormRef = ref()
	const rejectFormState = ref({})

	const columns = [
		{
			title: '提现单号',
			dataIndex: 'withdrawNo'
		},
		{
			title: '用户昵称',
			dataIndex: 'userNickname'
		},
		{
			title: '提现金额(元)',
			dataIndex: 'withdrawAmount'
		},
		{
			title: '手续费(元)',
			dataIndex: 'serviceFee'
		},
		{
			title: '实际到账金额(元)',
			dataIndex: 'actualAmount'
		},
		{
			title: '提现方式',
			dataIndex: 'withdrawType'
		},
		{
			title: '账户信息',
			dataIndex: 'accountInfo'
		},
		{
			title: '提现状态',
			dataIndex: 'status'
		},
		{
			title: '申请时间',
			dataIndex: 'applyTime'
		},
		{
			title: '审核时间',
			dataIndex: 'approveTime'
		},
		{
			title: '处理时间',
			dataIndex: 'processTime'
		},
		{
			title: '完成时间',
			dataIndex: 'completeTime'
		},
		{
			title: '审核用户',
			dataIndex: 'approveUser'
		},
		{
			title: '处理用户',
			dataIndex: 'processUser'
		},
		{
			title: '失败原因',
			dataIndex: 'failReason'
		},
		{
			title: '第三方交易号',
			dataIndex: 'transactionId'
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
	if (
		hasPerm([
			'wineWithdrawRecordEdit',
			'wineWithdrawRecordDelete',
			'wineWithdrawRecordApprove',
			'wineWithdrawRecordReject'
		])
	) {
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
		return wineWithdrawRecordApi.wineWithdrawRecordPage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteWineWithdrawRecord = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		wineWithdrawRecordApi.wineWithdrawRecordDelete(params).then(() => {
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
			wineWithdrawRecordApi.wineWithdrawRecordExport(params).then((res) => {
				downloadUtil.resultDownload(res)
			})
		} else {
			wineWithdrawRecordApi.wineWithdrawRecordExport([]).then((res) => {
				downloadUtil.resultDownload(res)
			})
		}
	}
	// 批量删除
	const deleteBatchWineWithdrawRecord = (params) => {
		wineWithdrawRecordApi.wineWithdrawRecordDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}

	// 显示审核模态框
	const showAuditModal = (record) => {
		auditFormState.value = cloneDeep(record)
		auditModalVisible.value = true
	}

	// 处理审核
	const handleAudit = () => {
		auditConfirmLoading.value = true
		const param = {
			id: auditFormState.value.id
		}
		wineWithdrawRecordApi
			.wineWithdrawRecordAudit(param)
			.then(() => {
				auditModalVisible.value = false
				auditConfirmLoading.value = false
				tableRef.value.refresh(true)
				$message.success('审核成功')
			})
			.catch(() => {
				auditConfirmLoading.value = false
			})
	}

	// 显示拒绝模态框
	const showRejectModal = (record) => {
		rejectFormState.value = cloneDeep(record)
		rejectModalVisible.value = true
	}

	// 处理拒绝
	const handleReject = () => {
		if (!rejectFormState.value.failReason) {
			$message.warning('请输入拒绝原因')
			return
		}
		rejectConfirmLoading.value = true
		const param = {
			id: rejectFormState.value.id,
			failReason: rejectFormState.value.failReason
		}
		wineWithdrawRecordApi
			.wineWithdrawRecordReject(param)
			.then(() => {
				rejectModalVisible.value = false
				rejectConfirmLoading.value = false
				tableRef.value.refresh(true)
				$message.success('已拒绝该提现申请')
			})
			.catch(() => {
				rejectConfirmLoading.value = false
			})
	}
</script>
