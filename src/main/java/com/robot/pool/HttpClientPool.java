package com.robot.pool;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;


public class HttpClientPool {

    public static final HttpClientPool DEFAULT = new HttpClientPool(new HttpClientPoolProperties());

    private final HttpClientBuilder httpClientBuilder;

    private static SSLContext sslContext = null;


    public HttpClientPool(HttpClientPoolProperties httpClientPoolProperties) {
        Registry registry = RegistryBuilder.create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
//                .register("https", SSLConnectionSocketFactory
//                        .getSystemSocketFactory())
                .register("https",new SSLConnectionSocketFactory(createIgnoreVerifySSL(), NoopHostnameVerifier.INSTANCE))
                //自定义ssl证书
//                .register("https", getSSLConnectionSocket())
                .build();
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(
                registry);
        //默认为Socket配置
        SocketConfig defaultSocketConfig = SocketConfig.custom()
                //tcp 包延迟优化,true
                .setTcpNoDelay(httpClientPoolProperties.isTcpNoDelay()).build();
        manager.setDefaultSocketConfig(defaultSocketConfig);
        //设置整个连接池的最大连接数,500
        manager.setMaxTotal(httpClientPoolProperties.getMaxTotal());
        //每个路由的默认最大连接，每个路由实际最大连接数由DefaultMaxPerRoute控制，而MaxTotal是整个池子的最大数 500
        manager.setDefaultMaxPerRoute(httpClientPoolProperties.getDefaultMaxPerRoute());
        // 每个路由的最大连接数
        // 在从连接池获取连接时，连接不活跃多长时间后需要进行一次验证，默认为2s,默认设置 5*1000
        manager.setValidateAfterInactivity(httpClientPoolProperties.getValidateAfterInactivity());
        //默认请求配置
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                //设置连接超时时间，2s,2*1000
                .setConnectTimeout(httpClientPoolProperties.getConnectTimeout())
                //设置等待数据超时时间，5s 5*1000
                .setSocketTimeout(httpClientPoolProperties.getSocketTimeout())
                //设置从连接池获取连接的等待超时时间,2000
                .setConnectionRequestTimeout(httpClientPoolProperties.getConnectionRequestTimeout())
                .build();
        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                //连接池不是共享模式,true
                .setConnectionManagerShared(httpClientPoolProperties.isConnectionManagerShared());
        httpClientBuilder
                //定期回收空闲连接 60
                .evictIdleConnections(httpClientPoolProperties.getEvictIdleConnectionsTime(),
                        TimeUnit.SECONDS);
        if (httpClientPoolProperties.isEvictExpiredConnections()) {
            //定期回收过期连接 true
            httpClientBuilder.evictExpiredConnections();
        }
        if (httpClientPoolProperties.getConnectionTimeToLive() > 0) {
            httpClientBuilder
                    //连接存活时间，如果不设置，则根据长连接信息决定 60
                    .setConnectionTimeToLive(httpClientPoolProperties.getConnectionTimeToLive()
                            , TimeUnit.SECONDS);
        }
        if (httpClientPoolProperties.getRetryCount() > 0) {
            httpClientBuilder.setRetryHandler(
                    //设置重试次数，默认是3次，当前是禁用掉（根据需要开启） 0
                    new DefaultHttpRequestRetryHandler(
                            httpClientPoolProperties.getRetryCount(), false));
        }
        //设置默认请求配置
        this.httpClientBuilder = httpClientBuilder
                .setDefaultRequestConfig(defaultRequestConfig)
                .setConnectionManager(manager)
                //连接重用策略，即是否能keepAlive
                .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)
                //长连接配置，即获取长连接生产多长时间
                .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE);

    }

    public CloseableHttpClient fetchHttpClient() {
        return httpClientBuilder.build();
    }

    /**
     * 自定义一个ssl
     * @return
     */
    private SSLConnectionSocketFactory getSSLConnectionSocket() {
        return new SSLConnectionSocketFactory(sslContext, new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"}, null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
    }


    /**
     * 绕过ssl
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static SSLContext createIgnoreVerifySSL()  {
        SSLContext sc = null;
        try {
            TrustStrategy acceptingTrustStrategy = new TrustSelfSignedStrategy();

            sc = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        }catch (Exception e){
            e.printStackTrace();
        }

        return sc;

    }
}