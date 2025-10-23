import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/wine/devicerole/` + url, ...arg)

/**
 * 设备角色定义表Api接口管理器
 *
 * @author jetox
 * @date  2025/09/21 09:16
 **/
export default {
	// 获取设备角色定义表分页
	wineDeviceRolePage(data) {
		return request('page', data, 'get')
	},
	// 提交设备角色定义表表单 edit为true时为编辑，默认为新增
	wineDeviceRoleSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除设备角色定义表
	wineDeviceRoleDelete(data) {
		return request('delete', data)
	},
	// 获取设备角色定义表详情
	wineDeviceRoleDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载设备角色定义表导入模板
    wineDeviceRoleDownloadTemplate(data) {
        return request('downloadImportTemplate', data, 'get', {
            responseType: 'blob'
        })
    },
    // 导入设备角色定义表
    wineDeviceRoleImport(data) {
        return request('importData', data)
    },
    // 导出设备角色定义表
    wineDeviceRoleExport(data) {
        return request('exportData', data, 'post', {
            responseType: 'blob'
        })
    }
}
