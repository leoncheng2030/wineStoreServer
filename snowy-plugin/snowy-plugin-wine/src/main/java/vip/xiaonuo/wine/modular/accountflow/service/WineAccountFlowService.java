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
package vip.xiaonuo.wine.modular.accountflow.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.wine.modular.accountflow.entity.WineAccountFlow;
import vip.xiaonuo.wine.modular.accountflow.param.WineAccountFlowAddParam;
import vip.xiaonuo.wine.modular.accountflow.param.WineAccountFlowEditParam;
import vip.xiaonuo.wine.modular.accountflow.param.WineAccountFlowIdParam;
import vip.xiaonuo.wine.modular.accountflow.param.WineAccountFlowPageParam;
import vip.xiaonuo.wine.modular.device.param.WineDevicePageParam;

import java.io.IOException;
import java.util.List;

/**
 * 账户流水Service接口
 *
 * @author jetox
 * @date  2025/07/24 08:56
 **/
public interface WineAccountFlowService extends IService<WineAccountFlow> {

    /**
     * 获取账户流水分页
     *
     * @author jetox
     * @date  2025/07/24 08:56
     */
    Page<WineAccountFlow> page(WineAccountFlowPageParam wineAccountFlowPageParam);

    /**
     * 添加账户流水
     *
     * @author jetox
     * @date  2025/07/24 08:56
     */
    void add(WineAccountFlowAddParam wineAccountFlowAddParam);

    /**
     * 编辑账户流水
     *
     * @author jetox
     * @date  2025/07/24 08:56
     */
    void edit(WineAccountFlowEditParam wineAccountFlowEditParam);

    /**
     * 删除账户流水
     *
     * @author jetox
     * @date  2025/07/24 08:56
     */
    void delete(List<WineAccountFlowIdParam> wineAccountFlowIdParamList);

    /**
     * 获取账户流水详情
     *
     * @author jetox
     * @date  2025/07/24 08:56
     */
    WineAccountFlow detail(WineAccountFlowIdParam wineAccountFlowIdParam);

    /**
     * 获取账户流水详情
     *
     * @author jetox
     * @date  2025/07/24 08:56
     **/
    WineAccountFlow queryEntity(String id);

    /**
     * 下载账户流水导入模板
     *
     * @author jetox
     * @date  2025/07/24 08:56
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入账户流水
     *
     * @author jetox
     * @date  2025/07/24 08:56
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出账户流水
     *
     * @author jetox
     * @date  2025/07/24 08:56
     */
    void exportData(List<WineAccountFlowIdParam> wineAccountFlowIdParamList, HttpServletResponse response) throws IOException;
}
