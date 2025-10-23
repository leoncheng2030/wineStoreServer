import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/dev/subscriptionsendlog/` + url, ...arg)

/**
 * 订阅消息发送记录表Api接口管理器
 *
 * @author jetox
 * @date  2025/09/29 00:50
 **/
export default {
	// 获取订阅消息发送记录表分页
	wechatSubscriptionSendLogPage(data) {
		return request('page', data, 'get')
	},
	// 提交订阅消息发送记录表表单 edit为true时为编辑，默认为新增
	wechatSubscriptionSendLogSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除订阅消息发送记录表
	wechatSubscriptionSendLogDelete(data) {
		return request('delete', data)
	},
	// 获取订阅消息发送记录表详情
	wechatSubscriptionSendLogDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载订阅消息发送记录表导入模板
    wechatSubscriptionSendLogDownloadTemplate(data) {
        return request('downloadImportTemplate', data, 'get', {
            responseType: 'blob'
        })
    },
    // 导入订阅消息发送记录表
    wechatSubscriptionSendLogImport(data) {
        return request('importData', data)
    },
    // 导出订阅消息发送记录表
    wechatSubscriptionSendLogExport(data) {
        return request('exportData', data, 'post', {
            responseType: 'blob'
        })
    }
}
