import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/wine/useraccount/` + url, ...arg)

/**
 * 账户列表Api接口管理器
 *
 * @author jetox
 * @date  2025/07/24 08:55
 **/
export default {
	// 获取账户列表分页
	wineUserAccountPage(data) {
		return request('page', data, 'get')
	},
	// 提交账户列表表单 edit为true时为编辑，默认为新增
	wineUserAccountSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除账户列表
	wineUserAccountDelete(data) {
		return request('delete', data)
	},
	// 获取账户列表详情
	wineUserAccountDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载账户列表导入模板
	wineUserAccountDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
	},
	// 导入账户列表
	wineUserAccountImport(data) {
		return request('importData', data)
	},
	// 导出账户列表
	wineUserAccountExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	},
	// 根据用户ID查询账户详情
	wineUserAccountDetailByUserId(data) {
		return request('getUserInfo', data, 'get')
	}
}
