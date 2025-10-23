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
package vip.xiaonuo.dev.modular.district.service;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.dev.modular.district.entity.DevDistrict;
import vip.xiaonuo.dev.modular.district.param.DevDistrictAddParam;
import vip.xiaonuo.dev.modular.district.param.DevDistrictEditParam;
import vip.xiaonuo.dev.modular.district.param.DevDistrictIdParam;
import vip.xiaonuo.dev.modular.district.param.DevDistrictPageParam;
import java.io.IOException;
import java.util.List;

/**
 * 地区表Service接口
 *
 * @author jetox
 * @date  2025/08/12 07:53
 **/
public interface DevDistrictService extends IService<DevDistrict> {

    /**
     * 获取地区表分页
     *
     * @author jetox
     * @date  2025/08/12 07:53
     */
    Page<DevDistrict> page(DevDistrictPageParam devDistrictPageParam);
    /**
     * 获取城市树
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<Tree<String>> tree();
    /**
     * 添加地区表
     *
     * @author jetox
     * @date  2025/08/12 07:53
     */
    void add(DevDistrictAddParam devDistrictAddParam);

    /**
     * 编辑地区表
     *
     * @author jetox
     * @date  2025/08/12 07:53
     */
    void edit(DevDistrictEditParam devDistrictEditParam);

    /**
     * 删除地区表
     *
     * @author jetox
     * @date  2025/08/12 07:53
     */
    void delete(List<DevDistrictIdParam> devDistrictIdParamList);

    /**
     * 获取地区表详情
     *
     * @author jetox
     * @date  2025/08/12 07:53
     */
    DevDistrict detail(DevDistrictIdParam devDistrictIdParam);

    /**
     * 获取地区表详情
     *
     * @author jetox
     * @date  2025/08/12 07:53
     **/
    DevDistrict queryEntity(String id);

    /**
     * 下载地区表导入模板
     *
     * @author jetox
     * @date  2025/08/12 07:53
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入地区表
     *
     * @author jetox
     * @date  2025/08/12 07:53
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出地区表
     *
     * @author jetox
     * @date  2025/08/12 07:53
     */
    void exportData(List<DevDistrictIdParam> devDistrictIdParamList, HttpServletResponse response) throws IOException;
}
