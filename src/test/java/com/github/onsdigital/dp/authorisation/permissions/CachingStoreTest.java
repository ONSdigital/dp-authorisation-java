package com.github.onsdigital.dp.authorisation.permissions;


import com.github.onsdigital.dp.authorisation.permissions.models.Bundle;
import com.github.onsdigital.dp.authorisation.exceptions.BundleNotCached;
import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

public class CachingStoreTest {

    @Mock
    private Store permissionStore;

    private CachingStore cachingStore;

    private static String expectedBundleNotCachedMessage = "permissions bundle not found in the cache";

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
        assertThat(cachingStore.getLastUpdateSuccessful(),equalTo(true));

    }

    @Test
    public void Test_update_underlyingStoreErr() throws Exception {
        when(permissionStore.getPermissionsBundle()).thenThrow(new Exception());
        Bundle actual = cachingStore.update();
        assertThat(actual, equalTo(null));
        assertThat(cachingStore.getLastUpdateSuccessful(),equalTo(false));
    }

    @Test
    public void Test_getPermissionsBundle() throws Exception {
        Bundle expected = new Bundle();
        when(permissionStore.getPermissionsBundle()).thenReturn(expected);

        Bundle actual = cachingStore.update();

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void Test_getPermissionsBundle_NotCached(){
        try {
            Bundle actual = cachingStore.getPermissionsBundle();
        } catch (Exception ex) {
            assertThat(ex.getMessage(), CoreMatchers.equalTo(new BundleNotCached().getMessage()));
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
    public void Test_checkCacheExpiry_testCacheIsUpdated() throws Exception {
        // Given that the cache was last updated 2 minutes ago:
        long timeLastUpdated = (new DateTime().getMillis() - Duration.standardMinutes(2).getMillis());
        cachingStore.setLastUpdated(new DateTime(timeLastUpdated));

        // And a permissions bundle is currently set
        cachingStore.setPermissionsBundle(new Bundle());

        // When the maximum cache expiry time is 1 minute and checkCacheExpiry is called
        cachingStore.checkCacheExpiry(Duration.standardMinutes(1));

        // Then the cache is updated by setting the permissions bundle to null. Therefore,
        // when getPermissionsBundle is called, a BundleNotCached exception is thrown.
        Exception exception = assertThrows(Exception.class,
                () -> cachingStore.getPermissionsBundle());
        Assert.assertTrue(exception.getMessage().contains(expectedBundleNotCachedMessage));
    }

    @Test
    public void Test_checkCacheExpiry_testCacheIsNotUpdatedIfLastUpdatedNull() throws Exception {
        // Given that the cache has never been updated before
        cachingStore.setLastUpdated(null);

        // And a permissions bundle is currently set
        cachingStore.setPermissionsBundle(new Bundle());

        // When the maximum cache expiry time is 1 minute and checkCacheExpiry is called
        cachingStore.checkCacheExpiry(Duration.standardMinutes(1));

        // Then the cache is not updated and a permissions bundle still exists
        Assert.assertNotNull(cachingStore.getPermissionsBundle());

        // And lastUpdated gets initialised
        Assert.assertNotNull(cachingStore.getLastUpdated());
    }

    @Test
    public void Test_checkCacheExpiry_testCacheIsNotUpdatedIfLastUpdatedJustBeenSet() throws Exception {
        // Given that the cache has only just been updated (less than 2 minutes ago)
        cachingStore.setLastUpdated(new DateTime());

        // And a permissions bundle is currently set
        cachingStore.setPermissionsBundle(new Bundle());

        // When the maximum cache expiry time is 2 minutes and checkCacheExpiry is called
        cachingStore.checkCacheExpiry(Duration.standardMinutes(1));

        // Then the cache is not updated and a permissions bundle still exists
        Assert.assertNotNull(cachingStore.getPermissionsBundle());
    }

    @Test
    public void Test_checkCacheExpiry_NoCachedData() throws Exception {
        Bundle expected = new Bundle();
        when(permissionStore.getPermissionsBundle()).thenReturn(expected);

        try {
            cachingStore.checkCacheExpiry(Duration.millis(1));
        } catch (Exception ex) {
            assertThat(ex.getMessage(), CoreMatchers.equalTo(expectedBundleNotCachedMessage));
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
