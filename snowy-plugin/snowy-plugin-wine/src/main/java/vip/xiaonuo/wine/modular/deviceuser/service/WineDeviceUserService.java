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
package vip.xiaonuo.wine.modular.deviceuser.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.wine.modular.deviceuser.entity.WineDeviceUser;
import vip.xiaonuo.wine.modular.deviceuser.param.*;
import vip.xiaonuo.wine.modular.deviceuser.vo.WineDeviceUserVO;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * 分佣配置Service接口
 *
 * @author jetox
 * @date  2025/07/24 08:44
 **/
public interface WineDeviceUserService extends IService<WineDeviceUser> {

    /**
     * 获取分佣配置分页
     *
     * @author jetox
     * @date  2025/07/24 08:44
     */
    Page<WineDeviceUser> page(WineDeviceUserPageParam wineDeviceUserPageParam);

    /**
     * 添加分佣配置
     *
     * @author jetox
     * @date  2025/07/24 08:44
     */
    void add(WineDeviceUserAddParam wineDeviceUserAddParam);

    /**
     * 编辑分佣配置
     *
     * @author jetox
     * @date  2025/07/24 08:44
     */
    void edit(WineDeviceUserEditParam wineDeviceUserEditParam);

    /**
     * 删除分佣配置
     *
     * @author jetox
     * @date  2025/07/24 08:44
     */
    void delete(List<WineDeviceUserIdParam> wineDeviceUserIdParamList);

    /**
     * 获取分佣配置详情
     *
     * @author jetox
     * @date  2025/07/24 08:44
     */
    WineDeviceUser detail(WineDeviceUserIdParam wineDeviceUserIdParam);

    /**
     * 获取分佣配置详情
     *
     * @author jetox
     * @date  2025/07/24 08:44
     **/
    WineDeviceUser queryEntity(String id);

    /**
     * 下载分佣配置导入模板
     *
     * @author jetox
     * @date  2025/07/24 08:44
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入分佣配置
     *
     * @author jetox
     * @date  2025/07/24 08:44
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出分佣配置
     *
     * @author jetox
     * @date  2025/07/24 08:44
     */
    void exportData(List<WineDeviceUserIdParam> wineDeviceUserIdParamList, HttpServletResponse response) throws IOException;
    /**
     * 设置佣金
     *
     * @author jetox
     * @date  2025/07/24 08:44
     */
    void setCommission(WineDeviceUserCommissionRateParam wineDeviceUserCommissionRateParam);
    
    /**
     * 根据用户ID和设备ID查询佣金比例
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 佣金比例，如果未找到则返回null
     */
    BigDecimal getCommissionRate(String userId, String deviceId);
    
    /**
     * 根据设备ID查询所有关联的用户及其佣金比例
     * @param deviceId 设备ID
     * @return 设备关联的所有用户及佣金比例列表
     */
    List<WineDeviceUser> getDeviceUsers(String deviceId);
    
    /**
     * 根据设备ID查询所有关联的用户及其佣金比例（包含用户详细信息）
     * @param deviceId 设备ID
     * @return 设备关联的所有用户及佣金比例列表（包含用户详细信息）
     */
    List<WineDeviceUserVO> getDeviceUsersWithUserInfo(String deviceId);
    
    /**
     * 解绑设备用户
     * @param deviceId 设备ID
     * @param userId 用户ID
     */
    void unbindUser(String deviceId, String userId);
    
    /**
     * 绑定设备用户角色（新方法）
     * @param wineDeviceUserBindRoleParam 绑定角色参数
     */
    void bindUserRole(WineDeviceUserBindRoleParam wineDeviceUserBindRoleParam);
    
    /**
     * 编辑设备用户角色和佣金
     * @param wineDeviceUserEditRoleParam 编辑参数
     */
    void editUserRole(WineDeviceUserEditRoleParam wineDeviceUserEditRoleParam);
    
    /**
     * 根据设备ID和角色编码查询用户列表
     * @param deviceId 设备ID
     * @param roleCode 角色编码
     * @return 用户列表
     */
    List<WineDeviceUser> getUsersByDeviceAndRole(String deviceId, String roleCode);
    
    /**
     * 根据用户ID和设备ID查询用户角色
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 角色编码列表
     */
    List<String> getUserRolesForDevice(String userId, String deviceId);
    
    /**
     * 检查用户在设备上是否有指定角色
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param roleCode 角色编码
     * @return 是否有该角色
     */
    boolean hasRole(String userId, String deviceId, String roleCode);
    
    /**
     * 根据用户ID和角色编码获取设备ID列表
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return 设备ID列表
     */
    List<String> getDeviceIdsByUserRole(String userId, String roleCode);
    
    /**
     * 根据设备ID和角色编码获取用户ID列表
     * @param deviceId 设备ID
     * @param roleCode 角色编码
     * @return 用户ID列表
     */
    List<String> getUserIdsByDeviceRole(String deviceId, String roleCode);
    
    /**
     * 检查用户是否有设备管理权限（通过角色验证）
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param roleCode 角色编码
     * @return 是否有权限
     */
    boolean hasUserRole(String userId, String deviceId, String roleCode);
    
    /**
     * 检查设备是否已有指定角色的用户
     * @param deviceId 设备ID
     * @param roleCode 角色编码
     * @return 是否存在该角色的用户
     */
    boolean hasUserWithRole(String deviceId, String roleCode);
}
