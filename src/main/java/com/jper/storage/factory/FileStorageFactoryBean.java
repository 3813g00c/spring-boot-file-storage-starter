package com.jper.storage.factory;

import com.jper.storage.FileStorageSpi;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author xiangyaowei
 * @date 2021/4/23
 */
public class FileStorageFactoryBean<T> implements FactoryBean<T> {

    private Class<? extends FileStorageSpi<T>> spiClazz;

    private List<FileStorageSpi<T>> fileStorageSpis;

    public FileStorageFactoryBean(ApplicationContext applicationContext, Class<? extends FileStorageSpi<T>> spiClazz) {
        this.spiClazz = spiClazz;
        Map<String, ? extends FileStorageSpi<T>> map = applicationContext.getBeansOfType(spiClazz);
        fileStorageSpis = new ArrayList<>(map.values());
        fileStorageSpis.sort(Comparator.comparingInt(FileStorageSpi::order));
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getObject() throws Exception {
        InvocationHandler invocationHandler = (proxy, method, args) -> {
            for (FileStorageSpi storageSpi : fileStorageSpis) {
                if (storageSpi.verify(args[0])) {
                    return method.invoke(storageSpi, args);
                }

            }
            throw new Exception("no spi server can execute! spiList: " + fileStorageSpis);
        };
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{spiClazz}, invocationHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return spiClazz;
    }
}
