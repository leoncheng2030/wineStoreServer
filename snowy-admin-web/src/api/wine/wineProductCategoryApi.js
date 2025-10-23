import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/wine/productcategory/` + url, ...arg)

/**
 * 酒品分类表Api接口管理器
 *
 * @author jetox
 * @date  2025/07/24 08:11
 **/
export default {
	// 获取酒品分类表分页
	wineProductCategoryPage(data) {
		return request('page', data, 'get')
	},
	// 获取酒品分类树
	wineProductCategoryTree(data) {
		return request('tree',data,'post')
	},
	// 提交酒品分类表表单 edit为true时为编辑，默认为新增
	wineProductCategorySubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除酒品分类表
	wineProductCategoryDelete(data) {
		return request('delete', data)
	},
	// 获取酒品分类表详情
	wineProductCategoryDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载酒品分类表导入模板
    wineProductCategoryDownloadTemplate(data) {
        return request('downloadImportTemplate', data, 'get', {
            responseType: 'blob'
        })
    },
    // 导入酒品分类表
    wineProductCategoryImport(data) {
        return request('importData', data)
    },
    // 导出酒品分类表
    wineProductCategoryExport(data) {
        return request('exportData', data, 'post', {
            responseType: 'blob'
        })
    }
}
