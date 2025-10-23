<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="角色名称" name="roleName">
						<a-input v-model:value="searchFormState.roleName" placeholder="请输入角色名称" />
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
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('wineDeviceRoleAdd')">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
					<a-button @click="importModelRef.onOpen()" v-if="hasPerm('wineDeviceRoleImport')">
                        <template #icon><import-outlined /></template>
                        <span>导入</span>
                    </a-button>
                    <a-button @click="exportData" v-if="hasPerm('wineDeviceRoleExport')">
                        <template #icon><export-outlined /></template>
                        <span>导出</span>
                    </a-button>
					<xn-batch-button
						v-if="hasPerm('wineDeviceRoleBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchWineDeviceRole"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'status'">
					{{ $TOOL.dictTypeData('COMMON_STATUS', record.status) }}
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)" v-if="hasPerm('wineDeviceRoleEdit')">编辑</a>
						<a-divider type="vertical" v-if="hasPerm(['wineDeviceRoleEdit', 'wineDeviceRoleDelete'], 'and')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteWineDeviceRole(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('wineDeviceRoleDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<ImportModel ref="importModelRef" />
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="devicerole">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import ImportModel from './importModel.vue'
    import downloadUtil from '@/utils/downloadUtil'
	import wineDeviceRoleApi from '@/api/wine/wineDeviceRoleApi'
	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const importModelRef = ref()
	const formRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	const columns = [
		{
			title: '角色编码',
			dataIndex: 'roleCode'
		},
		{
			title: '角色名称',
			dataIndex: 'roleName'
		},
		{
			title: '角色描述',
			dataIndex: 'roleDescription'
		},
		// {
		// 	title: '权限列表（JSON格式存储权限代码数组）',
		// 	dataIndex: 'permissions'
		// },
		{
			title: '默认佣金比例（百分比）',
			dataIndex: 'defaultCommissionRate'
		},
		{
			title: '角色级别（数字越小级别越高）',
			dataIndex: 'roleLevel'
		},
		{
			title: '状态（ENABLE-启用, DISABLE-禁用）',
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
		{
			title: '扩展信息',
			dataIndex: 'extJson'
		},
	]
	// 操作栏通过权限判断是否显示
	if (hasPerm(['wineDeviceRoleEdit', 'wineDeviceRoleDelete'])) {
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
		return wineDeviceRoleApi.wineDeviceRolePage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteWineDeviceRole = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		wineDeviceRoleApi.wineDeviceRoleDelete(params).then(() => {
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
            wineDeviceRoleApi.wineDeviceRoleExport(params).then((res) => {
                downloadUtil.resultDownload(res)
            })
        } else {
            wineDeviceRoleApi.wineDeviceRoleExport([]).then((res) => {
                downloadUtil.resultDownload(res)
            })
        }
    }
	// 批量删除
	const deleteBatchWineDeviceRole = (params) => {
		wineDeviceRoleApi.wineDeviceRoleDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>
