package tbc.uncagedmist.sarkaricloud.Service;

import java.util.List;

import tbc.uncagedmist.sarkaricloud.Model.Banner;

public interface IBannerLoadListener {

    void onBannerLoadSuccess(List<Banner> banners);
    void onBannerLoadFailed(String message);
}