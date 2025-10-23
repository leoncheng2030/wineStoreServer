package vip.xiaonuo.wine.modular.store.provider;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import vip.xiaonuo.wine.api.WineStoreAPI;
import vip.xiaonuo.wine.modular.store.entity.WineStore;
import vip.xiaonuo.wine.modular.store.service.WineStoreService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WineStoreProvider implements WineStoreAPI {
    @Resource
    private WineStoreService wineStoreService;
    
    @Override
    public List<String> getStoreIdsByStoreManagerId(String userId) {
        QueryWrapper<WineStore> wineStoreQueryWrapper = new QueryWrapper<WineStore>().checkSqlInjection();
        wineStoreQueryWrapper.lambda().eq(WineStore::getStoreManagerId, userId);
        return wineStoreService.list(wineStoreQueryWrapper).stream().map(WineStore::getId).toList();
    }
    
    @Override
    public Map<String, Object> getStoreAddressById(String storeId) {
        Map<String, Object> result = new HashMap<>();
        
        if (StrUtil.isBlank(storeId)) {
            return result;
        }
        
        try {
            WineStore store = wineStoreService.getById(storeId);
            if (store != null) {
                result.put("storeId", store.getId());
                result.put("storeName", store.getStoreName());
                result.put("province", store.getProvince());
                result.put("city", store.getCity());
                result.put("district", store.getDistrict());
                result.put("detailAddress", store.getDetailAddress());
                
                // 组装完整地址
                StringBuilder fullAddress = new StringBuilder();
                if (StrUtil.isNotBlank(store.getProvince())) {
                    fullAddress.append(store.getProvince());
                }
                if (StrUtil.isNotBlank(store.getCity())) {
                    fullAddress.append(store.getCity());
                }
                if (StrUtil.isNotBlank(store.getDistrict())) {
                    fullAddress.append(store.getDistrict());
                }
                if (StrUtil.isNotBlank(store.getDetailAddress())) {
                    fullAddress.append(store.getDetailAddress());
                }
                
                result.put("fullAddress", fullAddress.toString());
            }
        } catch (Exception e) {
            // 日志记录错误，但不抛出异常，返回空结果
            // 这里可以添加日志记录逻辑
        }
        
        return result;
    }
}
