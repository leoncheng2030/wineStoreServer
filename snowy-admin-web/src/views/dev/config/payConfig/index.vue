<template>
	<a-tabs v-model:activeKey="activeKey" tab-position="left">
		<a-tab-pane key="miniForm" tab="微信小程序支付">
			<mini-program-pay />
		</a-tab-pane>
		<a-tab-pane key="partnerForm" tab="微信服务商支付">
			<wx-partner-pay />
		</a-tab-pane>
		<a-tab-pane key="aliForm" tab="支付宝支付">
			<ali-pay />
		</a-tab-pane>
	</a-tabs>
</template>

<script setup name="loginConfig">
import MiniProgramPay from './miniProgramPayConfig.vue'
import WxPartnerPay from './wxPartnerPayConfig.vue'
import AliPay from './aliPayConfig.vue'
const activeKey = ref('miniForm')
</script>
