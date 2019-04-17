package com.hex.bigdata.udsp.jdbc.netty;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.hex.bigdata.udsp.config.NettyClientConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 长连接客户端工厂抽象类
 */
public abstract class AbstractClientFactory implements ClientFactory {

    // 长连接缓存，一个域名对应一个长连接
    private final Cache<RemotingUrl, Client> cacherClients = CacheBuilder.newBuilder()//
            .maximumSize(NettyClientConfig.getMaximumSize ()) // 单机长连接上限，超过上限采用LRU淘汰
            .expireAfterAccess(NettyClientConfig.getExpireAfterAccess (), TimeUnit.MINUTES) // 连接长时间没有读写则删除
            .removalListener(new RemovalListener<RemotingUrl, Client>() {
                @Override
                public void onRemoval(RemovalNotification<RemotingUrl, Client> notification) {
                    // 关闭连接
                    notification.getValue().close();
                }
            }) //
            .build();

    protected abstract Client createClient(RemotingUrl url) throws Exception;

    @Override
    public Client get(final RemotingUrl url) throws Exception {
        Client client = cacherClients.get(url, new Callable<Client>() {

            @Override
            public Client call() throws Exception {
                Client client = createClient(url);
                if (null != client) {
                    client.startHeartBeat();
                }
                return client;
            }

        });
        return client;
    }

    @Override
    public List<Client> getAllClients() throws Exception {
        List<Client> result = new ArrayList<Client>((int) cacherClients.size());
        result.addAll(cacherClients.asMap().values());
        return result;
    }

    @Override
    public void remove(RemotingUrl url) throws Exception {
        cacherClients.invalidate(url);
    }

}