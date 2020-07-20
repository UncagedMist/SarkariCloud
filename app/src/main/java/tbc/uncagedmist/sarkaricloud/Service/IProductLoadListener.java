package tbc.uncagedmist.sarkaricloud.Service;

import java.util.List;

import tbc.uncagedmist.sarkaricloud.Model.Product;

public interface IProductLoadListener {

    void onProductLoadSuccess(List<Product> products);
    void onProductLoadFailed(String message);
}
