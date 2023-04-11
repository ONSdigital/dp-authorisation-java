package com.github.onsdigital.dp.authorisation.permissions;

import com.github.onsdigital.dp.authorisation.exceptions.Messages;
import com.github.onsdigital.dp.authorisation.permissions.models.Bundle;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Supplier;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;


public class APIClient implements Store {
    private String bundlerEndpoint = "/v1/permissions-bundle";
    String host;
    private Supplier<CloseableHttpClient> httpClientSupplier;

    public APIClient(String host) {
        this.httpClientSupplier = () -> HttpClients.createDefault();
        this.host = host;
    }

    public APIClient(Supplier<CloseableHttpClient> httpClientSupplier, String host) {
        this.httpClientSupplier = httpClientSupplier;
        this.host = host;
    }


    // getPermissionsBundle gets the permissions bundle data from the permissions API.
    public Bundle getPermissionsBundle() throws Exception {
        String uri = host + bundlerEndpoint;
        info().data("uri", uri).log("getPermissionsBundle: starting permissions bundle request");

        HttpGet request = new HttpGet(uri);
        try (CloseableHttpClient httpClient = httpClientSupplier.get();
             CloseableHttpResponse response = httpClient.execute(request)) {

            int statusCode = response.getStatusLine().getStatusCode();
            info().data("resp.StatusCode", statusCode).log("GetPermissionsBundle: request successfully executed");

            if (statusCode != 200) {

                error().data("resp.StatusCode", statusCode).log(Messages.PermissionAPIFailed);
                throw new Exception(String.format("%s: %d", Messages.PermissionAPIFailed, statusCode));

            }
            info().log("GetPermissionsBundle: returning requested permissions to caller");
            return getResponseEntity(response.getEntity(), Bundle.class);
        }
    }

    private <T> T getResponseEntity(HttpEntity entity, Class<T> tClass) throws IOException {
        try (
                InputStream inputStream = entity.getContent();
                InputStreamReader reader = new InputStreamReader(inputStream)
        ) {
            return new Gson().fromJson(reader, tClass);
        }
    }
}
