package com.yolesdk.sdk.callback;

import com.yolesdk.sdk.data.InitSdkData;

public interface InitCallBackFunction {

    public void success(InitSdkData info);
    public void fail(String info);
}
