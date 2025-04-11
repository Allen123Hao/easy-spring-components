package com.allen.component.common.global;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * @author Allen
 * @version 1.0
 * @since 2023/7/5 10:31
 */
public class GlobalContext {

    private static ThreadLocal<GlobalVariables> variablesHolder = new TransmittableThreadLocal<>();

    public static void setGlobalVariables(GlobalVariables variables) {
        variablesHolder.set(variables);
    }

    public static GlobalVariables getGlobalVariables () {
        return variablesHolder.get();
    }

    public static void clear() {
        variablesHolder.remove();
    }
}
