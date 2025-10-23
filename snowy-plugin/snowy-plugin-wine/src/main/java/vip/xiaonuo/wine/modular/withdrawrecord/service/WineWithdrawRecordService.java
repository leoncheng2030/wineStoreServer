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
package vip.xiaonuo.wine.modular.withdrawrecord.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.wine.modular.withdrawrecord.entity.WineWithdrawRecord;
import vip.xiaonuo.wine.modular.withdrawrecord.param.*;

import java.io.IOException;
import java.util.List;

/**
 * 提现记录表Service接口
 *
 * @author jetox
 * @date  2025/07/24 08:50
 **/
public interface WineWithdrawRecordService extends IService<WineWithdrawRecord> {

    /**
     * 获取提现记录表分页
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    Page<WineWithdrawRecord> page(WineWithdrawRecordPageParam wineWithdrawRecordPageParam);

    /**
     * 添加提现记录表
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    void add(WineWithdrawRecordAddParam wineWithdrawRecordAddParam);

    /**
     * 编辑提现记录表
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    void edit(WineWithdrawRecordEditParam wineWithdrawRecordEditParam);

    /**
     * 删除提现记录表
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    void delete(List<WineWithdrawRecordIdParam> wineWithdrawRecordIdParamList);

    /**
     * 获取提现记录表详情
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    WineWithdrawRecord detail(WineWithdrawRecordIdParam wineWithdrawRecordIdParam);

    /**
     * 获取提现记录表详情
     *
     * @author jetox
     * @date  2025/07/24 08:50
     **/
    WineWithdrawRecord queryEntity(String id);

    /**
     * 下载提现记录表导入模板
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入提现记录表
     *
     * @author jetox
     * @date  2025/07/24 08:50
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出提现记录表
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    void exportData(List<WineWithdrawRecordIdParam> wineWithdrawRecordIdParamList, HttpServletResponse response) throws IOException;

    /**
     * 提现审核
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    void approveWithdraw(WineWithdrawRecordIdParam wineWithdrawRecordIdParam);

    void rejectWithdraw(WineWithdrawRecordRejectWithdrawParam wineWithdrawRecordRejectWithdrawParam);
}
