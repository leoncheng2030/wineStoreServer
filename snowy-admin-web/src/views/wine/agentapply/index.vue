<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="关键词" name="searchKey">
						<a-input v-model:value="searchFormState.searchKey" placeholder="请输入姓名、手机号或身份证号" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="申请状态" name="status">
						<a-select v-model:value="searchFormState.status" placeholder="请选择申请状态" allow-clear>
							<a-select-option value="PENDING">待审核</a-select-option>
							<a-select-option value="APPROVED">已通过</a-select-option>
							<a-select-option value="REJECTED">已拒绝</a-select-option>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="申请时间" name="timeRange">
						<a-range-picker
							v-model:value="searchFormState.timeRange"
							:placeholder="['开始时间', '结束时间']"
							format="YYYY-MM-DD"
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
					<a-button @click="exportData" v-if="hasPerm('wineAgentApplyExport')">
						<template #icon><export-outlined /></template>
						<span>导出</span>
					</a-button>
					<xn-batch-button
						v-if="hasPerm('wineAgentApplyBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchWineAgentApply"
					/>
					<a-button
						type="primary"
						@click="batchAudit('APPROVED')"
						v-if="hasPerm('wineAgentApplyAudit') && selectedRowKeys.length > 0"
					>
						<template #icon><check-outlined /></template>
						批量通过
					</a-button>
					<a-button
						danger
						@click="batchAudit('REJECTED')"
						v-if="hasPerm('wineAgentApplyAudit') && selectedRowKeys.length > 0"
					>
						<template #icon><close-outlined /></template>
						批量拒绝
					</a-button>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'status'">
					<a-tag v-if="record.status === 'PENDING'" color="orange">待审核</a-tag>
					<a-tag v-else-if="record.status === 'APPROVED'" color="green">已通过</a-tag>
					<a-tag v-else-if="record.status === 'REJECTED'" color="red">已拒绝</a-tag>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="viewDetail(record)">查看详情</a>
						<a-divider type="vertical" v-if="record.status === 'PENDING' && hasPerm('wineAgentApplyAudit')" />
						<a
							@click="auditRecord(record, 'APPROVED')"
							v-if="record.status === 'PENDING' && hasPerm('wineAgentApplyAudit')"
							>通过</a
						>
						<a-divider type="vertical" v-if="record.status === 'PENDING' && hasPerm('wineAgentApplyAudit')" />
						<a
							@click="auditRecord(record, 'REJECTED')"
							v-if="record.status === 'PENDING' && hasPerm('wineAgentApplyAudit')"
							>拒绝</a
						>
						<a-divider type="vertical" v-if="hasPerm('wineAgentApplyDelete')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteWineAgentApply(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('wineAgentApplyDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>

	<!-- 详情对话框 -->
	<a-modal v-model:open="detailVisible" title="申请详情" :footer="null" width="800px">
		<a-descriptions :column="2" bordered v-if="currentRecord">
			<a-descriptions-item label="申请用户">{{ currentRecord.clientUserId }}</a-descriptions-item>
			<a-descriptions-item label="真实姓名">{{ currentRecord.realName }}</a-descriptions-item>
			<a-descriptions-item label="手机号码">{{ currentRecord.phone }}</a-descriptions-item>
			<a-descriptions-item label="身份证号">{{ currentRecord.idCard }}</a-descriptions-item>
			<a-descriptions-item label="所在地区" :span="2">{{ formatArea(currentRecord) }}</a-descriptions-item>
			<a-descriptions-item label="详细地址" :span="2">{{ currentRecord.address }}</a-descriptions-item>
			<a-descriptions-item label="申请时间" :span="2">{{ currentRecord.createTime }}</a-descriptions-item>
			<a-descriptions-item label="申请状态">
				<a-tag v-if="currentRecord.status === 'PENDING'" color="orange">待审核</a-tag>
				<a-tag v-else-if="currentRecord.status === 'APPROVED'" color="green">已通过</a-tag>
				<a-tag v-else-if="currentRecord.status === 'REJECTED'" color="red">已拒绝</a-tag>
			</a-descriptions-item>
			<a-descriptions-item label="审核时间" v-if="currentRecord.auditTime">{{
				currentRecord.auditTime
			}}</a-descriptions-item>
			<a-descriptions-item label="审核人员" v-if="currentRecord.auditUser">{{
				currentRecord.auditUser
			}}</a-descriptions-item>
			<a-descriptions-item label="审核意见" :span="2" v-if="currentRecord.auditRemark">{{
				currentRecord.auditRemark
			}}</a-descriptions-item>
		</a-descriptions>
	</a-modal>

	<!-- 审核对话框 -->
	<a-modal
		v-model:open="auditVisible"
		:title="auditType === 'APPROVED' ? '审核通过' : '审核拒绝'"
		@ok="confirmAudit"
		@cancel="auditVisible = false"
	>
		<a-form :model="auditForm" layout="vertical" :rules="auditFormRef">
			<a-form-item label="子商户ID" v-if="auditType === 'APPROVED'" name="subMerId">
				<a-input v-model:value="auditForm.subMerId" placeholder="请输入子商户ID" />
			</a-form-item>
			<a-form-item label="审核意见" name="auditRemark">
				<a-textarea
					v-model:value="auditForm.auditRemark"
					:placeholder="auditType === 'APPROVED' ? '请输入通过原因（可选）' : '请输入拒绝原因'"
					:rows="4"
				/>
			</a-form-item>
		</a-form>
	</a-modal>
