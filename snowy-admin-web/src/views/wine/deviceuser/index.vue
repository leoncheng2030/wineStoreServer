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
					<a-form-item label="设备ID" name="deviceId">
						<a-input v-model:value="searchFormState.deviceId" placeholder="请输入设备ID" />
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
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('wineDeviceUserAdd')">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
					<a-button @click="importModelRef.onOpen()" v-if="hasPerm('wineDeviceUserImport')">
                        <template #icon><import-outlined /></template>
                        <span>导入</span>
                    </a-button>
                    <a-button @click="exportData" v-if="hasPerm('wineDeviceUserExport')">
                        <template #icon><export-outlined /></template>
                        <span>导出</span>
                    </a-button>
					<xn-batch-button
						v-if="hasPerm('wineDeviceUserBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchWineDeviceUser"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)" v-if="hasPerm('wineDeviceUserEdit')">编辑</a>
						<a-divider type="vertical" v-if="hasPerm(['wineDeviceUserEdit', 'wineDeviceUserDelete'], 'and')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteWineDeviceUser(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('wineDeviceUserDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<ImportModel ref="importModelRef" />
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="deviceuser">
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import ImportModel from './importModel.vue'
    import downloadUtil from '@/utils/downloadUtil'
	import wineDeviceUserApi from '@/api/wine/wineDeviceUserApi'
	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const importModelRef = ref()
	const formRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	const columns = [
		{
			title: '用户ID',
			dataIndex: 'userId'
		},
		{
			title: '设备ID',
			dataIndex: 'deviceId'
		},
		{
			title: '佣金比例',
			dataIndex: 'commissionRate'
		},
		{
			title: '备注',
			dataIndex: 'remark'
		},
	]
	// 操作栏通过权限判断是否显示
	if (hasPerm(['wineDeviceUserEdit', 'wineDeviceUserDelete'])) {
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
		return wineDeviceUserApi.wineDeviceUserPage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteWineDeviceUser = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		wineDeviceUserApi.wineDeviceUserDelete(params).then(() => {
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
            wineDeviceUserApi.wineDeviceUserExport(params).then((res) => {
                downloadUtil.resultDownload(res)
            })
        } else {
            wineDeviceUserApi.wineDeviceUserExport([]).then((res) => {
                downloadUtil.resultDownload(res)
            })
        }
    }
	// 批量删除
	const deleteBatchWineDeviceUser = (params) => {
		wineDeviceUserApi.wineDeviceUserDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>
