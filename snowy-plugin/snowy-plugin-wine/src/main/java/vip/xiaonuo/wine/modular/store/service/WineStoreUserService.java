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
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.wine.modular.store.entity.WineStoreUser;
import vip.xiaonuo.wine.modular.store.param.WineStoreUserAddParam;
import vip.xiaonuo.wine.modular.store.param.WineStoreUserEditParam;
import vip.xiaonuo.wine.modular.store.param.WineStoreUserIdParam;
import vip.xiaonuo.wine.modular.store.param.WineStoreUserPageParam;
import java.io.IOException;
import java.util.List;

/**
 * 店员表Service接口
 *
 * @author jetox
 * @date  2025/08/20 19:08
 **/
public interface WineStoreUserService extends IService<WineStoreUser> {

    /**
     * 获取店员表分页
     *
     * @author jetox
     * @date  2025/08/20 19:08
     */
    Page<WineStoreUser> page(WineStoreUserPageParam wineStoreUserPageParam);

    /**
     * 添加店员表
     *
     * @author jetox
     * @date  2025/08/20 19:08
     */
    void add(WineStoreUserAddParam wineStoreUserAddParam);

    /**
     * 编辑店员表
     *
     * @author jetox
     * @date  2025/08/20 19:08
     */
    void edit(WineStoreUserEditParam wineStoreUserEditParam);

    /**
     * 删除店员表
     *
     * @author jetox
     * @date  2025/08/20 19:08
     */
    void delete(List<WineStoreUserIdParam> wineStoreUserIdParamList);

    /**
     * 获取店员表详情
     *
     * @author jetox
     * @date  2025/08/20 19:08
     */
    WineStoreUser detail(WineStoreUserIdParam wineStoreUserIdParam);

    /**
     * 获取店员表详情
     *
     * @author jetox
     * @date  2025/08/20 19:08
     **/
    WineStoreUser queryEntity(String id);

    /**
     * 下载店员表导入模板
     *
     * @author jetox
     * @date  2025/08/20 19:08
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入店员表
     *
     * @author jetox
     * @date  2025/08/20 19:08
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出店员表
     *
     * @author jetox
     * @date  2025/08/20 19:08
     */
    void exportData(List<WineStoreUserIdParam> wineStoreUserIdParamList, HttpServletResponse response) throws IOException;

    /**
     * 成为店员
     *
     * @author jetox
     * @date  2025/08/20 19:08
     */
    void becomeClerk(String storeId, String clientUserId);
    /**
     * 移除店员
     *
     * @author jetox
     * @date  2025/08/20 19:08
     */
    void removeClerk(String storeId, String clientUserId);
}
