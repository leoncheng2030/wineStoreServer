import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/wine/agent/` + url, ...arg)

/**
 * wine_agentApi接口管理器
 *
 * @author jetox
 * @date  2025/09/19 07:13
 **/
export default {
	// 获取wine_agent分页
	wineAgentPage(data) {
		return request('page', data, 'get')
	},
	// 提交wine_agent表单 edit为true时为编辑，默认为新增
	wineAgentSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除wine_agent
	wineAgentDelete(data) {
		return request('delete', data)
	},
	// 获取wine_agent详情
	wineAgentDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载wine_agent导入模板
    wineAgentDownloadTemplate(data) {
        return request('downloadImportTemplate', data, 'get', {
            responseType: 'blob'
        })
    },
    // 导入wine_agent
    wineAgentImport(data) {
        return request('importData', data)
    },
    // 导出wine_agent
    wineAgentExport(data) {
        return request('exportData', data, 'post', {
            responseType: 'blob'
        })
    },
    // 根据用户ID获取代理商详情
    wineAgentDetailByUserId(data) {
        return request('detailByUserId', data, 'get')
    },
    // 根据用户ID获取代理商详情（包含申请信息）
    wineAgentDetailWithApplyInfo(data) {
        return request('getAgentDetailWithApplyInfo', data, 'get')
    }
}
