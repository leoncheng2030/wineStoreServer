import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/wine/deviceuser/` + url, ...arg)

/**
 * 分佣配置Api接口管理器
 *
 * @author jetox
 * @date  2025/07/24 08:44
 **/
export default {
	// 获取分佣配置分页
	wineDeviceUserPage(data) {
		return request('page', data, 'get')
	},
	// 提交分佣配置表单 edit为true时为编辑，默认为新增
	wineDeviceUserSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除分佣配置
	wineDeviceUserDelete(data) {
		return request('delete', data)
	},
	// 获取分佣配置详情
	wineDeviceUserDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载分佣配置导入模板
    wineDeviceUserDownloadTemplate(data) {
        return request('downloadImportTemplate', data, 'get', {
            responseType: 'blob'
        })
    },
    // 导入分佣配置
    wineDeviceUserImport(data) {
        return request('importData', data)
    },
    // 导出分佣配置
    wineDeviceUserExport(data) {
        return request('exportData', data, 'post', {
            responseType: 'blob'
        })
    }
}
