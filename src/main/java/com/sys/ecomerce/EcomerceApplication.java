package com.sys.ecomerce;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = {"com.sys.ecomerce", "commons"})
@MapperScan("com.sys.ecomerce.mapper")
@EnableCaching
public class EcomerceApplication {

    static {
        System.setProperty("java.net.useSystemProxies", "false");
        String nonProxy =
                "localhost|127.*|10.*|192.168.*|172.*|*.local|8.131.154.104";
        System.setProperty("http.nonProxyHosts", nonProxy);
        System.setProperty("socksNonProxyHosts", nonProxy);
        System.clearProperty("socksProxyHost");
        System.clearProperty("socksProxyPort");
        System.clearProperty("socksProxyVersion");
        System.clearProperty("http.proxyHost");
        System.clearProperty("http.proxyPort");
        System.clearProperty("https.proxyHost");
        System.clearProperty("https.proxyPort");

        ProxySelector.setDefault(new ProxySelector() {
            @Override
            public List<Proxy> select(URI uri) {
                return List.of(Proxy.NO_PROXY);
            }

            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                // no-op
            }
        });
    }

    public static void main(String[] args) {
        SpringApplication.run(EcomerceApplication.class, args);
    }
}
