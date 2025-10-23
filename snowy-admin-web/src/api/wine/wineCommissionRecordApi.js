import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/wine/commissionrecord/` + url, ...arg)

/**
 * 佣金记录表Api接口管理器
 *
 * @author jetox
 * @date  2025/07/24 08:50
 **/
export default {
	// 获取佣金记录表分页
	wineCommissionRecordPage(data) {
		return request('page', data, 'get')
	},
	// 提交佣金记录表表单 edit为true时为编辑，默认为新增
	wineCommissionRecordSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除佣金记录表
	wineCommissionRecordDelete(data) {
		return request('delete', data)
	},
	// 获取佣金记录表详情
	wineCommissionRecordDetail(data) {
		return request('detail', data, 'get')
	},
	// 获取支付模式配置
	wineCommissionRecordPaymentMode() {
		return request('paymentMode', {}, 'get')
	},
	// 下载佣金记录表导入模板
    wineCommissionRecordDownloadTemplate(data) {
        return request('downloadImportTemplate', data, 'get', {
            responseType: 'blob'
        })
    },
    // 导入佣金记录表
    wineCommissionRecordImport(data) {
        return request('importData', data)
    },
    // 导出佣金记录表
    wineCommissionRecordExport(data) {
        return request('exportData', data, 'post', {
            responseType: 'blob'
        })
    }
}
