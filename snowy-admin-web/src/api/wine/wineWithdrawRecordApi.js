import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/wine/withdrawrecord/` + url, ...arg)

/**
 * 提现记录表Api接口管理器
 *
 * @author jetox
 * @date  2025/07/24 08:50
 **/
export default {
	// 获取提现记录表分页
	wineWithdrawRecordPage(data) {
		return request('page', data, 'get')
	},
	// 提交提现记录表表单 edit为true时为编辑，默认为新增
	wineWithdrawRecordSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除提现记录表
	wineWithdrawRecordDelete(data) {
		return request('delete', data)
	},
	// 获取提现记录表详情
	wineWithdrawRecordDetail(data) {
		return request('detail', data, 'get')
	},
	// 提现审核
	wineWithdrawRecordAudit(data) {
		return request('approveWithdraw', data)
	},
	// 拒绝提现
	wineWithdrawRecordReject(data) {
		return request('rejectWithdraw', data)
	},
	// 下载提现记录表导入模板
	wineWithdrawRecordDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
	},
	// 导入提现记录表
	wineWithdrawRecordImport(data) {
		return request('importData', data)
	},
	// 导出提现记录表
	wineWithdrawRecordExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	}
}
