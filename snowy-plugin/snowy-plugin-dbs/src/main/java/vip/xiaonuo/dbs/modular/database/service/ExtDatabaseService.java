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
package vip.xiaonuo.dbs.modular.database.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.dbs.modular.database.entity.ExtDatabase;
import vip.xiaonuo.dbs.modular.database.param.ExtDatabaseAddParam;
import vip.xiaonuo.dbs.modular.database.param.ExtDatabaseEditParam;
import vip.xiaonuo.dbs.modular.database.param.ExtDatabaseIdParam;
import vip.xiaonuo.dbs.modular.database.param.ExtDatabasePageParam;
import java.io.IOException;
import java.util.List;

/**
 * 数据源Service接口
 *
 * @author jetox
 * @date  2025/07/28 00:35
 **/
public interface ExtDatabaseService extends IService<ExtDatabase> {

    /**
     * 获取数据源分页
     *
     * @author jetox
     * @date  2025/07/28 00:35
     */
    Page<ExtDatabase> page(ExtDatabasePageParam extDatabasePageParam);

    /**
     * 添加数据源
     *
     * @author jetox
     * @date  2025/07/28 00:35
     */
    void add(ExtDatabaseAddParam extDatabaseAddParam);

    /**
     * 编辑数据源
     *
     * @author jetox
     * @date  2025/07/28 00:35
     */
    void edit(ExtDatabaseEditParam extDatabaseEditParam);

    /**
     * 删除数据源
     *
     * @author jetox
     * @date  2025/07/28 00:35
     */
    void delete(List<ExtDatabaseIdParam> extDatabaseIdParamList);

    /**
     * 获取数据源详情
     *
     * @author jetox
     * @date  2025/07/28 00:35
     */
    ExtDatabase detail(ExtDatabaseIdParam extDatabaseIdParam);

    /**
     * 获取数据源详情
     *
     * @author jetox
     * @date  2025/07/28 00:35
     **/
    ExtDatabase queryEntity(String id);

    /**
     * 下载数据源导入模板
     *
     * @author jetox
     * @date  2025/07/28 00:35
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入数据源
     *
     * @author jetox
     * @date  2025/07/28 00:35
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出数据源
     *
     * @author jetox
     * @date  2025/07/28 00:35
     */
    void exportData(List<ExtDatabaseIdParam> extDatabaseIdParamList, HttpServletResponse response) throws IOException;
}
