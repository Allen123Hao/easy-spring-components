package com.allen.component.common.global;

import cn.hutool.json.JSONUtil;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Allen
 * @version 1.0
 * @since 2023/7/5 10:36
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalVariables {

    //用于获取当前对象的Key
    public static final String KEY = "globalVariables";

    private String traceId;

    private String userUuid;

    private String courseUuid;

    private String appVersion;

    private String platform;

    private String remoteIP;

    private String locale;

    private String timezone;

    //扩展信息
    @Setter(AccessLevel.NONE)
    private Map<String,Object> meta;

    /**
     * meta中增加内容，不会覆盖原有key
     * @param key
     * @param value
     */
    public void put(String key,Object value){
        if(meta == null){
            meta = new HashMap<>();
        }
        meta.putIfAbsent(key,value);
    }

    public Object getMetaValue(String key){
        if(meta == null){
            return null;
        }
        return meta.get(key);
    }

    public String getMetaStrValue(String key){
        if(meta == null){
            return null;
        }
        return meta.get(key).toString();
    }

    public final String toJsonStr(){
        return JSONUtil.toJsonStr(this);
    }

    public static final GlobalVariables toObject(String jsonStr){
        if(StringUtils.isEmpty(jsonStr)){
            return null;
        }
        return JSONUtil.toBean(jsonStr,GlobalVariables.class);
    }
}
