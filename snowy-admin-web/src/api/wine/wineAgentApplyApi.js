import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/wine/agentApply/` + url, ...arg)

/**
 * 代理商申请Api接口管理器
 *
 * @author Qoder
 * @date  2024/09/20
 **/
export default {
	// 获取代理商申请分页
	wineAgentApplyPage(data) {
		return request('page', data, 'get')
	},
	// 获取代理商申请列表
	wineAgentApplyList(data) {
		return request('list', data, 'get')
	},
	// 获取代理商申请详情
	wineAgentApplyDetail(data) {
		return request('detail', data, 'get')
	},
	// 删除代理商申请
	wineAgentApplyDelete(data) {
		return request('delete', data)
	},
	// 审核代理商申请
	wineAgentApplyAudit(data) {
		return request('audit', data)
	},
	// 获取申请统计
	wineAgentApplyStatistics(data) {
		return request('statistics', data, 'get')
	},
	// 导出代理商申请
	wineAgentApplyExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	}
}