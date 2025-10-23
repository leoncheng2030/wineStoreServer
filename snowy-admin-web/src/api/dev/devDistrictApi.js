import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/dev/district/` + url, ...arg)

/**
 * 地区表Api接口管理器
 *
 * @author jetox
 * @date  2025/08/12 07:53
 **/
export default {
	// 获取地区表分页
	devDistrictPage(data) {
		return request('page', data, 'get')
	},
	// 提交地区表表单 edit为true时为编辑，默认为新增
	devDistrictSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除地区表
	devDistrictDelete(data) {
		return request('delete', data)
	},
	// 获取地区表详情
	devDistrictDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载地区表导入模板
	devDistrictDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
	},
	// 导入地区表
	devDistrictImport(data) {
		return request('importData', data)
	},
	// 导出地区表
	devDistrictExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	}
}