</template>

<script setup name="agentApply">
	import { cloneDeep } from 'lodash-es'
	import { Modal } from 'ant-design-vue'
	import downloadUtil from '@/utils/downloadUtil'
	import wineAgentApplyApi from '@/api/wine/wineAgentApplyApi'
	import { required } from '@/utils/formRules'

	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }

	// 详情对话框
	const detailVisible = ref(false)
	const currentRecord = ref(null)

	// 审核对话框
	const auditVisible = ref(false)
	const auditType = ref('')
	const auditForm = ref({
		auditRemark: ''
	})
	const auditRecordId = ref('')

	const columns = [
		{
			title: '申请用户',
			dataIndex: 'clientUserId',
			width: 120
		},
		{
			title: '真实姓名',
			dataIndex: 'realName',
			width: 100
		},
		{
			title: '手机号码',
			dataIndex: 'phone',
			width: 120
		},
		{
			title: '身份证号',
			dataIndex: 'idCard',
			width: 180
		},
		{
			title: '所在地区',
			dataIndex: 'area',
			width: 200,
			customRender: ({ record }) => {
				return formatArea(record)
			}
		},
		{
			title: '申请状态',
			dataIndex: 'status',
			width: 100
		},
		{
			title: '申请时间',
			dataIndex: 'createTime',
			width: 160
		},
		{
			title: '审核时间',
			dataIndex: 'auditTime',
			width: 160
		}
	]

	// 操作栏通过权限判断是否显示
	if (hasPerm(['wineAgentApplyAudit', 'wineAgentApplyDelete'])) {
		columns.push({
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 200,
			fixed: 'right'
		})
	}

	const selectedRowKeys = ref([])
	// 列表选择配置
	const options = {
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

		// 处理时间范围
		if (searchFormParam.timeRange && searchFormParam.timeRange.length === 2) {
			searchFormParam.startTime = searchFormParam.timeRange[0].format('YYYY-MM-DD 00:00:00')
			searchFormParam.endTime = searchFormParam.timeRange[1].format('YYYY-MM-DD 23:59:59')
			delete searchFormParam.timeRange
		}

		return wineAgentApplyApi.wineAgentApplyPage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}

	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}

	// 格式化地区显示
	const formatArea = (record) => {
		if (!record.provinceName && !record.cityName && !record.countyName) {
			return '-'
		}
		return `${record.provinceName || ''} ${record.cityName || ''} ${record.countyName || ''}`.trim()
	}

	// 查看详情
	const viewDetail = (record) => {
		currentRecord.value = record
		detailVisible.value = true
	}

	// 审核记录
	const auditRecord = (record, type) => {
		auditRecordId.value = record.id
		auditType.value = type
		auditForm.value.auditRemark = ''
		auditVisible.value = true
	}

	const auditFormRef = {
		subMerId: [required('子商户ID不能为空')]
	}

	// 确认审核
	const confirmAudit = () => {
		if (auditType.value === 'REJECTED' && !auditForm.value.auditRemark) {
			Modal.warning({
				title: '提示',
				content: '拒绝申请时必须填写拒绝原因'
			})
			return
		}

		const params = {
			id: auditRecordId.value,
			status: auditType.value,
			subMerId: auditForm.value.subMerId,
			auditRemark: auditForm.value.auditRemark
		}

		wineAgentApplyApi.wineAgentApplyAudit(params).then(() => {
			auditVisible.value = false
			tableRef.value.refresh(true)
			Modal.success({
				title: '成功',
				content: `审核${auditType.value === 'APPROVED' ? '通过' : '拒绝'}成功`
			})
		})
	}

	// 批量审核
	const batchAudit = (type) => {
		if (selectedRowKeys.value.length === 0) {
			Modal.warning({
				title: '提示',
				content: '请选择要审核的申请记录'
			})
			return
		}

		Modal.confirm({
			title: '确认',
			content: `确定要批量${type === 'APPROVED' ? '通过' : '拒绝'}选中的申请吗？`,
			onOk: () => {
				const auditPromises = selectedRowKeys.value.map((id) => {
					return wineAgentApplyApi.wineAgentApplyAudit({
						id: id,
						status: type,
						subMerId: auditForm.value.subMerId,
						auditRemark: type === 'APPROVED' ? '批量审核通过' : '批量审核拒绝'
					})
				})

				Promise.all(auditPromises)
					.then(() => {
						tableRef.value.clearRefreshSelected()
						Modal.success({
							title: '成功',
							content: `批量审核${type === 'APPROVED' ? '通过' : '拒绝'}成功`
						})
					})
					.catch(() => {
						Modal.error({
							title: '错误',
							content: '批量审核失败，请重试'
						})
					})
			}
		})
	}

	// 删除
	const deleteWineAgentApply = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		wineAgentApplyApi.wineAgentApplyDelete(params).then(() => {
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
			wineAgentApplyApi.wineAgentApplyExport(params).then((res) => {
				downloadUtil.resultDownload(res)
			})
		} else {
			wineAgentApplyApi.wineAgentApplyExport([]).then((res) => {
				downloadUtil.resultDownload(res)
			})
		}
	}

	// 批量删除
	const deleteBatchWineAgentApply = (params) => {
		wineAgentApplyApi.wineAgentApplyDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>

<style scoped>
	.ant-descriptions-item-label {
		width: 120px;
	}
</style>
