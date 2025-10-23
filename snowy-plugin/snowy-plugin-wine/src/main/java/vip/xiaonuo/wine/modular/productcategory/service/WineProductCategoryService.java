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
package vip.xiaonuo.wine.modular.productcategory.service;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.wine.modular.productcategory.entity.WineProductCategory;
import vip.xiaonuo.wine.modular.productcategory.param.WineProductCategoryAddParam;
import vip.xiaonuo.wine.modular.productcategory.param.WineProductCategoryEditParam;
import vip.xiaonuo.wine.modular.productcategory.param.WineProductCategoryIdParam;
import vip.xiaonuo.wine.modular.productcategory.param.WineProductCategoryPageParam;
import java.io.IOException;
import java.util.List;

/**
 * 酒品分类表Service接口
 *
 * @author jetox
 * @date  2025/07/24 08:11
 **/
public interface WineProductCategoryService extends IService<WineProductCategory> {

    /**
     * 获取酒品分类表分页
     *
     * @author jetox
     * @date  2025/07/24 08:11
     */
    Page<WineProductCategory> page(WineProductCategoryPageParam wineProductCategoryPageParam);

    /**
     * 酒品分类树
     *
     * @author jetox
     * @date  2025/07/24 08:11
     */
    List<Tree<String>> tree();

    /**
     * 添加酒品分类表
     *
     * @author jetox
     * @date  2025/07/24 08:11
     */
    void add(WineProductCategoryAddParam wineProductCategoryAddParam);

    /**
     * 编辑酒品分类表
     *
     * @author jetox
     * @date  2025/07/24 08:11
     */
    void edit(WineProductCategoryEditParam wineProductCategoryEditParam);

    /**
     * 删除酒品分类表
     *
     * @author jetox
     * @date  2025/07/24 08:11
     */
    void delete(List<WineProductCategoryIdParam> wineProductCategoryIdParamList);

    /**
     * 获取酒品分类表详情
     *
     * @author jetox
     * @date  2025/07/24 08:11
     */
    WineProductCategory detail(WineProductCategoryIdParam wineProductCategoryIdParam);

    /**
     * 获取酒品分类表详情
     *
     * @author jetox
     * @date  2025/07/24 08:11
     **/
    WineProductCategory queryEntity(String id);

    /**
     * 下载酒品分类表导入模板
     *
     * @author jetox
     * @date  2025/07/24 08:11
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入酒品分类表
     *
     * @author jetox
     * @date  2025/07/24 08:11
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出酒品分类表
     *
     * @author jetox
     * @date  2025/07/24 08:11
     */
    void exportData(List<WineProductCategoryIdParam> wineProductCategoryIdParamList, HttpServletResponse response) throws IOException;
}
