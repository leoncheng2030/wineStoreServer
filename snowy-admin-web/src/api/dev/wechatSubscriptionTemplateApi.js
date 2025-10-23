import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/dev/subscriptiontemplate/` + url, ...arg)

/**
 * 微信订阅消息模板表Api接口管理器
 *
 * @author jetox
 * @date  2025/09/29 00:49
 **/
export default {
	// 获取微信订阅消息模板表分页
	wechatSubscriptionTemplatePage(data) {
		return request('page', data, 'get')
	},
	// 提交微信订阅消息模板表表单 edit为true时为编辑，默认为新增
	wechatSubscriptionTemplateSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除微信订阅消息模板表
	wechatSubscriptionTemplateDelete(data) {
		return request('delete', data)
	},
	// 获取微信订阅消息模板表详情
	wechatSubscriptionTemplateDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载微信订阅消息模板表导入模板
    wechatSubscriptionTemplateDownloadTemplate(data) {
        return request('downloadImportTemplate', data, 'get', {
            responseType: 'blob'
        })
    },
    // 导入微信订阅消息模板表
    wechatSubscriptionTemplateImport(data) {
        return request('importData', data)
    },
    // 导出微信订阅消息模板表
    wechatSubscriptionTemplateExport(data) {
        return request('exportData', data, 'post', {
            responseType: 'blob'
        })
    }
}
