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
package vip.xiaonuo.wine.modular.devicerole.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.wine.modular.devicerole.entity.WineDeviceRole;
import vip.xiaonuo.wine.modular.devicerole.param.WineDeviceRoleAddParam;
import vip.xiaonuo.wine.modular.devicerole.param.WineDeviceRoleEditParam;
import vip.xiaonuo.wine.modular.devicerole.param.WineDeviceRoleIdParam;
import vip.xiaonuo.wine.modular.devicerole.param.WineDeviceRolePageParam;
import java.io.IOException;
import java.util.List;

/**
 * 设备角色定义表Service接口
 *
 * @author jetox
 * @date  2025/09/21 09:16
 **/
public interface WineDeviceRoleService extends IService<WineDeviceRole> {

    /**
     * 获取设备角色定义表分页
     *
     * @author jetox
     * @date  2025/09/21 09:16
     */
    Page<WineDeviceRole> page(WineDeviceRolePageParam wineDeviceRolePageParam);

    /**
     * 添加设备角色定义表
     *
     * @author jetox
     * @date  2025/09/21 09:16
     */
    void add(WineDeviceRoleAddParam wineDeviceRoleAddParam);

    /**
     * 编辑设备角色定义表
     *
     * @author jetox
     * @date  2025/09/21 09:16
     */
    void edit(WineDeviceRoleEditParam wineDeviceRoleEditParam);

    /**
     * 删除设备角色定义表
     *
     * @author jetox
     * @date  2025/09/21 09:16
     */
    void delete(List<WineDeviceRoleIdParam> wineDeviceRoleIdParamList);

    /**
     * 获取设备角色定义表详情
     *
     * @author jetox
     * @date  2025/09/21 09:16
     */
    WineDeviceRole detail(WineDeviceRoleIdParam wineDeviceRoleIdParam);

    /**
     * 获取设备角色定义表详情
     *
     * @author jetox
     * @date  2025/09/21 09:16
     **/
    WineDeviceRole queryEntity(String id);

    /**
     * 下载设备角色定义表导入模板
     *
     * @author jetox
     * @date  2025/09/21 09:16
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入设备角色定义表
     *
     * @author jetox
     * @date  2025/09/21 09:16
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出设备角色定义表
     *
     * @author jetox
     * @date  2025/09/21 09:16
     */
    void exportData(List<WineDeviceRoleIdParam> wineDeviceRoleIdParamList, HttpServletResponse response) throws IOException;
}
