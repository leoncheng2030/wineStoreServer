import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/wine/device/` + url, ...arg)

/**
 * 设备信息表Api接口管理器
 *
 * @author jetox
 * @date  2025/07/24 07:57
 **/
export default {
	// 获取设备信息表分页
	wineDevicePage(data) {
		return request('page', data, 'get')
	},
	// 提交设备信息表表单 edit为true时为编辑，默认为新增
	wineDeviceSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除设备信息表
	wineDeviceDelete(data) {
		return request('delete', data)
	},
	// 获取设备信息表详情
	wineDeviceDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载设备信息表导入模板
    wineDeviceDownloadTemplate(data) {
        return request('downloadImportTemplate', data, 'get', {
            responseType: 'blob'
        })
    },
    // 导入设备信息表
    wineDeviceImport(data) {
        return request('importData', data)
    },
    // 导出设备信息表
    wineDeviceExport(data) {
        return request('exportData', data, 'post', {
            responseType: 'blob'
        })
    },
    // 批量更新所有设备的脉冲数
    updateAllDevicesPulseRatio(pulseRatio) {
        return request('updateAllPulseRatio', { pulseRatio: pulseRatio }, 'post')
    }
}
