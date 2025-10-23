<template>
	<a-modal
		v-model:open="visible"
		title="地图选点"
		:width="900"
		:confirm-loading="confirmLoading"
		@cancel="handleCancel"
		@ok="handleOk"
	>
		<div class="location-picker-modal">
			<div class="map-wrapper">
				<baidu-map
					ref="mapRef"
					:api-key="apiKey"
					:center="mapCenter"
					:zoom="zoom"
					:height="parseInt(mapHeight)"
					@complete="handleMapComplete"
					@markerClick="handleMarkerClick"
				/>
			</div>
			<div class="location-info" v-if="hasLocationData">
				<div class="info-row">
					<span class="label">详细地址：</span>
					<span class="value">{{ locationData.address }}</span>
				</div>
				<div class="info-row" v-if="locationData.province || locationData.city || locationData.district">
					<span class="label">行政区划：</span>
					<span class="value">
						{{ locationData.province }}
						<span v-if="locationData.province && locationData.city"> - </span>
						{{ locationData.city }}
						<span v-if="locationData.city && locationData.district"> - </span>
						{{ locationData.district }}
					</span>
				</div>
				<div class="info-row">
					<span class="label">经纬度：</span>
					<span class="value">{{ locationData.longitude }}, {{ locationData.latitude }}</span>
				</div>
			</div>
		</div>
	</a-modal>
</template>

