import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/wine/order/` + url, ...arg)

/**
 * 订单表Api接口管理器
 *
 * @author jetox
 * @date  2025/07/24 08:47
 **/
export default {
	// 获取订单表分页
	wineOrderPage(data) {
		return request('page', data, 'get')
	},
	// 提交订单表表单 edit为true时为编辑，默认为新增
	wineOrderSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除订单表
	wineOrderDelete(data) {
		return request('delete', data)
	},
	// 获取订单表详情
	wineOrderDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载订单表导入模板
    wineOrderDownloadTemplate(data) {
        return request('downloadImportTemplate', data, 'get', {
            responseType: 'blob'
        })
    },
    // 导入订单表
    wineOrderImport(data) {
        return request('importData', data)
    },
    // 导出订单表
    wineOrderExport(data) {
        return request('exportData', data, 'post', {
            responseType: 'blob'
        })
    },
    // 手动触发订单分账
    manualProfitSharing(data) {
        return request('manualProfitSharing', data, 'post')
    },
    // 手动修复退款状态
    fixRefundStatus(data) {
        return request('fixRefundStatus', data, 'post')
    }
}
