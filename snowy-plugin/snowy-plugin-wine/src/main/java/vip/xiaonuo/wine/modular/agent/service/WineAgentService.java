/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.wine.modular.agent.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.wine.modular.agent.dto.WineAgentDetailDto;
import vip.xiaonuo.wine.modular.agent.entity.WineAgent;
import vip.xiaonuo.wine.modular.agent.param.WineAgentAddParam;
import vip.xiaonuo.wine.modular.agent.param.WineAgentEditParam;
import vip.xiaonuo.wine.modular.agent.param.WineAgentIdParam;
import vip.xiaonuo.wine.modular.agent.param.WineAgentPageParam;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * wine_agentService接口
 *
 * @author jetox
 * @date  2025/09/19 07:13
 **/
public interface WineAgentService extends IService<WineAgent> {

    /**
     * 获取wine_agent分页
     *
     * @author jetox
     * @date  2025/09/19 07:13
     */
    Page<WineAgent> page(WineAgentPageParam wineAgentPageParam);

    /**
     * 添加wine_agent
     *
     * @author jetox
     * @date  2025/09/19 07:13
     */
    void add(WineAgentAddParam wineAgentAddParam);

    /**
     * 编辑wine_agent
     *
     * @author jetox
     * @date  2025/09/19 07:13
     */
    void edit(WineAgentEditParam wineAgentEditParam);

    /**
     * 删除wine_agent
     *
     * @author jetox
     * @date  2025/09/19 07:13
     */
    void delete(List<WineAgentIdParam> wineAgentIdParamList);

    /**
     * 获取wine_agent详情
     *
     * @author jetox
     * @date  2025/09/19 07:13
     */
    WineAgent detail(WineAgentIdParam wineAgentIdParam);

    /**
     * 获取wine_agent详情
     *
     * @author jetox
     * @date  2025/09/19 07:13
     **/
    WineAgent queryEntity(String id);

    /**
     * 下载wine_agent导入模板
     *
     * @author jetox
     * @date  2025/09/19 07:13
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入wine_agent
     *
     * @author jetox
     * @date  2025/09/19 07:13
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出wine_agent
     *
     * @author jetox
     * @date  2025/09/19 07:13
     */
    void exportData(List<WineAgentIdParam> wineAgentIdParamList, HttpServletResponse response) throws IOException;

    BigDecimal queryMaxProfitSharingRatio(String subMchId);

    /**
     * 根据用户ID获取代理商信息
     *
     * @author lingming
     * @date  2025/09/30 14:30
     */
    WineAgent detailByUserId(String userId);

    /**
     * 根据用户ID获取代理商详情（包含申请信息）
     *
     * @author lingming
     * @date  2025/09/30 14:30
     */
    WineAgentDetailDto getAgentDetailWithApplyInfo(String userId);

    void setSubMerId(String clientUserId, String subMerId);
}
