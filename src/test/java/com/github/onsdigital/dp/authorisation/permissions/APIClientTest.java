package com.github.onsdigital.dp.authorisation.permissions;


import com.github.onsdigital.dp.authorisation.permissions.models.Bundle;
import com.github.onsdigital.dp.authorisation.permissions.models.Condition;
import com.github.onsdigital.dp.authorisation.permissions.models.EntityIDToPolicies;
import com.github.onsdigital.dp.authorisation.permissions.models.Policy;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class APIClientTest {

  @Mock
  private CloseableHttpClient httpClient;

  private Supplier<CloseableHttpClient> clientSupplier;

  private APIClient client;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    httpClient = mock(CloseableHttpClient.class);
    clientSupplier = () -> httpClient;
    this.client = new APIClient(clientSupplier, "");
  }

  @Test
  public void testGetPermissionBundle_success() throws Exception {
    CloseableHttpResponse response = mock(CloseableHttpResponse.class);

    Bundle expectedBundle = new Bundle("permission/admin",
            new EntityIDToPolicies("group/admin",
                    new Policy("policy/123",
                            new Condition("collection_id", "StringEquals", "col123")
                    )
            )
    );

    String expectedJson = new Gson().toJson(expectedBundle);

    try (InputStream responseBody = new ByteArrayInputStream(expectedJson.getBytes())) {
      when(httpClient.execute(Matchers.any())).thenReturn(response);
      StatusLine statusLine = mock(StatusLine.class);
      when(response.getStatusLine()).thenReturn(statusLine);
      when(statusLine.getStatusCode()).thenReturn(200);
      HttpEntity entity = mock(HttpEntity.class);
      when(response.getEntity()).thenReturn(entity);
      when(entity.getContent()).thenReturn(responseBody);

      Bundle actual = client.getPermissionsBundle();

      assertThat(new Gson().toJson(actual), equalTo(expectedJson));
    }
  }

  @Test
  public void testGetContentHash_non200Status() {
    try {
      CloseableHttpResponse response = mock(CloseableHttpResponse.class);
      StatusLine statusLine = mock(StatusLine.class);

      when(httpClient.execute(Matchers.any())).thenReturn(response);
      when(response.getStatusLine()).thenReturn(statusLine);
      when(statusLine.getStatusCode()).thenReturn(400);

      client.getPermissionsBundle();
    } catch (Exception ex) {
      assertThat(ex.getMessage(), CoreMatchers.equalTo("unexpected status returned from the permissions api permissions-bundle endpoint: 400"));
    }
  }
}
