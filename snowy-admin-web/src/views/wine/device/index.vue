<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="4">
					<a-form-item label="设备编码" name="deviceCode">
						<a-input v-model:value="searchFormState.deviceCode" placeholder="请输入设备编码" />
					</a-form-item>
				</a-col>
				<a-col :span="4">
					<a-form-item label="门店" name="storeId">
						<a-select
							v-model:value="searchFormState.storeId"
							:options="storeOptions"
							placeholder="请输入设备名称"
							:field-names="{ label: 'storeName', value: 'id' }"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="4">
					<a-form-item label="代理商编号" name="agentAccount">
						<a-input v-model:value="searchFormState.agentAccount" placeholder="请输入代理商编号" />
					</a-form-item>
				</a-col>
				<a-col :span="4">
					<a-form-item label="酒品" name="currentProductId">
						<a-select
							v-model:value="searchFormState.currentProductId"
							:options="productOptions"
							:field-names="{ label: 'productName', value: 'id' }"
							placeholder="请输入设备名称"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="4">
					<a-button type="primary" @click="tableRef.refresh()">查询</a-button>
					<a-button style="margin: 0 8px" @click="reset">重置</a-button>
				</a-col>
			</a-row>
		</a-form>
		<s-table
			ref="tableRef"
			:columns="columns"
			:data="loadData"
			:alert="options.alert.show"
			bordered
			:row-key="(record) => record.id"
			:tool-config="toolConfig"
			:row-selection="options.rowSelection"
		>
			<template #operator>
				<a-space>
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('wineDeviceAdd')">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
					<a-button @click="importModelRef.onOpen()" v-if="hasPerm('wineDeviceImport')">
						<template #icon><import-outlined /></template>
						<span>导入</span>
					</a-button>
					<a-button @click="exportData" v-if="hasPerm('wineDeviceExport')">
						<template #icon><export-outlined /></template>
						<span>导出</span>
					</a-button>
					<a-button 
						@click="batchExportQrCodes" 
						v-if="hasPerm('wineDeviceExport') && selectedRowKeys.length > 0"
						type="default"
						:loading="batchExporting"
						:disabled="batchExporting"
					>
						<template #icon><qrcode-outlined /></template>
						<span>{{ batchExporting ? '正在导出...' : '批量导出二维码' }}</span>
					</a-button>
					<xn-batch-button
						v-if="hasPerm('wineDeviceBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchWineDevice"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'agentName'">
					<div class="info">
						<div class="nickname">	{{ record.agentName }}</div>
						<div class="phone">{{ record.agentPhone }}</div>
					</div>
				</template>
				<template v-if="column.dataIndex === 'operationType'">
					{{ $TOOL.dictTypeData('OPERATION_TYPE', record.operationType) }}
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="showQrCode(record)" v-if="hasPerm('wineDeviceEdit')">二维码</a>
						<a-divider type="vertical" v-if="hasPerm(['wineDeviceEdit', 'wineDeviceDelete'], 'and')" />
						<a @click="formRef.onOpen(record)" v-if="hasPerm('wineDeviceEdit')">编辑</a>
						<a-divider type="vertical" v-if="hasPerm(['wineDeviceEdit', 'wineDeviceDelete'], 'and')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteWineDevice(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('wineDeviceDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<ImportModel ref="importModelRef" />
	<Form ref="formRef" @successful="tableRef.refresh()" />
	
	<!-- 二维码弹窗组件 -->
	<QrCodeModal 
		v-model:open="qrCodeVisible" 
		:device-code="qrCodeDeviceCode"
		@cancel="handleQrCodeCancel"
	/>
	
	<!-- 批量导出进度弹窗 -->
	<a-modal 
		v-model:open="progressModalVisible" 
		title="批量导出二维码" 
		:footer="null" 
		:closable="false"
		:mask-closable="false"
		:width="500"
	>
		<div class="progress-container">
			<div class="progress-info">
				<p><strong>总进度：</strong>{{ exportProgress.current }}/{{ exportProgress.total }}</p>
				<p v-if="exportProgress.currentDevice"><strong>当前设备：</strong>{{ exportProgress.currentDevice }}</p>
				<p><strong>状态：</strong>{{ exportProgress.status }}</p>
			</div>
			
			<a-progress 
				:percent="exportProgress.percent" 
				:status="exportProgress.progressStatus"
				:show-info="true"
				:stroke-width="8"
			/>
			
			<div class="progress-stats" v-if="exportProgress.current > 0">
				<a-row :gutter="16">
					<a-col :span="8">
						<a-statistic title="成功" :value="exportProgress.success" :value-style="{ color: '#3f8600' }" />
					</a-col>
					<a-col :span="8">
						<a-statistic title="失败" :value="exportProgress.failed" :value-style="{ color: '#cf1322' }" />
					</a-col>
					<a-col :span="8">
						<a-statistic title="文件数" :value="exportProgress.fileCount" />
					</a-col>
				</a-row>
			</div>
			
			<div class="progress-actions" v-if="exportProgress.completed">
				<a-space>
					<a-button type="primary" @click="closeProgressModal">完成</a-button>
				</a-space>
			</div>
		</div>
	</a-modal>
