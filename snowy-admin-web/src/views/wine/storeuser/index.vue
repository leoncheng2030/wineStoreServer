<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="门店ID" name="storeId">
						<a-input v-model:value="searchFormState.storeId" placeholder="请输入门店ID" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="用户ID" name="clientUserId">
						<a-input v-model:value="searchFormState.clientUserId" placeholder="请输入用户ID" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="用户昵称" name="clientUserName">
						<a-input v-model:value="searchFormState.clientUserName" placeholder="请输入用户昵称" />
					</a-form-item>
				</a-col>
				<a-col :span="6" v-show="advanced">
					<a-form-item label="用户手机号" name="clientUserPhone">
						<a-input v-model:value="searchFormState.clientUserPhone" placeholder="请输入用户手机号" />
					</a-form-item>
				</a-col>
				<a-col :span="6" v-show="advanced">
					<a-form-item label="状态" name="status">
						<a-select v-model:value="searchFormState.status" placeholder="请选择状态" :options="statusOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-button type="primary" @click="tableRef.refresh()">查询</a-button>
					<a-button style="margin: 0 8px" @click="reset">重置</a-button>
					<a @click="toggleAdvanced" style="margin-left: 8px">
						{{ advanced ? '收起' : '展开' }}
						<component :is="advanced ? 'up-outlined' : 'down-outlined'"/>
					</a>
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
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('wineStoreUserAdd')">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
					<a-button @click="importModelRef.onOpen()" v-if="hasPerm('wineStoreUserImport')">
                        <template #icon><import-outlined /></template>
                        <span>导入</span>
                    </a-button>
                    <a-button @click="exportData" v-if="hasPerm('wineStoreUserExport')">
                        <template #icon><export-outlined /></template>
                        <span>导出</span>
                    </a-button>
					<xn-batch-button
						v-if="hasPerm('wineStoreUserBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchWineStoreUser"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'status'">
					{{ $TOOL.dictTypeData('COMMON_STATUS', record.status) }}
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)" v-if="hasPerm('wineStoreUserEdit')">编辑</a>
						<a-divider type="vertical" v-if="hasPerm(['wineStoreUserEdit', 'wineStoreUserDelete'], 'and')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteWineStoreUser(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('wineStoreUserDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<ImportModel ref="importModelRef" />
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="storeuser">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import ImportModel from './importModel.vue'
    import downloadUtil from '@/utils/downloadUtil'
	import wineStoreUserApi from '@/api/wine/wineStoreUserApi'
	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const importModelRef = ref()
	const formRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	// 查询区域显示更多控制
	const advanced = ref(false)
	const toggleAdvanced = () => {
		advanced.value = !advanced.value
	}
	const columns = [
		{
			title: '门店ID',
			dataIndex: 'storeId'
		},
		{
			title: '用户ID',
			dataIndex: 'clientUserId'
		},
		{
			title: '用户昵称',
			dataIndex: 'clientUserName'
		},
		{
			title: '用户手机号',
			dataIndex: 'clientUserPhone'
		},
		{
			title: '状态',
			dataIndex: 'status'
		},
		{
			title: '排序码',
			dataIndex: 'sortCode'
		},
		{
			title: '备注',
			dataIndex: 'remark'
		},
	]
	// 操作栏通过权限判断是否显示
	if (hasPerm(['wineStoreUserEdit', 'wineStoreUserDelete'])) {
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
		return wineStoreUserApi.wineStoreUserPage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteWineStoreUser = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		wineStoreUserApi.wineStoreUserDelete(params).then(() => {
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
            wineStoreUserApi.wineStoreUserExport(params).then((res) => {
                downloadUtil.resultDownload(res)
            })
        } else {
            wineStoreUserApi.wineStoreUserExport([]).then((res) => {
                downloadUtil.resultDownload(res)
            })
        }
    }
	// 批量删除
	const deleteBatchWineStoreUser = (params) => {
		wineStoreUserApi.wineStoreUserDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	const statusOptions = tool.dictList('COMMON_STATUS')
</script>
