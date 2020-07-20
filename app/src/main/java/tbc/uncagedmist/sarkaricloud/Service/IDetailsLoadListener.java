package tbc.uncagedmist.sarkaricloud.Service;

import java.util.List;

import tbc.uncagedmist.sarkaricloud.Model.Detail;

public interface IDetailsLoadListener {

    void onDetailLoadSuccess(List<Detail> details);
    void onDetailLoadFailed(String message);
}