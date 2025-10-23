<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="父级地区编码" name="parentId">
						<a-select v-model:value="searchFormState.parentId" placeholder="请选择父级地区编码" :options="parentIdOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="等级" name="level">
						<a-select v-model:value="searchFormState.level" placeholder="请选择等级" :options="levelOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="名称" name="name">
						<a-input v-model:value="searchFormState.name" placeholder="请输入名称" />
					</a-form-item>
				</a-col>
				<a-col :span="6" v-show="advanced">
					<a-form-item label="简称" name="shortName">
						<a-input v-model:value="searchFormState.shortName" placeholder="请输入简称" />
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
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('devDistrictAdd')">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
					<a-button @click="importModelRef.onOpen()" v-if="hasPerm('devDistrictImport')">
                        <template #icon><import-outlined /></template>
                        <span>导入</span>
                    </a-button>
                    <a-button @click="exportData" v-if="hasPerm('devDistrictExport')">
                        <template #icon><export-outlined /></template>
                        <span>导出</span>
                    </a-button>
					<xn-batch-button
						v-if="hasPerm('devDistrictBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchDevDistrict"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)" v-if="hasPerm('devDistrictEdit')">编辑</a>
						<a-divider type="vertical" v-if="hasPerm(['devDistrictEdit', 'devDistrictDelete'], 'and')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteDevDistrict(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('devDistrictDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<ImportModel ref="importModelRef" />
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="district">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import ImportModel from './importModel.vue'
	import downloadUtil from '@/utils/downloadUtil'
	import devDistrictApi from '@/api/dev/devDistrictApi'
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
			title: '名称',
			dataIndex: 'name'
		},
		{
			title: '简称',
			dataIndex: 'shortName'
		},
		{
			title: '地区编码',
			dataIndex: 'cityCode'
		},
		{
			title: '邮政编码',
			dataIndex: 'zipCode'
		},
		{
			title: '纬度',
			dataIndex: 'gcj02Lng'
		},
		{
			title: '经度',
			dataIndex: 'gcj02Lat'
		},
		{
			title: '纬度',
			dataIndex: 'db09Lng'
		},
		{
			title: '经度',
			dataIndex: 'db09Lat'
		},
		{
			title: 'REMARK1',
			dataIndex: 'remark1'
		},
		{
			title: 'REMARK2',
			dataIndex: 'remark2'
		},
	]
	// 操作栏通过权限判断是否显示
	if (hasPerm(['devDistrictEdit', 'devDistrictDelete'])) {
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
		return devDistrictApi.devDistrictPage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteDevDistrict = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		devDistrictApi.devDistrictDelete(params).then(() => {
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
            devDistrictApi.devDistrictExport(params).then((res) => {
                downloadUtil.resultDownload(res)
            })
        } else {
            devDistrictApi.devDistrictExport([]).then((res) => {
                downloadUtil.resultDownload(res)
            })
        }
    }
	// 批量删除
	const deleteBatchDevDistrict = (params) => {
		devDistrictApi.devDistrictDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	const parentIdOptions = tool.dictList('COMMON_STATUS')
	const levelOptions = tool.dictList('DISTRICT_LEVEL')
</script>
