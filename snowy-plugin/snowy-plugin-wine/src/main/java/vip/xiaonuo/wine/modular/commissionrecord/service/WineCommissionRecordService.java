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
package vip.xiaonuo.wine.modular.commissionrecord.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.wine.modular.commissionrecord.entity.WineCommissionRecord;
import vip.xiaonuo.wine.modular.commissionrecord.param.WineCommissionRecordAddParam;
import vip.xiaonuo.wine.modular.commissionrecord.param.WineCommissionRecordEditParam;
import vip.xiaonuo.wine.modular.commissionrecord.param.WineCommissionRecordIdParam;
import vip.xiaonuo.wine.modular.commissionrecord.param.WineCommissionRecordPageParam;
import java.io.IOException;
import java.util.List;

/**
 * 佣金记录表Service接口
 *
 * @author jetox
 * @date  2025/07/24 08:50
 **/
public interface WineCommissionRecordService extends IService<WineCommissionRecord> {

    /**
     * 获取佣金记录表分页
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    Page<WineCommissionRecord> page(WineCommissionRecordPageParam wineCommissionRecordPageParam);

    /**
     * 添加佣金记录表
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    void add(WineCommissionRecordAddParam wineCommissionRecordAddParam);

    /**
     * 编辑佣金记录表
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    void edit(WineCommissionRecordEditParam wineCommissionRecordEditParam);

    /**
     * 删除佣金记录表
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    void delete(List<WineCommissionRecordIdParam> wineCommissionRecordIdParamList);

    /**
     * 获取佣金记录表详情
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    WineCommissionRecord detail(WineCommissionRecordIdParam wineCommissionRecordIdParam);

    /**
     * 获取佣金记录表详情
     *
     * @author jetox
     * @date  2025/07/24 08:50
     **/
    WineCommissionRecord queryEntity(String id);

    /**
     * 下载佣金记录表导入模板
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入佣金记录表
     *
     * @author jetox
     * @date  2025/07/24 08:50
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出佣金记录表
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    void exportData(List<WineCommissionRecordIdParam> wineCommissionRecordIdParamList, HttpServletResponse response) throws IOException;
}
