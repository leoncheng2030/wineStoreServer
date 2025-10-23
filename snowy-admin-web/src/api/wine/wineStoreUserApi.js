import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/wine/storeuser/` + url, ...arg)

/**
 * 店员表Api接口管理器
 *
 * @author jetox
 * @date  2025/08/20 19:08
 **/
export default {
	// 获取店员表分页
	wineStoreUserPage(data) {
		return request('page', data, 'get')
	},
	// 提交店员表表单 edit为true时为编辑，默认为新增
	wineStoreUserSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除店员表
	wineStoreUserDelete(data) {
		return request('delete', data)
	},
	// 获取店员表详情
	wineStoreUserDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载店员表导入模板
    wineStoreUserDownloadTemplate(data) {
        return request('downloadImportTemplate', data, 'get', {
            responseType: 'blob'
        })
    },
    // 导入店员表
    wineStoreUserImport(data) {
        return request('importData', data)
    },
    // 导出店员表
    wineStoreUserExport(data) {
        return request('exportData', data, 'post', {
            responseType: 'blob'
        })
    }
}
