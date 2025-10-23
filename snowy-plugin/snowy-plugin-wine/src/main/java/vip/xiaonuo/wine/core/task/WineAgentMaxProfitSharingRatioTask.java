package vip.xiaonuo.wine.core.task;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vip.xiaonuo.common.timer.CommonTimerTaskRunner;
import vip.xiaonuo.pay.api.PayApi;
import vip.xiaonuo.wine.modular.agent.entity.WineAgent;
import vip.xiaonuo.wine.modular.agent.service.WineAgentService;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
public class WineAgentMaxProfitSharingRatioTask  implements CommonTimerTaskRunner {
    @Resource
    private WineAgentService wineAgentService;
    @Resource
    private PayApi payApi;
    @Override
    public void action(String extJson) {
        List<WineAgent> list = wineAgentService.list();
        for (WineAgent wineAgent : list) {
            if (wineAgent.getSubMerId()!=null){
                BigDecimal maxProfitSharingRatio = payApi.queryMaxProfitSharingRatio(wineAgent.getSubMerId());
                wineAgent.setProfitSharingMaxRate(maxProfitSharingRatio);
                wineAgentService.updateById(wineAgent);
            }
        }
    }
}
