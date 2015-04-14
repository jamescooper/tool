package com.example;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.filter.LoggingFilter;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by coopja on 4/10/15.
 */
public class EchoRequest {
    private String url;
    private String path;
    private Client client;

    public EchoRequest(String url, String path) {
        this.url = url;
        this.path = path;
        client = ClientBuilder.newClient();
    }

    public void initializeClient(String userName, String password, boolean debug) {
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(userName, password);
        client.register(feature);
        if (debug) {
            client.register(new LoggingFilter());
        }
    }

    public void send(String message) throws RuntimeException {
        WebTarget target = getClient().target(getUrl()).path(getPath()).queryParam("message", message);
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
        if (response.getStatus() != 200) {
            throw new RuntimeException("This does not work, check your passwords");
        } else {
            System.out.println("Response: " + response.readEntity(String.class));
        }
    }

    public String getUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
