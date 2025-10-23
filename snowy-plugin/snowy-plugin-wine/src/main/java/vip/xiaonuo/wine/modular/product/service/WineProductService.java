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
package vip.xiaonuo.wine.modular.product.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.wine.modular.product.entity.WineProduct;
import vip.xiaonuo.wine.modular.product.param.*;

import java.io.IOException;
import java.util.List;

/**
 * 酒品列表Service接口
 *
 * @author jetox
 * @date  2025/07/24 08:20
 **/
public interface WineProductService extends IService<WineProduct> {

    /**
     * 获取酒品列表分页
     *
     * @author jetox
     * @date  2025/07/24 08:20
     */
    Page<WineProduct> page(WineProductPageParam wineProductPageParam);

    List<WineProduct> idList(WineProductIdListParam wineProductIdListParam);

    /**
     * 添加酒品列表
     *
     * @author jetox
     * @date  2025/07/24 08:20
     */
    void add(WineProductAddParam wineProductAddParam);

    /**
     * 编辑酒品列表
     *
     * @author jetox
     * @date  2025/07/24 08:20
     */
    void edit(WineProductEditParam wineProductEditParam);

    /**
     * 删除酒品列表
     *
     * @author jetox
     * @date  2025/07/24 08:20
     */
    void delete(List<WineProductIdParam> wineProductIdParamList);

    /**
     * 获取酒品列表详情
     *
     * @author jetox
     * @date  2025/07/24 08:20
     */
    WineProduct detail(WineProductIdParam wineProductIdParam);

    /**
     * 获取酒品列表详情
     *
     * @author jetox
     * @date  2025/07/24 08:20
     **/
    WineProduct queryEntity(String id);

    /**
     * 下载酒品列表导入模板
     *
     * @author jetox
     * @date  2025/07/24 08:20
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入酒品列表
     *
     * @author jetox
     * @date  2025/07/24 08:20
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出酒品列表
     *
     * @author jetox
     * @date  2025/07/24 08:20
     */
    void exportData(List<WineProductIdParam> wineProductIdParamList, HttpServletResponse response) throws IOException;
}
