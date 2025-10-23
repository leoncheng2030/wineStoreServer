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
package vip.xiaonuo.wine.modular.spec.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.wine.modular.spec.entity.WineSpec;
import vip.xiaonuo.wine.modular.spec.param.WineSpecAddParam;
import vip.xiaonuo.wine.modular.spec.param.WineSpecEditParam;
import vip.xiaonuo.wine.modular.spec.param.WineSpecIdParam;
import vip.xiaonuo.wine.modular.spec.param.WineSpecPageParam;
import java.io.IOException;
import java.util.List;

/**
 * 规格Service接口
 *
 * @author jetox
 * @date  2025/09/28 19:06
 **/
public interface WineSpecService extends IService<WineSpec> {

    /**
     * 获取规格分页
     *
     * @author jetox
     * @date  2025/09/28 19:06
     */
    Page<WineSpec> page(WineSpecPageParam wineSpecPageParam);

    /**
     * 添加规格
     *
     * @author jetox
     * @date  2025/09/28 19:06
     */
    void add(WineSpecAddParam wineSpecAddParam);

    /**
     * 编辑规格
     *
     * @author jetox
     * @date  2025/09/28 19:06
     */
    void edit(WineSpecEditParam wineSpecEditParam);

    /**
     * 删除规格
     *
     * @author jetox
     * @date  2025/09/28 19:06
     */
    void delete(List<WineSpecIdParam> wineSpecIdParamList);

    /**
     * 获取规格详情
     *
     * @author jetox
     * @date  2025/09/28 19:06
     */
    WineSpec detail(WineSpecIdParam wineSpecIdParam);

    /**
     * 获取规格详情
     *
     * @author jetox
     * @date  2025/09/28 19:06
     **/
    WineSpec queryEntity(String id);

    /**
     * 下载规格导入模板
     *
     * @author jetox
     * @date  2025/09/28 19:06
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入规格
     *
     * @author jetox
     * @date  2025/09/28 19:06
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出规格
     *
     * @author jetox
     * @date  2025/09/28 19:06
     */
    void exportData(List<WineSpecIdParam> wineSpecIdParamList, HttpServletResponse response) throws IOException;
}
