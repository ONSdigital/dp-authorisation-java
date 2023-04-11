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


/**
 * APIClient.
 */
public class APIClient implements Store {
    public static final int STATUS_CODE_SUCCESS = 200;
    private final String statusCodeTitle = "resp.StatusCode";
    private final String bundlerEndpoint = "/v1/permissions-bundle";
    private final Supplier<CloseableHttpClient> httpClientSupplier;
    String host;

    public APIClient(String host) {
        this.httpClientSupplier = () -> HttpClients.createDefault();
        this.host = host;
    }

    public APIClient(Supplier<CloseableHttpClient> httpClientSupplier, String host) {
        this.httpClientSupplier = httpClientSupplier;
        this.host = host;
    }


    /**
     * getPermissionsBundle gets the permissions bundle data from the permissions API.
     *
     * @return getResponseEntity
     * @throws Exception
     */
    public Bundle getPermissionsBundle() throws Exception {
        String uri = host + bundlerEndpoint;
        info().data("uri", uri).log("getPermissionsBundle: starting permissions bundle request");

        HttpGet request = new HttpGet(uri);
        try (CloseableHttpClient httpClient = httpClientSupplier.get();
             CloseableHttpResponse response = httpClient.execute(request)) {

            int statusCode = response.getStatusLine().getStatusCode();
            info().data(statusCodeTitle, statusCode).log("GetPermissionsBundle: request successfully executed");

            if (statusCode != STATUS_CODE_SUCCESS) {

                error().data(statusCodeTitle, statusCode).log(Messages.PERMISSIONS_API_FAILED);
                throw new Exception(String.format("%s: %d", Messages.PERMISSIONS_API_FAILED, statusCode));

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
