package com.university.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {

        /*
         * Corporate proxy bypass for localhost.
         *
         * On company networks the JVM inherits the system HTTP proxy settings.
         * Even requests to http://localhost are routed through the corporate
         * proxy, which rejects them.  This causes the Eureka client to fail
         * every HTTP call with "Cannot execute request on any known server".
         *
         * Setting http.nonProxyHosts before the Spring context starts ensures
         * that ALL HTTP clients (RestTemplate, HttpURLConnection, Jersey) skip
         * the proxy for localhost / 127.0.0.1 traffic.
         */
        // Bypass the corporate proxy for all loopback / local addresses.
        // The pipe-delimited list covers IPv4 loopback, all-zeros bind address,
        // and IPv6 loopback (::1).  Java's HttpURLConnection, Apache HC5 with
        // useSystemProperties(), and most JVM HTTP clients honour this property.
        // System.setProperty("http.nonProxyHosts",  "localhost|127.0.0.1|0.0.0.0|::1");
        // System.setProperty("https.nonProxyHosts", "localhost|127.0.0.1|0.0.0.0|::1");

        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
