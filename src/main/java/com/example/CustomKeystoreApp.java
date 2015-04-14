package com.example;

import org.glassfish.jersey.SslConfigurator;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * Created by coopja on 4/10/15.
 */
public class CustomKeystoreApp {

    public static void main(String[] args) {
        System.out.println(CustomKeystoreApp.class.getName() + ": Start");
        SslConfigurator sslConfig = SslConfigurator.newInstance()
                .trustStoreFile("./toolTrustStore.jks")
                .trustStorePassword("password");

        SSLContext sslContext = sslConfig.createSSLContext();
        Client client = ClientBuilder.newBuilder().sslContext(sslContext).build();

        EchoRequest request = new EchoRequest("https://safeavenue-na.f-secure.com", "api/charter/v2.1/echo");
        request.setClient(client);
        request.initializeClient("charter-api-coopja", "password", true);
        request.send("Hello World");
    }
}
