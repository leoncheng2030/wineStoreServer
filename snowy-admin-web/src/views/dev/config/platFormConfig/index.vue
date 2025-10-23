<template>
	<a-tabs v-model:activeKey="activeKey" tab-position="left">
		<a-tab-pane key="miniForm" tab="微信小程序">
			<mini-program />
		</a-tab-pane>
		<a-tab-pane key="aliForm" tab="支付宝小程序">
			<ali-mini-program />
		</a-tab-pane>
	</a-tabs>
</template>

<script setup name="loginConfig">
import MiniProgram from './miniProgramConfig.vue'
import AliMiniProgram from './aliMiniProgramConfig.vue'
const activeKey = ref('miniForm')
</script>
