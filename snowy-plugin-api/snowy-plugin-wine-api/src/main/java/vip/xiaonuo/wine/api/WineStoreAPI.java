package vip.xiaonuo.wine.api;

import java.util.List;
import java.util.Map;

public interface WineStoreAPI {

    List<String> getStoreIdsByStoreManagerId(String userId);
    
    /**
     * 根据门店ID获取门店地址信息
     *
     * @param storeId 门店ID
     * @return 地址信息 Map，包含 province, city, district, detailAddress, storeName 等字段
     */
    Map<String, Object> getStoreAddressById(String storeId);
}
