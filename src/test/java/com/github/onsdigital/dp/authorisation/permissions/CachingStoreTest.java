package com.github.onsdigital.dp.authorisation.permissions;


import com.github.onsdigital.dp.authorisation.permissions.models.Bundle;
import org.hamcrest.CoreMatchers;
import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

public class CachingStoreTest {


    @Mock
    private Store permissionStore;

    private CachingStore cachingStore;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.cachingStore = new CachingStore(permissionStore);
    }

    @Test
    public void Test_update() throws Exception {
        Bundle expected = new Bundle();
        when(permissionStore.getPermissionsBundle()).thenReturn(expected);

        Bundle actual = cachingStore.update();

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void Test_update_underlyingStoreErr() throws Exception {
        when(permissionStore.getPermissionsBundle()).thenThrow(new Exception());

        Bundle actual = cachingStore.update();

        assertThat(actual, equalTo(null));
    }

    @Test
    public void  Test_getPermissionsBundle() throws Exception {
        Bundle expected = new Bundle();
        when(permissionStore.getPermissionsBundle()).thenReturn(expected);

        Bundle actual = cachingStore.update();

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void Test_getPermissionsBundle_NotCached() throws Exception {
        try {
            Bundle actual = cachingStore.getPermissionsBundle();
        } catch (Exception ex) {
            assertThat(ex.getMessage(), CoreMatchers.equalTo("permissions bundle not found in the cache"));
        }
    }

    @Test
    public void Test_checkCacheExpiry() throws Exception {
        Bundle expected = new Bundle();
        when(permissionStore.getPermissionsBundle()).thenReturn(expected);

        cachingStore.update();
        cachingStore.checkCacheExpiry(Duration.standardMinutes(1));

        assertThat(cachingStore.getPermissionsBundle(), equalTo(expected));
    }

    @Test
    public void Test_checkCacheExpiry_Expired() throws Exception {
        Bundle expected = new Bundle();
        when(permissionStore.getPermissionsBundle()).thenReturn(expected);

        try {
            cachingStore.update();
            cachingStore.checkCacheExpiry(Duration.millis(1));
            Thread.sleep(3);
            cachingStore.getPermissionsBundle();
        } catch (Exception ex) {
            assertThat(ex.getMessage(), CoreMatchers.equalTo("permissions bundle not found in the cache"));
        }
    }


    @Test
    public void Test_checkCacheExpiry_NoCachedData() throws Exception {
        Bundle expected = new Bundle();
        when(permissionStore.getPermissionsBundle()).thenReturn(expected);

        try {
            cachingStore.checkCacheExpiry(Duration.millis(1));
        } catch (Exception ex) {
            assertThat(ex.getMessage(), CoreMatchers.equalTo("permissions bundle not found in the cache"));
        }
    }

    @Test
    public void Test_schedulers() throws Exception {
        Bundle expected = new Bundle();
        when(permissionStore.getPermissionsBundle()).thenReturn(expected);

        cachingStore.startCacheUpdater(Duration.standardSeconds(1));
        cachingStore.startExpiryChecker(Duration.standardSeconds(2), Duration.standardMinutes(1));

        assertThat(cachingStore.getPermissionsBundle(), equalTo(expected));
    }
}
