package com.example;

/**
 * Safe Avenue Echo using JVM default keystore
 */
public class DefaultKeystoreApp {

    public static void main(String[] args) {
        EchoRequest request = new EchoRequest("https://safeavenue-na.f-secure.com",
                "api/charter/v2.1/echo");
        request.initializeClient("charter-api-coopja", "wJcapBbepUDJ6t!", true);
        request.send("Hello");
    }
}
