import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/wine/spec/` + url, ...arg)

/**
 * 规格Api接口管理器
 *
 * @author jetox
 * @date  2025/09/28 19:06
 **/
export default {
	// 获取规格分页
	wineSpecPage(data) {
		return request('page', data, 'get')
	},
	// 提交规格表单 edit为true时为编辑，默认为新增
	wineSpecSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除规格
	wineSpecDelete(data) {
		return request('delete', data)
	},
	// 获取规格详情
	wineSpecDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载规格导入模板
    wineSpecDownloadTemplate(data) {
        return request('downloadImportTemplate', data, 'get', {
            responseType: 'blob'
        })
    },
    // 导入规格
    wineSpecImport(data) {
        return request('importData', data)
    },
    // 导出规格
    wineSpecExport(data) {
        return request('exportData', data, 'post', {
            responseType: 'blob'
        })
    }
}
