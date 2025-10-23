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
package vip.xiaonuo.wine.modular.store.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.wine.modular.store.entity.WineStore;
import vip.xiaonuo.wine.modular.store.entity.WineStoreUser;
import vip.xiaonuo.wine.modular.store.param.*;

import java.io.IOException;
import java.util.List;

/**
 * 门店管理表Service接口
 *
 * @author jetox
 * @date  2025/07/24 08:01
 **/
public interface WineStoreService extends IService<WineStore> {

    /**
     * 获取门店管理表分页
     *
     * @author jetox
     * @date  2025/07/24 08:01
     */
    Page<WineStore> page(WineStorePageParam wineStorePageParam);

    /**
     * 根据ids获取门店列表
     * @param wineStoreIdListParam
     * @return
     */
    List<WineStore> idList(WineStoreIdListParam wineStoreIdListParam);
    /**
     * 添加门店管理表
     *
     * @author jetox
     * @date  2025/07/24 08:01
     */
    void add(WineStoreAddParam wineStoreAddParam);

    /**
     * 编辑门店管理表
     *
     * @author jetox
     * @date  2025/07/24 08:01
     */
    void edit(WineStoreEditParam wineStoreEditParam);

    /**
     * 删除门店管理表
     *
     * @author jetox
     * @date  2025/07/24 08:01
     */
    void delete(List<WineStoreIdParam> wineStoreIdParamList);

    /**
     * 获取门店管理表详情
     *
     * @author jetox
     * @date  2025/07/24 08:01
     */
    WineStore detail(WineStoreIdParam wineStoreIdParam);

    /**
     * 获取门店管理表详情
     *
     * @author jetox
     * @date  2025/07/24 08:01
     **/
    WineStore queryEntity(String id);

    /**
     * 下载门店管理表导入模板
     *
     * @author jetox
     * @date  2025/07/24 08:01
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入门店管理表
     *
     * @author jetox
     * @date  2025/07/24 08:01
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出门店管理表
     *
     * @author jetox
     * @date  2025/07/24 08:01
     */
    void exportData(List<WineStoreIdParam> wineStoreIdParamList, HttpServletResponse response) throws IOException;
    /**
     * 获取附近门店
     *
     * @author jetox
     * @date  2025/07/24 08:01
     */
    Page<WineStore> nearby(WineStoreNearbyParam wineStoreNearbyParam);

    /**
     * 获取店员表列表
     *
     * @author jetox
     * @date  2025/08/20 19:08
     */
    List<WineStoreUser> list(WineStoreIdParam wineStoreIdParam);

}
