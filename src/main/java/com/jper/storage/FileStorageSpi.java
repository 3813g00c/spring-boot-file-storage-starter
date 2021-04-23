package com.jper.storage;

/**
 * @author xiangyaowei
 * @date 2021/4/23
 */
public interface FileStorageSpi<T> {

    /**
     * 根据入参选择执行条件
     *
     * @param condition
     * @return
     */
    boolean verify(T condition);

    default int order() {
        return 10;
    }
}
