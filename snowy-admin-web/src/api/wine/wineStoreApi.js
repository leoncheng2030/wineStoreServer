import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/wine/store/` + url, ...arg)

/**
 * 门店管理表Api接口管理器
 *
 * @author jetox
 * @date  2025/07/24 08:01
 **/
export default {
	// 获取门店管理表分页
	wineStorePage(data) {
		return request('page', data, 'get')
	},
	// 获取门店管理表列表
	wineStoreList(data) {
		return request('list', data, 'get')
	},
	// 获取门店管理表列表
	wineStoreIdList(data) {
		return request('idList', data)
	},
	// 提交门店管理表表单 edit为true时为编辑，默认为新增
	wineStoreSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除门店管理表
	wineStoreDelete(data) {
		return request('delete', data)
	},
	// 获取门店管理表详情
	wineStoreDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载门店管理表导入模板
	wineStoreDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
	},
	// 导入门店管理表
	wineStoreImport(data) {
		return request('importData', data)
	},
	// 导出门店管理表
	wineStoreExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	}
}
