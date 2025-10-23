package vip.xiaonuo.wine.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import vip.xiaonuo.wine.param.ProductListParam;

public interface ProductApi {
    public Page<Object> getProductList(ProductListParam productListParam);
}
