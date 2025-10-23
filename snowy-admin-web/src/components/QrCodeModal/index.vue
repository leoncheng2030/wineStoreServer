<template>
	<!-- 二维码弹窗 -->
	<a-modal 
		v-model:open="visible" 
		title="二维码" 
		:footer="null" 
		:width="600"
		@cancel="handleCancel"
	>
		<div class="text-center">
			<a-tabs v-model:activeKey="activeKey">
				<a-tab-pane key="1" tab="设备二维码">
					<div class="qr-code-container">
						<QrcodeVue
							v-if="qrCodeText"
							:value="qrCodeText"
							:size="256"
							level="H"
							render-as="canvas"
							ref="qrCodeRef"
							:image-settings="logoSettings"
						/>
						<a-empty v-else description="未找到设备编码" />
					</div>
					<p class="mt-4">设备编码: {{ deviceCode }}</p>
					<p class="mt-2 text-gray-500">小程序扫码后可用于设备管理和购买</p>
				</a-tab-pane>
			</a-tabs>
			
			<!-- 下载按钮 -->
			<a-space style="margin-top: 16px;">
				<a-button 
					type="primary" 
					@click="downloadQRCode" 
					:disabled="!qrCodeText || deviceCode === '未找到设备编码'"
				>
					下载二维码
				</a-button>
			</a-space>
		</div>
	</a-modal>
</template>

<script setup name="QrCodeModal">
	import { ref, computed, watch } from 'vue'
	import { message } from 'ant-design-vue'
	import QrcodeVue from 'qrcode.vue'
	import logoImage from '@/assets/images/logo.jpg'
	import JSZip from 'jszip'

	// Props
	const props = defineProps({
		open: {
			type: Boolean,
			default: false
		},
		deviceCode: {
			type: String,
			default: ''
		}
	})

	// Emits
	const emit = defineEmits(['update:open', 'cancel'])

	// 响应式数据
	const visible = ref(false)
	const qrCodeText = ref('')
	const qrCodeRef = ref(null)
	const activeKey = ref('1')

	// LOGO设置
	const logoSettings = computed(() => {
		return {
			src: logoImage,
			width: 48,
			height: 48,
			excavate: true,
			margin: 0,
			crossOrigin: 'anonymous'
		}
	})

	// 监听props变化
	watch(() => props.open, (newVal) => {
		visible.value = newVal
		if (newVal && props.deviceCode) {
			// 生成带URL的二维码内容，保持与之前的格式一致
			qrCodeText.value = `https://sdback.hn-dc.com?deviceCode=${String(props.deviceCode)}`
		} else {
			qrCodeText.value = ''
		}
	}, { immediate: true })

	watch(visible, (newVal) => {
		emit('update:open', newVal)
	})

	// 关闭弹窗
	const handleCancel = () => {
		visible.value = false
		emit('cancel')
	}

	// 下载二维码
	const downloadQRCode = () => {
		if (!props.deviceCode || props.deviceCode === '未找到设备编码') {
			message.error('设备编码无效，无法下载二维码')
			return
		}
		
		let canvas = null
		const fileName = `设备二维码_${props.deviceCode}.png`
		
		// 直接从DOM中查找canvas元素
		const modalElement = document.querySelector('.ant-modal-body')
		if (modalElement) {
			const canvasElements = modalElement.querySelectorAll('canvas')
			if (canvasElements.length > 0) {
				canvas = canvasElements[0] // 只有一个二维码，直接使用第一个
			}
		}
		
		if (canvas && typeof canvas.toDataURL === 'function') {
			try {
				// 等待一小段时间确保二维码完全渲染
				setTimeout(() => {
					try {
						createQRCodeWithText(canvas, fileName)
					} catch (error) {
						console.error('生成二维码数据失败:', error)
						message.error('生成二维码数据失败，请重试')
					}
				}, 100) // 延迟100ms确保渲染完成
			} catch (error) {
				console.error('下载二维码失败:', error)
				message.error('下载二维码失败，请重试')
			}
		} else {
			console.warn('Canvas element not found or toDataURL not available')
			message.error('未找到二维码画布，请确保二维码已正确显示后再试')
		}
	}

	// 创建带文字的二维码图片
	const createQRCodeWithText = (qrCanvas, fileName) => {
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
			const deviceText = `编号：${props.deviceCode}`
			ctx.fillText(deviceText, compositeCanvas.width / 2, qrSize + padding + 25)
			
			const tipText = '微信扫一扫更方便'
			ctx.font = 'bold 18px Arial, sans-serif'
			ctx.fillText(tipText, compositeCanvas.width / 2, qrSize + padding + 50)
			
			// 转换为图片并下载
			const dataURL = compositeCanvas.toDataURL('image/png', 1.0)
			if (dataURL && dataURL !== 'data:,') {
				downloadImage(dataURL, fileName)
				message.success('二维码下载成功！')
			} else {
				message.error('生成带文字的二维码失败，请重试')
			}
		} catch (error) {
			console.error('创建带文字二维码失败:', error)
			message.error('创建带文字二维码失败，请重试')
		}
	}



	// 下载图片的辅助函数
	const downloadImage = (dataURL, fileName) => {
		const link = document.createElement('a')
		link.download = fileName
		link.href = dataURL
		link.style.display = 'none'
		document.body.appendChild(link)
		link.click()
		document.body.removeChild(link)
	}


</script>

<style scoped>
.text-center {
	text-align: center;
}

.mt-4 {
	margin-top: 1rem;
}

.qr-code-container {
	display: flex;
	justify-content: center;
	align-items: center;
	min-height: 256px;
}
</style>