<script setup name="LocationPicker">
	import { ref, computed, watch } from 'vue'
	import BaiduMap from './baiduMap/index.vue'

	const props = defineProps({
		// 纬度
		latitude: {
			type: Number,
			default: null
		},
		// 经度
		longitude: {
			type: Number,
			default: null
		},
		// 详细地址
		address: {
			type: String,
			default: ''
		},
		// 省份
		province: {
			type: String,
			default: ''
		},
		// 城市
		city: {
			type: String,
			default: ''
		},
		// 县区
		district: {
			type: String,
			default: ''
		},
		apiKey: {
			type: String,
			default: 'your-baidu-map-api-key'
		},
		mapHeight: {
			type: String,
			default: '400px'
		},
		center: {
			type: Object,
			default: () => ({ lng: 116.404, lat: 39.915 })
		},
		zoom: {
			type: Number,
			default: 15
		}
	})

	const emits = defineEmits(['select'])

	const visible = ref(false)
	const confirmLoading = ref(false)
	const mapRef = ref(null)

	// 地图中心点（保持稳定，不随选点变化）
	const mapCenter = ref([116.404, 39.915]) // 默认北京坐标
	
	// 初始化地图中心点
	const initMapCenter = () => {
		if (props.center && props.center.lng && props.center.lat) {
			mapCenter.value = [props.center.lng, props.center.lat]
		} else if (locationData.value.longitude && locationData.value.latitude) {
			mapCenter.value = [locationData.value.longitude, locationData.value.latitude]
		}
	}

	// 内部位置数据
	const locationData = ref({
		longitude: null,
		latitude: null,
		address: '',
		province: '',
		city: '',
		district: ''
	})

	// 计算是否有位置数据
	const hasLocationData = computed(() => {
		return locationData.value.longitude || locationData.value.latitude || locationData.value.address
	})

	// 监听props变化，同步到内部数据
	watch(
		() => [props.latitude, props.longitude, props.address, props.province, props.city, props.district],
		([latitude, longitude, address, province, city, district]) => {
			locationData.value = {
				longitude: longitude,
				latitude: latitude,
				address: address || '',
				province: province || '',
				city: city || '',
				district: district || ''
			}
			// 当props变化时，重新初始化地图中心点
			initMapCenter()
		},
		{ immediate: true }
	)

	// 显示弹窗
	const show = () => {
		visible.value = true
		// 重新同步props数据到内部数据
		locationData.value = {
			longitude: props.longitude,
			latitude: props.latitude,
			address: props.address || '',
			province: props.province || '',
			city: props.city || '',
			district: props.district || ''
		}
		// 初始化地图中心点
		initMapCenter()
	}

	// 隐藏弹窗
	const hide = () => {
		visible.value = false
	}

	// 地图完成加载
	const handleMapComplete = () => {
		console.log('地图加载完成')
		// 如果有坐标数据，在地图上渲染标记点（初始化时使用完整渲染）
		if (locationData.value.longitude && locationData.value.latitude) {
			const markerData = [{
				position: [locationData.value.longitude, locationData.value.latitude],
				title: locationData.value.address || '选中位置'
			}]
			mapRef.value.renderMarker(markerData)
		}
		// 添加地图点击事件监听
		addMapClickListener()
	}

	// 添加地图点击事件监听
	const addMapClickListener = () => {
		if (mapRef.value && mapRef.value.baiduMap) {
			mapRef.value.baiduMap.addEventListener('click', (e) => {
				// 清除之前的标记
				mapRef.value.clearOverlay()
				// 更新位置数据
				locationData.value.longitude = e.latlng.lng
				locationData.value.latitude = e.latlng.lat
				// 根据坐标获取地址信息
				getAddressByCoordinates(e.latlng.lng, e.latlng.lat)
				// 渲染新的标记点
				renderMarkerOnMap()
			})
		}
	}

	// 根据坐标获取地址信息
	const getAddressByCoordinates = (lng, lat) => {
		if (window.BMapGL) {
			const geocoder = new window.BMapGL.Geocoder()
			const point = new window.BMapGL.Point(lng, lat)
			geocoder.getLocation(point, (result) => {
				if (result) {
					locationData.value.address = result.address || ''
					// 获取详细的行政区划信息
					if (result.addressComponents) {
						locationData.value.province = result.addressComponents.province || ''
						locationData.value.city = result.addressComponents.city || ''
						locationData.value.district = result.addressComponents.district || ''
					}
					console.log('获取到地址信息:', {
						address: result.address,
						province: locationData.value.province,
						city: locationData.value.city,
						district: locationData.value.district
					})
				} else {
					console.log('无法获取地址信息')
				}
			})
		}
	}

	// 处理标记点击事件
	const handleMarkerClick = (position) => {
		console.log('标记点击:', position)
	}

	// 在地图上渲染标记点
	const renderMarkerOnMap = () => {
		if (mapRef.value && locationData.value.longitude && locationData.value.latitude) {
			const markerData = [{
				position: [locationData.value.longitude, locationData.value.latitude],
				title: locationData.value.address || '选中位置'
			}]
			// 调用BaiduMap组件的渲染标记方法（不调整视图）
			mapRef.value.renderMarkerOnly(markerData)
		}
	}

	// 坐标变化处理
	const handleCoordinateChange = () => {
		// 当坐标输入框变化时，重新渲染地图标记点
		console.log('坐标变化:', locationData.value)
		if (locationData.value.longitude && locationData.value.latitude) {
			// 根据新坐标获取地址信息
			getAddressByCoordinates(locationData.value.longitude, locationData.value.latitude)
			// 重新渲染标记点
			renderMarkerOnMap()
		}
	}

	// 取消按钮处理
	const handleCancel = () => {
		hide()
	}

	// 确定按钮处理
	const handleOk = () => {
		// 触发选择事件，传递位置数据
		emits('select', {
			longitude: locationData.value.longitude,
			latitude: locationData.value.latitude,
			address: locationData.value.address,
			province: locationData.value.province,
			city: locationData.value.city,
			district: locationData.value.district
		})
		hide()
	}

	defineExpose({
		show,
		hide
	})
</script>

<style lang="less" scoped>
	.location-picker-modal {
		padding: 16px 0;
		
		.map-wrapper {
			margin-bottom: 16px;
			border: 1px solid #d9d9d9;
			border-radius: 6px;
			overflow: hidden;
		}
		
		.location-info {
			margin-top: 16px;
			padding: 12px;
			background-color: #f5f5f5;
			border-radius: 6px;
			border: 1px solid #d9d9d9;
			
			.info-row {
				display: flex;
				margin-bottom: 8px;
				
				&:last-child {
					margin-bottom: 0;
				}
				
				.label {
					font-weight: 500;
					color: #666;
					min-width: 80px;
					flex-shrink: 0;
				}
				
				.value {
					color: #333;
					flex: 1;
					word-break: break-all;
				}
			}
		}
		
		.location-form {
			margin-top: 16px;
		}
	}
</style>