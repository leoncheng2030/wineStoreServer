<template>
	<a-card
		class="xn-wd"
		:bordered="false"
		:tab-list="tabListNoTitle"
		:active-tab-key="noTitleKey"
		@tabChange="(key) => onTabChange(key, 'noTitleKey')"
	>
		<p v-if="noTitleKey === 'baseConfig'">
			<base-config />
		</p>
		<p v-if="noTitleKey === 'commissionConfig'">
			<SysConfig />
		</p>
		<p v-if="noTitleKey === 'testConfig'">
			<test-config />
		</p>
	</a-card>
</template>

<script setup name="devConfig">
	import SysConfig from './commissionConfig/index.vue'
	import BaseConfig from './baseConfig/index.vue'
	import TestConfig from '@/views/wine/config/DeviceTestConfig/index.vue'
	const key = ref('baseConfig')
	const noTitleKey = ref('baseConfig')

	const tabListNoTitle = [
		{
			key: 'baseConfig',
			tab: '基础配置'
		},
		{
			key: 'commissionConfig',
			tab: '分销配置'
		},
		{
			key: 'testConfig',
			tab: '测试配置'
		}
	]

	const onTabChange = (value, type) => {
		if (type === 'key') {
			key.value = value
		} else if (type === 'noTitleKey') {
			noTitleKey.value = value
		}
	}
</script>
<style lang="less" scoped>
	:deep(.ant-tabs-tab) {
		font-size: 14px !important;
	}
</style>
