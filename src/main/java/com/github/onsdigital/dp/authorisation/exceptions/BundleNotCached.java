package com.github.onsdigital.dp.authorisation.exceptions;

public class BundleNotCached extends Exception {
    static final String Message = "permissions bundle not found in the cache";

    public BundleNotCached() {
        super(Message);
    }
}
