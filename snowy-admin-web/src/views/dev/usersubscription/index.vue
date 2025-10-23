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
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('wechatUserSubscriptionAdd')">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
					<a-button @click="importModelRef.onOpen()" v-if="hasPerm('wechatUserSubscriptionImport')">
                        <template #icon><import-outlined /></template>
                        <span>导入</span>
                    </a-button>
                    <a-button @click="exportData" v-if="hasPerm('wechatUserSubscriptionExport')">
                        <template #icon><export-outlined /></template>
                        <span>导出</span>
                    </a-button>
					<xn-batch-button
						v-if="hasPerm('wechatUserSubscriptionBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchWechatUserSubscription"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)" v-if="hasPerm('wechatUserSubscriptionEdit')">编辑</a>
						<a-divider type="vertical" v-if="hasPerm(['wechatUserSubscriptionEdit', 'wechatUserSubscriptionDelete'], 'and')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteWechatUserSubscription(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('wechatUserSubscriptionDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<ImportModel ref="importModelRef" />
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="usersubscription">
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import ImportModel from './importModel.vue'
    import downloadUtil from '@/utils/downloadUtil'
	import wechatUserSubscriptionApi from '@/api/dev/wechatUserSubscriptionApi'
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
			title: '订阅状态(1已授权/0已取消)',
			dataIndex: 'subscriptionStatus'
		},
		{
			title: '授权时间',
			dataIndex: 'subscriptionTime'
		},
		{
			title: '过期时间',
			dataIndex: 'expireTime'
		},
		{
			title: '剩余发送次数',
			dataIndex: 'remainingTimes'
		},
		{
			title: '已发送次数',
			dataIndex: 'totalSent'
		},
		{
			title: '最后发送时间',
			dataIndex: 'lastSentTime'
		},
		{
			title: '扩展信息',
			dataIndex: 'extJson'
		},
	]
	// 操作栏通过权限判断是否显示
	if (hasPerm(['wechatUserSubscriptionEdit', 'wechatUserSubscriptionDelete'])) {
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
		return wechatUserSubscriptionApi.wechatUserSubscriptionPage(parameter).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteWechatUserSubscription = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		wechatUserSubscriptionApi.wechatUserSubscriptionDelete(params).then(() => {
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
            wechatUserSubscriptionApi.wechatUserSubscriptionExport(params).then((res) => {
                downloadUtil.resultDownload(res)
            })
        } else {
            wechatUserSubscriptionApi.wechatUserSubscriptionExport([]).then((res) => {
                downloadUtil.resultDownload(res)
            })
        }
    }
	// 批量删除
	const deleteBatchWechatUserSubscription = (params) => {
		wechatUserSubscriptionApi.wechatUserSubscriptionDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>
