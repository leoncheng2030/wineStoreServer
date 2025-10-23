import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/dev/usersubscription/` + url, ...arg)

/**
 * 用户订阅消息授权表Api接口管理器
 *
 * @author jetox
 * @date  2025/09/29 00:49
 **/
export default {
	// 获取用户订阅消息授权表分页
	wechatUserSubscriptionPage(data) {
		return request('page', data, 'get')
	},
	// 提交用户订阅消息授权表表单 edit为true时为编辑，默认为新增
	wechatUserSubscriptionSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除用户订阅消息授权表
	wechatUserSubscriptionDelete(data) {
		return request('delete', data)
	},
	// 获取用户订阅消息授权表详情
	wechatUserSubscriptionDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载用户订阅消息授权表导入模板
    wechatUserSubscriptionDownloadTemplate(data) {
        return request('downloadImportTemplate', data, 'get', {
            responseType: 'blob'
        })
    },
    // 导入用户订阅消息授权表
    wechatUserSubscriptionImport(data) {
        return request('importData', data)
    },
    // 导出用户订阅消息授权表
    wechatUserSubscriptionExport(data) {
        return request('exportData', data, 'post', {
            responseType: 'blob'
        })
    }
}
