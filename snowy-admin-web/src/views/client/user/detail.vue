<template>
	<xn-form-container
		title="用户详情"
		:width="800"
		:visible="visible"
		:destroy-on-close="true"
		:body-style="{ 'padding-top': '0px' }"
		@close="onClose"
	>
		<a-tabs default-active-key="1">
			<a-tab-pane key="1" tab="基本信息">
				<a-descriptions bordered size="middle" :column="1">
					<a-descriptions-item label="头像">
						<a-avatar :src="userInfo.avatar" size="large" />
					</a-descriptions-item>
					<a-descriptions-item label="姓名">{{ userInfo.name }}</a-descriptions-item>
					<a-descriptions-item label="账号">{{ userInfo.account }}</a-descriptions-item>
					<a-descriptions-item label="手机号">{{ userInfo.phone }}</a-descriptions-item>
					<a-descriptions-item label="性别">{{ $TOOL.dictTypeData('GENDER', userInfo.gender) }}</a-descriptions-item>
					<a-descriptions-item label="昵称">{{ userInfo.nickname }}</a-descriptions-item>
					<a-descriptions-item label="状态">
						<a-tag :color="userInfo.userStatus === 'ENABLE' ? 'green' : 'red'">
							{{ $TOOL.dictTypeData('COMMON_STATUS', userInfo.userStatus) }}
						</a-tag>
					</a-descriptions-item>
					<a-descriptions-item label="是否代理">
						<a-tag :color="userInfo.isAgent === 'YES' ? 'green' : 'red'">
							{{ userInfo.isAgent === 'YES' ? '是' : '否' }}
						</a-tag>
					</a-descriptions-item>
				</a-descriptions>
			</a-tab-pane>
			<a-tab-pane key="2" tab="账户信息" force-render>
				<a-descriptions bordered size="middle" :column="1">
					<a-descriptions-item label="账户余额">{{ userAccountInfo.totalBalance }}</a-descriptions-item>
					<a-descriptions-item label="可用余额">{{ userAccountInfo.availableBalance }}</a-descriptions-item>
					<a-descriptions-item label="冻结余额">{{ userAccountInfo.frozenBalance }}</a-descriptions-item>
					<a-descriptions-item label="累计佣金">{{ userAccountInfo.totalCommission }}</a-descriptions-item>
					<a-descriptions-item label="累计提现">{{ userAccountInfo.totalWithdraw }}</a-descriptions-item>
					<a-descriptions-item label="上次佣金时间">{{ userAccountInfo.lastCommissionTime }}</a-descriptions-item>
					<a-descriptions-item label="上次提现时间">{{ userAccountInfo.lastWithdrawTime }}</a-descriptions-item>
					<a-descriptions-item label="账户状态">
						<a-tag :color="userAccountInfo.status === 'NORMAL' ? 'green' : 'red'">
							{{ userAccountInfo.status === 'NORMAL' ? '正常' : '异常' }}
						</a-tag>
					</a-descriptions-item>
				</a-descriptions>
			</a-tab-pane>
			<a-tab-pane key="3" tab="代理商信息" force-render v-if="clientUserInfo.isAgent === 'YES'">
				<a-descriptions bordered size="middle" :column="1">
					<a-descriptions-item label="代理商编号">{{ agentInfo.agentInfo.agentCode }}</a-descriptions-item>
					<a-descriptions-item label="子商户号">{{ agentInfo.agentInfo.subMerId }}</a-descriptions-item>
					<a-descriptions-item label="子应用ID">{{ agentInfo.agentInfo.subAppId }}</a-descriptions-item>
					<a-descriptions-item label="分账比例">{{ agentInfo.agentInfo.profitSharingMaxRate }}</a-descriptions-item>
					<a-descriptions-item label="申请状态" v-if="agentInfo.applyInfo">{{ agentInfo.applyInfo.status }}</a-descriptions-item>
					<a-descriptions-item label="真实姓名" v-if="agentInfo.applyInfo">{{ agentInfo.applyInfo.realName }}</a-descriptions-item>
					<a-descriptions-item label="手机号码" v-if="agentInfo.applyInfo">{{ agentInfo.applyInfo.phone }}</a-descriptions-item>
					<a-descriptions-item label="身份证号" v-if="agentInfo.applyInfo">{{ agentInfo.applyInfo.idCard }}</a-descriptions-item>
					<a-descriptions-item label="省份" v-if="agentInfo.applyInfo">{{ agentInfo.applyInfo.province }}</a-descriptions-item>
					<a-descriptions-item label="城市" v-if="agentInfo.applyInfo">{{ agentInfo.applyInfo.city }}</a-descriptions-item>
					<a-descriptions-item label="区县" v-if="agentInfo.applyInfo">{{ agentInfo.applyInfo.district }}</a-descriptions-item>
					<a-descriptions-item label="详细地址" v-if="agentInfo.applyInfo">{{ agentInfo.applyInfo.address }}</a-descriptions-item>
					<a-descriptions-item label="申请理由" v-if="agentInfo.applyInfo">{{ agentInfo.applyInfo.reason }}</a-descriptions-item>
					<a-descriptions-item label="审核意见" v-if="agentInfo.applyInfo">{{ agentInfo.applyInfo.auditRemark }}</a-descriptions-item>
				</a-descriptions>
			</a-tab-pane>
		</a-tabs>
	</xn-form-container>
</template>
<script setup>
	import clientUserApi from '@/api/client/clientUserApi'
	import wineUserAccountApi from '@/api/wine/wineUserAccountApi'
	import wineAgentApi from '@/api/wine/wineAgentApi'
	// 默认是关闭状态
	const visible = ref(false)
	const formRef = ref()
	const emit = defineEmits({ successful: null })
	const clientUserInfo = ref({})
	const userInfo = ref({})
	const userAccountInfo = ref({})
	const agentInfo = ref({})
	// 打开抽屉
	const onOpen = (recored) => {
		visible.value = true
		if (recored) {
			clientUserInfo.value = recored
		}
		loadData()
	}
	// 关闭抽屉
	const onClose = () => {
		visible.value = false
	}
	const loadData = () => {
		clientUserApi.userDetail({ id: clientUserInfo.value.id }).then((res) => {
			userInfo.value = res
		})
		wineUserAccountApi.wineUserAccountDetailByUserId({ userId: clientUserInfo.value.id }).then((res) => {
			userAccountInfo.value = res
		})
		if (clientUserInfo.value.isAgent === 'YES') {
			wineAgentApi
				.wineAgentDetailWithApplyInfo({ userId: clientUserInfo.value.id })
				.then((res) => {
					agentInfo.value = res
				})
				.catch(() => {
					// 用户可能不是代理商，忽略错误
				})
		}
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
