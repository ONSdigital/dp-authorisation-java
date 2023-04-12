package com.github.onsdigital.dp.authorisation.permissions.models;


import java.util.HashMap;

/**
 * Bundle.
 */
public class Bundle extends HashMap<String, EntityIDToPolicies> {
  public Bundle() {
    super();
  }

  public Bundle(String id, EntityIDToPolicies entityIDToPolicies) {
    this.put(id, entityIDToPolicies);
  }

  public Bundle add(String id, EntityIDToPolicies entityIDToPolicies) {
    this.put(id, entityIDToPolicies);
    return this;
  }
}
