package com.yolesdk.sdk.data;

import java.util.HashMap;
import java.util.Map;

public class PayInfo {
    public Map<String,String> data = new HashMap<>();
    public void addData(String Key,String value)
    {
        data.put(Key,value);
    }
}
