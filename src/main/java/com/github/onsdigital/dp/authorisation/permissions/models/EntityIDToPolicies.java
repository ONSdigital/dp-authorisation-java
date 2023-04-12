package com.github.onsdigital.dp.authorisation.permissions.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * EntityIDToPolicies.
 */
public class EntityIDToPolicies extends HashMap<String, List<Policy>> {
  EntityIDToPolicies() {
    super();
  }

  /**
   * EntityIDToPolicies.
   *
   * @param id - id
   *
   * @param policy - policy
   *
   */
  public EntityIDToPolicies(String id, Policy policy) {
    this.put(id, new ArrayList<Policy>() {
      {
        add(policy);
      }
    });
  }
}
