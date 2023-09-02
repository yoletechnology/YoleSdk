package com.yolesdk.sdk.tool;

import java.util.HashMap;
import java.util.Map;

public class PayInfo {
    public Map<String,String> data = new HashMap<>();
    public void addData(String Key,String value)
    {
        data.put(Key,value);
    }
}
