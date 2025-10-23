import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/dbs/database/` + url, ...arg)

/**
 * 数据源Api接口管理器
 *
 * @author jetox
 * @date  2025/07/28 00:35
 **/
export default {
	// 获取数据源分页
	extDatabasePage(data) {
		return request('page', data, 'get')
	},
	// 提交数据源表单 edit为true时为编辑，默认为新增
	extDatabaseSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除数据源
	extDatabaseDelete(data) {
		return request('delete', data)
	},
	// 获取数据源详情
	extDatabaseDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载数据源导入模板
    extDatabaseDownloadTemplate(data) {
        return request('downloadImportTemplate', data, 'get', {
            responseType: 'blob'
        })
    },
    // 导入数据源
    extDatabaseImport(data) {
        return request('importData', data)
    },
    // 导出数据源
    extDatabaseExport(data) {
        return request('exportData', data, 'post', {
            responseType: 'blob'
        })
    }
}
