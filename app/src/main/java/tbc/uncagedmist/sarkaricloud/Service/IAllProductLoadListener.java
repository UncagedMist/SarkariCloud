package tbc.uncagedmist.sarkaricloud.Service;

import java.util.List;

import tbc.uncagedmist.sarkaricloud.Model.Service;

public interface IAllProductLoadListener {
    void onAllProductLoadSuccess(List<Service> allProductList);
    void onAllProductLoadFailed(String message);
}