</template>

<script setup name="device">
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import ImportModel from './importModel.vue'
	import downloadUtil from '@/utils/downloadUtil'
	import wineDeviceApi from '@/api/wine/wineDeviceApi'
	import wineStoreApi from '@/api/wine/wineStoreApi'
	import wineProductApi from '@/api/wine/wineProductApi'
	import QrCodeModal from '@/components/QrCodeModal/index.vue'
	import { ref } from 'vue'
	import { message } from 'ant-design-vue'
	import JSZip from 'jszip'

	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const importModelRef = ref()
	const formRef = ref()
	// 定义设备列表变量，用于存储完整的设备数据
	const deviceList = ref([])
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	const storeOptions = ref([])
	const productOptions = ref([])
	
	// 二维码相关
	const qrCodeVisible = ref(false)
	const qrCodeDeviceCode = ref('')
	
	// 显示二维码
	const showQrCode = (record) => {
		if (record && record.deviceCode) {
			qrCodeDeviceCode.value = String(record.deviceCode)
		} else {
			qrCodeDeviceCode.value = '未找到设备编码'
		}
		qrCodeVisible.value = true
	}
	
	// 处理二维码弹窗取消
	const handleQrCodeCancel = () => {
		qrCodeVisible.value = false
	}
	


	const columns = [
		{
			title: '设备名称',
			dataIndex: 'deviceName'
		},
		{
			title: '设备编码',
			dataIndex: 'deviceCode'
		},
		{
			title: '所属门店',
			dataIndex: 'storeName'
		},
		{
			title: '酒品',
			dataIndex: 'productName'
		},
		{
			title: '脉冲比',
			dataIndex: 'pulseRatio'
		},
		{
			title: '运营方式',
			dataIndex: 'operationType'
		},
		{
			title: '代理商',
			dataIndex: 'agentName'
		},
		{
			title: '排序码',
			dataIndex: 'sortCode'
		},
		{
			title: '备注',
			dataIndex: 'remark'
		}
	]
	// 操作栏通过权限判断是否显示
	if (hasPerm(['wineDeviceEdit', 'wineDeviceDelete'])) {
		columns.push({
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 200
		})
	}
	const selectedRowKeys = ref([])
	// 列表选择配置
	const options = {
		// columns数字类型字段加入 needTotal: true 可以勾选自动算账
		alert: {
			show: true,
			clear: () => {
				selectedRowKeys.value = ref([])
			}
		},
		rowSelection: {
			onChange: (selectedRowKey, selectedRows) => {
				selectedRowKeys.value = selectedRowKey
			}
		}
	}
	const loadData = async (parameter) => {
		loadStoreOptions()
		loadProductOptions()
		const searchFormParam = cloneDeep(searchFormState.value)
		return await wineDeviceApi.wineDevicePage(Object.assign(parameter, searchFormParam)).then((data) => {
			// 将获取到的设备数据存储到deviceList中
			if (data && data.records) {
				deviceList.value = data.records
			}
			return data
		})
	}
	// 加载门店
	const loadStoreOptions = () => {
		wineStoreApi.wineStoreList({}).then((data) => {
			storeOptions.value = data
		})
	}
	// 加载酒品
	const loadProductOptions = () => {
		wineProductApi.wineProductList({}).then((data) => {
			productOptions.value = data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteWineDevice = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		wineDeviceApi.wineDeviceDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 导出
	const exportData = () => {
		if (selectedRowKeys.value.length > 0) {
			const params = selectedRowKeys.value.map((m) => {
				return {
					id: m
				}
			})
			wineDeviceApi.wineDeviceExport(params).then((res) => {
				downloadUtil.resultDownload(res)
			})
		} else {
			wineDeviceApi.wineDeviceExport([]).then((res) => {
				downloadUtil.resultDownload(res)
			})
		}
	}
	// 批量删除
	const deleteBatchWineDevice = (params) => {
		wineDeviceApi.wineDeviceDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	
	// 获取选中设备的设备编码列表
	const getSelectedDeviceCodes = () => {
		// selectedRowKeys是设备id，需要从设备列表中获取对应的deviceCode
		if (!selectedRowKeys.value || selectedRowKeys.value.length === 0) {
			return []
		}
		
		if (!deviceList.value || deviceList.value.length === 0) {
			console.warn('设备列表为空，无法获取设备编码')
			return []
		}
		
		// 根据选中的id获取对应的deviceCode
		const deviceCodes = []
		selectedRowKeys.value.forEach(id => {
			const device = deviceList.value.find(item => item.id === id)
			if (device && device.deviceCode) {
				deviceCodes.push(device.deviceCode)
			} else {
				console.warn(`未找到id为${id}的设备或设备编码为空`)
			}
		})
		
		console.log('获取到的设备编码:', deviceCodes)
		return deviceCodes
	}
	
	// 创建带Logo和文字的二维码图片并返回Blob（用于ZIP打包）
	const createQRCodeWithTextForZip = (qrCanvas, deviceCode) => {
		return new Promise((resolve, reject) => {
			try {
				// 创建新的canvas用于合成
				const compositeCanvas = document.createElement('canvas')
				const ctx = compositeCanvas.getContext('2d')
				
				// 设置画布尺寸（二维码 + 文字区域）
				const qrSize = 256
				const textHeight = 50
				const padding = 20
				compositeCanvas.width = qrSize + padding * 2
				compositeCanvas.height = qrSize + textHeight + padding * 2
				
				// 设置背景色为白色
				ctx.fillStyle = '#ffffff'
				ctx.fillRect(0, 0, compositeCanvas.width, compositeCanvas.height)
				
				// 绘制二维码
				ctx.drawImage(qrCanvas, padding, padding, qrSize, qrSize)
				
				// 设置文字样式
				ctx.fillStyle = '#000000'
				ctx.textAlign = 'center'
				ctx.font = 'bold 24px Arial, sans-serif'
				
				// 统一的带Logo文字内容
				const deviceText = `编号：${deviceCode}`
				ctx.fillText(deviceText, compositeCanvas.width / 2, qrSize + padding + 25)
				
				// 绘制提示文字
				ctx.font = 'bold 20px Arial, sans-serif'
				const tipText = '微信扫一扫更方便'
				ctx.fillText(tipText, compositeCanvas.width / 2, qrSize + padding + 50)
				
				// 转换为Blob对象
				compositeCanvas.toBlob((blob) => {
					if (blob) {
						resolve(blob)
					} else {
						reject(new Error('生成图片Blob失败'))
					}
				}, 'image/png', 1.0)
				
			} catch (error) {
				console.error('创建带文字二维码Blob失败:', error)
				reject(error)
			}
		})
	}
	
	// 生成二维码Canvas
	const generateQRCodeCanvas = (content) => {
		return new Promise((resolve, reject) => {
			try {
				// 动态导入qrcode库
				import('qrcode').then(QRCode => {
					const canvas = document.createElement('canvas')
					QRCode.default.toCanvas(canvas, content, {
						width: 256,
						margin: 2,
						color: {
							dark: '#000000',
							light: '#FFFFFF'
						}
					}, (error) => {
						if (error) {
							reject(error)
						} else {
							resolve(canvas)
						}
					})
				}).catch(reject)
			} catch (error) {
				reject(error)
			}
		})
	}

	// 生成带Logo的二维码Canvas
	const generateQRCodeCanvasWithLogo = (content) => {
		return new Promise((resolve, reject) => {
			try {
				// 动态导入qrcode库
				import('qrcode').then(QRCode => {
					const canvas = document.createElement('canvas')
					QRCode.default.toCanvas(canvas, content, {
						width: 256,
						margin: 2,
						color: {
							dark: '#000000',
							light: '#FFFFFF'
						}
					}, async (error) => {
						if (error) {
							reject(error)
							return
						}
						
						try {
							// 在二维码上添加logo
							const ctx = canvas.getContext('2d')
							const logoImg = new Image()
							logoImg.crossOrigin = 'anonymous'
							
							logoImg.onload = () => {
								try {
									// 计算logo位置（居中）
									const logoSize = 48
									const x = (canvas.width - logoSize) / 2
									const y = (canvas.height - logoSize) / 2
									
									// 在logo位置先画一个白色背景
									ctx.fillStyle = '#FFFFFF'
									ctx.fillRect(x - 2, y - 2, logoSize + 4, logoSize + 4)
									
									// 绘制logo
									ctx.drawImage(logoImg, x, y, logoSize, logoSize)
									resolve(canvas)
								} catch (drawError) {
									console.error('绘制logo失败:', drawError)
									// 如果绘制logo失败，返回原始二维码
									resolve(canvas)
								}
							}
							
							logoImg.onerror = () => {
								console.error('logo图片加载失败，返回普通二维码')
								// 如果logo加载失败，返回原始二维码
								resolve(canvas)
							}
							
							// 动态导入logo图片
							import('@/assets/images/logo.jpg').then(logoModule => {
								logoImg.src = logoModule.default
							}).catch(() => {
								console.error('logo模块导入失败，返回普通二维码')
								resolve(canvas)
							})
							
						} catch (logoError) {
							console.error('处理logo时出错:', logoError)
							// 如果处理logo出错，返回原始二维码
							resolve(canvas)
						}
					})
				}).catch(reject)
			} catch (error) {
				reject(error)
			}
		})
	}


	
	// 批量导出状态
	const batchExporting = ref(false)
	
	// 进度条相关
	const progressModalVisible = ref(false)
	const exportProgress = ref({
		current: 0,
		total: 0,
		percent: 0,
		status: '准备中...',
		progressStatus: 'active',
		currentDevice: '',
		success: 0,
		failed: 0,
		fileCount: 0,
		completed: false
	})
	
	// 重置进度数据
	const resetProgress = () => {
		exportProgress.value = {
			current: 0,
			total: 0,
			percent: 0,
			status: '准备中...',
			progressStatus: 'active',
			currentDevice: '',
			success: 0,
			failed: 0,
			fileCount: 0,
			completed: false
		}
	}
	
	// 更新进度
	const updateProgress = (current, total, status, currentDevice = '') => {
		exportProgress.value.current = current
		exportProgress.value.total = total
		exportProgress.value.percent = total > 0 ? Math.round((current / total) * 100) : 0
		exportProgress.value.status = status
		exportProgress.value.currentDevice = currentDevice
	}
	
	// 关闭进度弹窗
	const closeProgressModal = () => {
		progressModalVisible.value = false
		resetProgress()
	}
	
	// 获取选中设备的详细数据
	const getSelectedDevicesData = () => {
		if (!selectedRowKeys.value || selectedRowKeys.value.length === 0) {
			return []
		}
		
		if (!deviceList.value || deviceList.value.length === 0) {
			console.warn('设备列表为空，无法获取设备详细信息')
			return []
		}
		
		// 根据选中的id获取对应的设备详细信息
		const selectedDevices = []
		selectedRowKeys.value.forEach(id => {
			const device = deviceList.value.find(item => item.id === id)
			if (device) {
				selectedDevices.push(device)
			} else {
				console.warn(`未找到id为${id}的设备`)
			}
		})
		
		console.log('获取到的设备详细数据:', selectedDevices)
		return selectedDevices
	}
	
	// 批量导出二维码
	const batchExportQrCodes = async () => {
		if (selectedRowKeys.value.length === 0) {
			message.warning('请先选择要导出的设备')
			return
		}
		
		if (batchExporting.value) {
			message.warning('正在导出中，请稍候...')
			return
		}
		
		try {
			batchExporting.value = true
			// 显示进度弹窗
			resetProgress()
			progressModalVisible.value = true
			updateProgress(0, selectedRowKeys.value.length, '正在准备导出数据...')
			
			// 获取选中设备的详细数据，支持异步等待
			let selectedDevices = getSelectedDevicesData()
			
			// 如果第一次获取为空，等待一下再试
			if ((!selectedDevices || selectedDevices.length === 0) && selectedRowKeys.value.length > 0) {
				console.log('第一次获取数据为空，等待数据加载...')
				updateProgress(0, selectedRowKeys.value.length, '等待表格数据加载完成...')
				
				// 等待500ms后重试
				await new Promise(resolve => setTimeout(resolve, 500))
				selectedDevices = getSelectedDevicesData()
				
				// 如果还是为空，再等待一次
				if ((!selectedDevices || selectedDevices.length === 0) && selectedRowKeys.value.length > 0) {
					console.log('第二次获取数据为空，再次等待...')
					updateProgress(0, selectedRowKeys.value.length, '重新获取设备数据...')
					await new Promise(resolve => setTimeout(resolve, 1000))
					selectedDevices = getSelectedDevicesData()
				}
			}
			
			if (selectedDevices.length === 0) {
				exportProgress.value.progressStatus = 'exception'
				updateProgress(0, selectedRowKeys.value.length, '未找到选中的设备数据')
				message.error('未找到选中的设备数据，请确保表格数据已加载完成后重新选择')
				console.error('最终获取的设备数据为空，选中的行键:', selectedRowKeys.value)
				return
			}
			
			console.log('最终获取到的选中设备数据:', selectedDevices)
			
			// 更新总数为实际设备数量
			updateProgress(0, selectedDevices.length, '开始生成二维码...')
			
			const zip = new JSZip()
			let addedCount = 0
			let failedCount = 0
			const failedDevices = []
			
			// 为每个设备生成二维码
			for (let i = 0; i < selectedDevices.length; i++) {
				const device = selectedDevices[i]
				const deviceCode = String(device.deviceCode || '未知设备')
				
				// 更新进度
				updateProgress(i, selectedDevices.length, `正在生成二维码`, deviceCode)
				
				try {
					// 验证设备编码
					if (!device.deviceCode || device.deviceCode.trim() === '') {
						throw new Error('设备编码为空')
					}
					
					// 生成带Logo和URL的统一二维码
					const qrContent = `https://sdback.hn-dc.com?deviceCode=${deviceCode}`
					const canvas = await generateQRCodeCanvasWithLogo(qrContent)
					const blob = await createQRCodeWithTextForZip(canvas, deviceCode)
					zip.file(`${deviceCode}.png`, blob)
					
					addedCount += 1
					exportProgress.value.success++
					exportProgress.value.fileCount += 1
				} catch (error) {
					console.error(`生成设备${deviceCode}的二维码失败:`, error)
					failedCount++
					exportProgress.value.failed++
					failedDevices.push({ deviceCode, error: error.message })
					// 继续处理其他设备，不中断整个流程
				}
			}
			
			if (addedCount === 0) {
				exportProgress.value.progressStatus = 'exception'
				updateProgress(selectedDevices.length, selectedDevices.length, '所有设备的二维码生成都失败了')
				exportProgress.value.completed = true
				message.error('所有设备的二维码生成都失败了，请检查设备数据')
				if (failedDevices.length > 0) {
					console.error('失败的设备列表:', failedDevices)
				}
				return
			}
			
			// 生成ZIP文件
			updateProgress(selectedDevices.length, selectedDevices.length, '正在打包ZIP文件...')
			try {
				const zipBlob = await zip.generateAsync({ 
					type: 'blob',
					compression: 'DEFLATE',
					compressionOptions: { level: 6 }
				})
				
				// 下载ZIP文件
				updateProgress(selectedDevices.length, selectedDevices.length, '正在准备下载...')
				const zipUrl = URL.createObjectURL(zipBlob)
				const timestamp = new Date().toISOString().slice(0, 19).replace(/[:-]/g, '')
				const link = document.createElement('a')
				link.download = `设备二维码批量导出_${timestamp}.zip`
				link.href = zipUrl
				link.style.display = 'none'
				document.body.appendChild(link)
				link.click()
				document.body.removeChild(link)
				
				// 清理URL对象
				URL.revokeObjectURL(zipUrl)
				
				// 标记完成
				exportProgress.value.progressStatus = 'success'
				updateProgress(selectedDevices.length, selectedDevices.length, '导出完成')
				exportProgress.value.completed = true
				
				// 显示成功消息
				const successCount = selectedDevices.length - failedCount
				let successMsg = `二维码ZIP包下载成功！成功导出${successCount}个设备的二维码`
				if (failedCount > 0) {
					successMsg += `，${failedCount}个设备导出失败`
					console.warn('部分设备导出失败:', failedDevices)
				}
				message.success(successMsg)
				
			} catch (zipError) {
				exportProgress.value.progressStatus = 'exception'
				updateProgress(selectedDevices.length, selectedDevices.length, 'ZIP文件生成失败')
				exportProgress.value.completed = true
				throw new Error(`ZIP文件生成失败: ${zipError.message}`)
			}
			
		} catch (error) {
			console.error('批量导出二维码失败:', error)
			message.error(`批量导出二维码失败: ${error.message || '未知错误'}`)
			exportProgress.value.progressStatus = 'exception'
			exportProgress.value.completed = true
		} finally {
			batchExporting.value = false
		}
	}
</script>

<style scoped>
</style>