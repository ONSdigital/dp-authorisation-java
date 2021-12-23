package com.github.onsdigital.dp.authorisation.permissions;

import com.github.onsdigital.dp.authorisation.exceptions.BundleNotCached;
import com.github.onsdigital.dp.authorisation.permissions.models.Bundle;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.*;

public class CachingStore implements Cache {
    private Store underlyingStore;
    private ReentrantLock mutex;
    private Bundle cachedBundle;
    private Boolean lastUpdateSuccessful;
    private DateTime lastUpdated;
    private ScheduledExecutorService scheduledExecutorService;

    CachingStore() {
        scheduledExecutorService = Executors.newScheduledThreadPool(2);
        mutex = new ReentrantLock();
    }

    CachingStore(Store underlyingStore) {
        this();
        this.underlyingStore = underlyingStore;
    }

    // getPermissionsBundle returns the cached permission data, or an error if it's not cached.
    public Bundle getPermissionsBundle() throws BundleNotCached {
        mutex.lock();
        try {
            if (cachedBundle == null) {
                throw new BundleNotCached();
            }
            return cachedBundle;
        } finally {
            mutex.unlock();
        }
    }

    // startExpiryChecker starts a goroutine to continually check for expired cache data.
    //  - checkInterval - how often to check for expired cache data.
    //  - maxCacheTime - how long to cache permissions data before it's expired.
    public void startExpiryChecker(Duration checkInterval, Duration maxCacheTime) {
        scheduledExecutorService.schedule(
                () -> checkCacheExpiry(maxCacheTime),
                checkInterval.getMillis(), TimeUnit.MILLISECONDS
        );
    }

    // checkCacheExpiry clears the cache data it it's gone beyond it's expiry time.
    void checkCacheExpiry(Duration maxCacheTime) {
        mutex.lock();
        try {
            if (lastUpdated != null && (lastUpdated.getMillis() - System.currentTimeMillis()) > maxCacheTime.getMillis()) {
                info().log("clearing permissions cache data as it has gone beyond the max cache time");
                cachedBundle = null;
                lastUpdated = new DateTime();
            }
        } finally {
            mutex.unlock();
        }
    }

    // startCacheUpdater starts a go routine to continually update cache data at time intervals.
    //  - updateInterval - how often to update the cache data.
    public void startCacheUpdater(Duration updateInterval) {
        update();
        scheduledExecutorService.schedule(
                () -> update(),
                updateInterval.getMillis(), TimeUnit.MILLISECONDS
        );
    }


    // Update the permissions cache data, by calling the underlying permissions store
    public Bundle update() {
        mutex.lock();
        try {
            Bundle permissionsBundle = underlyingStore.getPermissionsBundle();
            info().log("updating cache");
            cachedBundle = permissionsBundle;
            lastUpdateSuccessful = true;
        } catch (Exception e) {
            error().logException(e, "failed to update permissions cache");
            lastUpdateSuccessful = false;
        } finally {
            lastUpdated = new DateTime();
            mutex.unlock();
        }
        return cachedBundle;
    }

    @Override
    public void close() {
        scheduledExecutorService.shutdown();
    }
}
