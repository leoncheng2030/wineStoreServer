import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/wine/product/` + url, ...arg)

/**
 * 酒品列表Api接口管理器
 *
 * @author jetox
 * @date  2025/07/24 08:20
 **/
export default {
	// 获取酒品列表分页
	wineProductPage(data) {
		return request('page', data, 'get')
	},
	// 获取酒品列表列表
	wineProductList(data) {
		return request('list', data, 'get')
	},
	// 获取酒品列表ID列表
	wineProductIdList(data) {
		return request('idList', data)
	},
	// 提交酒品列表表单 edit为true时为编辑，默认为新增
	wineProductSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除酒品列表
	wineProductDelete(data) {
		return request('delete', data)
	},
	// 获取酒品列表详情
	wineProductDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载酒品列表导入模板
	wineProductDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
	},
	// 导入酒品列表
	wineProductImport(data) {
		return request('importData', data)
	},
	// 导出酒品列表
	wineProductExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	}
}
