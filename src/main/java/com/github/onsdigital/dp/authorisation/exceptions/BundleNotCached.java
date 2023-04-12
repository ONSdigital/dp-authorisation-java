package com.github.onsdigital.dp.authorisation.exceptions;

/**
 * BundleNotCached.
 */
public class BundleNotCached extends Exception {
  static final String MESSAGE = "permissions bundle not found in the cache";

  public BundleNotCached() {
    super(MESSAGE);
  }
}
