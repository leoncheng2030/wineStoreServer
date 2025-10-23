import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/wine/accountflow/` + url, ...arg)

/**
 * 账户流水Api接口管理器
 *
 * @author jetox
 * @date  2025/07/24 08:56
 **/
export default {
	// 获取账户流水分页
	wineAccountFlowPage(data) {
		return request('page', data, 'get')
	},
	// 提交账户流水表单 edit为true时为编辑，默认为新增
	wineAccountFlowSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除账户流水
	wineAccountFlowDelete(data) {
		return request('delete', data)
	},
	// 获取账户流水详情
	wineAccountFlowDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载账户流水导入模板
    wineAccountFlowDownloadTemplate(data) {
        return request('downloadImportTemplate', data, 'get', {
            responseType: 'blob'
        })
    },
    // 导入账户流水
    wineAccountFlowImport(data) {
        return request('importData', data)
    },
    // 导出账户流水
    wineAccountFlowExport(data) {
        return request('exportData', data, 'post', {
            responseType: 'blob'
        })
    }
}
