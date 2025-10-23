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
package vip.xiaonuo.wine.modular.useraccount.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.wine.modular.useraccount.entity.WineUserAccount;
import vip.xiaonuo.wine.modular.useraccount.param.WineUserAccountAddParam;
import vip.xiaonuo.wine.modular.useraccount.param.WineUserAccountEditParam;
import vip.xiaonuo.wine.modular.useraccount.param.WineUserAccountIdParam;
import vip.xiaonuo.wine.modular.useraccount.param.WineUserAccountPageParam;
import java.io.IOException;
import java.util.List;

/**
 * 账户列表Service接口
 *
 * @author jetox
 * @date  2025/07/24 08:55
 **/
public interface WineUserAccountService extends IService<WineUserAccount> {

    /**
     * 获取账户列表分页
     *
     * @author jetox
     * @date  2025/07/24 08:55
     */
    Page<WineUserAccount> page(WineUserAccountPageParam wineUserAccountPageParam);

    /**
     * 添加账户列表
     *
     * @author jetox
     * @date  2025/07/24 08:55
     */
    void add(WineUserAccountAddParam wineUserAccountAddParam);

    /**
     * 编辑账户列表
     *
     * @author jetox
     * @date  2025/07/24 08:55
     */
    void edit(WineUserAccountEditParam wineUserAccountEditParam);

    /**
     * 删除账户列表
     *
     * @author jetox
     * @date  2025/07/24 08:55
     */
    void delete(List<WineUserAccountIdParam> wineUserAccountIdParamList);

    /**
     * 获取账户列表详情
     *
     * @author jetox
     * @date  2025/07/24 08:55
     */
    WineUserAccount detail(WineUserAccountIdParam wineUserAccountIdParam);

    /**
     * 根据用户ID获取详情
     *
     * @author jetox
     * @date  2025/07/24 08:55
     */
    WineUserAccount getUserInfo(String userId);

    /**
     * 获取账户列表详情
     *
     * @author jetox
     * @date  2025/07/24 08:55
     **/
    WineUserAccount queryEntity(String id);

    /**
     * 下载账户列表导入模板
     *
     * @author jetox
     * @date  2025/07/24 08:55
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入账户列表
     *
     * @author jetox
     * @date  2025/07/24 08:55
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出账户列表
     *
     * @author jetox
     * @date  2025/07/24 08:55
     */
    void exportData(List<WineUserAccountIdParam> wineUserAccountIdParamList, HttpServletResponse response) throws IOException;

    WineUserAccount getAccountInfo(String id);
}
