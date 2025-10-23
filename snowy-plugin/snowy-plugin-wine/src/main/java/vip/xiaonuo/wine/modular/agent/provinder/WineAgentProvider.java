package vip.xiaonuo.wine.modular.agent.provinder;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.client.ClientApi;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.wine.api.OrderApi;
import vip.xiaonuo.wine.api.WineAgentApi;
import vip.xiaonuo.wine.modular.agent.entity.WineAgent;
import vip.xiaonuo.wine.modular.agent.service.WineAgentService;

import java.math.BigDecimal;

@Slf4j
@Service
public class WineAgentProvider implements WineAgentApi {
    @Resource
    private WineAgentService wineAgentService;
    @Resource
    private ClientApi clientApi;
    @Resource
    private OrderApi orderApi;
    @Override
    public String getSubMchIdByOutTradeNo(String outTradeNo) {
        // 先从订单中获取设备ID，然后从设备中获取代理商ID
        String subMchId = orderApi.getStoreSubMchIdByOutTradNo(outTradeNo);
        return subMchId;
    }

    @Override
    public String getSubMchIdByClientUserId(String clientUserId) {
        if (ObjectUtil.isEmpty(clientUserId)) {
            throw new RuntimeException("用户ID不能为空");
        }
        QueryWrapper<WineAgent> wineAgentQueryWrapper = new QueryWrapper<WineAgent>().checkSqlInjection();
        wineAgentQueryWrapper.lambda().eq(WineAgent::getClientUserId, clientUserId);
        WineAgent wineAgent = wineAgentService.getOne(wineAgentQueryWrapper);
        if (ObjectUtil.isEmpty(wineAgent)) {
            throw new RuntimeException("代理商信息不存在，用户ID：" + clientUserId);
        }
        if (ObjectUtil.isEmpty(wineAgent.getSubMerId())) {
            throw new RuntimeException("代理商未配置子商户号，代理商ID：" + wineAgent.getId());
        }
        // 返回时清理子商户号，去除首尾空格
        return wineAgent.getSubMerId().trim();
    }

    @Override
    public String getSubMchIdByOpenId(String openId) {
        if (ObjectUtil.isEmpty(openId)) {
            throw new RuntimeException("OpenId不能为空");
        }
        
        // 通过openId获取用户ID
        String clientUserId = clientApi.getIdByOpenId(openId);
        if (ObjectUtil.isEmpty(clientUserId)) {
            throw new RuntimeException("无法获取用户信息，OpenId：" + openId);
        }
        
        // 通过用户ID获取子商户号
        return getSubMchIdByClientUserId(clientUserId);
    }

    @Override
    public String getClientUserIdBySubMchId(String subMchId) {
        QueryWrapper<WineAgent> wineAgentQueryWrapper = new QueryWrapper<WineAgent>().checkSqlInjection();
        wineAgentQueryWrapper.lambda().eq(WineAgent::getSubMerId, subMchId);
        WineAgent wineAgent = wineAgentService.getOne(wineAgentQueryWrapper);
        return wineAgent.getClientUserId();
    }

    @Override
    public BigDecimal getMaxProfitSharingRatio(String clientUserId) {
        try {
            String subMchId = getSubMchIdByClientUserId(clientUserId);
            return wineAgentService.queryMaxProfitSharingRatio(subMchId);

        } catch (Exception e) {
            log.error("从代理商配置获取分账比例异常：{}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAgentRecord(String clientUserId) {
        if (StrUtil.isEmpty(clientUserId)) {
            throw new CommonException("用户ID不能为空");
        }
        
        // 检查是否已存在代理商记录
        WineAgent existingAgent = wineAgentService.getOne(
            new LambdaQueryWrapper<WineAgent>().eq(WineAgent::getClientUserId, clientUserId)
        );
        
        if (existingAgent != null) {
            log.warn("代理商记录已存在，用户ID：{}", clientUserId);
            return;
        }
        
        // 创建新的代理商记录
        WineAgent wineAgent = new WineAgent();
        wineAgent.setClientUserId(clientUserId);
        // 生成代理商编号：AGENT + 时间戳
        wineAgent.setAgentCode("AGENT" + System.currentTimeMillis());
        
        wineAgentService.save(wineAgent);
        log.info("创建代理商记录成功，用户ID：{}，代理商编号：{}", clientUserId, wineAgent.getAgentCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAgentRecord(String clientUserId) {
        if (StrUtil.isEmpty(clientUserId)) {
            throw new CommonException("用户ID不能为空");
        }
        
        // 查找并删除代理商记录
        boolean removed = wineAgentService.remove(
            new LambdaQueryWrapper<WineAgent>().eq(WineAgent::getClientUserId, clientUserId)
        );
        
        if (removed) {
            log.info("删除代理商记录成功，用户ID：{}", clientUserId);
        } else {
            log.warn("未找到要删除的代理商记录，用户ID：{}", clientUserId);
        }
    }
}
