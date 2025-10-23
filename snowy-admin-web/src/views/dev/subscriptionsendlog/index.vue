<template>
	<a-card :bordered="false">
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
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('wechatSubscriptionSendLogAdd')">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
					<a-button @click="importModelRef.onOpen()" v-if="hasPerm('wechatSubscriptionSendLogImport')">
                        <template #icon><import-outlined /></template>
                        <span>导入</span>
                    </a-button>
                    <a-button @click="exportData" v-if="hasPerm('wechatSubscriptionSendLogExport')">
                        <template #icon><export-outlined /></template>
                        <span>导出</span>
                    </a-button>
					<xn-batch-button
						v-if="hasPerm('wechatSubscriptionSendLogBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchWechatSubscriptionSendLog"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)" v-if="hasPerm('wechatSubscriptionSendLogEdit')">编辑</a>
						<a-divider type="vertical" v-if="hasPerm(['wechatSubscriptionSendLogEdit', 'wechatSubscriptionSendLogDelete'], 'and')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteWechatSubscriptionSendLog(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('wechatSubscriptionSendLogDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<ImportModel ref="importModelRef" />
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="subscriptionsendlog">
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import ImportModel from './importModel.vue'
    import downloadUtil from '@/utils/downloadUtil'
	import wechatSubscriptionSendLogApi from '@/api/dev/wechatSubscriptionSendLogApi'
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
			title: '微信openid',
			dataIndex: 'openid'
		},
		{
			title: '模板ID',
			dataIndex: 'templateId'
		},
		{
			title: '关联的站内信ID',
			dataIndex: 'messageId'
		},
		{
			title: '发送的数据内容JSON',
			dataIndex: 'sendData'
		},
		{
			title: '发送状态(1成功/0失败)',
			dataIndex: 'sendStatus'
		},
		{
			title: '微信返回的消息ID',
			dataIndex: 'wechatMsgId'
		},
		{
			title: '错误码',
			dataIndex: 'errorCode'
		},
		{
			title: '错误信息',
			dataIndex: 'errorMsg'
		},
		{
			title: '发送时间',
			dataIndex: 'sendTime'
		},
		{
			title: '微信接口返回数据',
			dataIndex: 'responseData'
		},
		{
			title: '业务类型',
			dataIndex: 'businessType'
		},
		{
			title: '业务ID',
			dataIndex: 'businessId'
		},
		{
			title: '扩展信息',
			dataIndex: 'extJson'
		},
	]
	// 操作栏通过权限判断是否显示
	if (hasPerm(['wechatSubscriptionSendLogEdit', 'wechatSubscriptionSendLogDelete'])) {
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
		return wechatSubscriptionSendLogApi.wechatSubscriptionSendLogPage(parameter).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteWechatSubscriptionSendLog = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		wechatSubscriptionSendLogApi.wechatSubscriptionSendLogDelete(params).then(() => {
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
            wechatSubscriptionSendLogApi.wechatSubscriptionSendLogExport(params).then((res) => {
                downloadUtil.resultDownload(res)
            })
        } else {
            wechatSubscriptionSendLogApi.wechatSubscriptionSendLogExport([]).then((res) => {
                downloadUtil.resultDownload(res)
            })
        }
    }
	// 批量删除
	const deleteBatchWechatSubscriptionSendLog = (params) => {
		wechatSubscriptionSendLogApi.wechatSubscriptionSendLogDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>
