<template>
	<div class="location-picker-demo">
		<a-card title="地图选点示例（Modal模式）">
			<a-space>
				<a-button type="primary" @click="showLocationPicker">打开地图选点</a-button>
				<a-button @click="clearSelectedLocation">清空选点</a-button>
			</a-space>
			
			<a-card title="选点结果" style="margin-top: 20px" v-if="confirmedLocation">
				<a-descriptions :column="1">
					<a-descriptions-item label="经度">
						{{ confirmedLocation.longitude }}
					</a-descriptions-item>
					<a-descriptions-item label="纬度">
						{{ confirmedLocation.latitude }}
					</a-descriptions-item>
					<a-descriptions-item label="详细地址">
						{{ confirmedLocation.address }}
					</a-descriptions-item>
				</a-descriptions>
			</a-card>
		</a-card>
		
		<location-picker
			ref="pickerRef"
			api-key="NtTydKuftIVXAy526uWXZoHS86lg0KeW"
			:center="[116.404, 39.915]"
			:zoom="15"
			:map-height="500"
			@select="handleLocationSelect"
		/>
	</div>
</template>

<script setup name="LocationPickerDemo">
	import { ref } from 'vue'
	import LocationPicker from '@/components/Map/LocationPicker.vue'
	
	const pickerRef = ref(null)
	const confirmedLocation = ref(null)
	
	// 显示地图选点弹窗
	const showLocationPicker = () => {
		pickerRef.value.show()
	}
	
	// 清空选点结果
	const clearSelectedLocation = () => {
		confirmedLocation.value = null
	}
	
	// 处理选点确认事件
	const handleLocationSelect = (location) => {
		confirmedLocation.value = location
		console.log('选点确认:', location)
	}
</script>

<style lang="less" scoped>
	.location-picker-demo {
		padding: 16px;
	}
</style>