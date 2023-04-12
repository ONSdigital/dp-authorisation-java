package com.github.onsdigital.dp.authorisation.permissions.models;

/**
 * Policy.
 */
public class Policy {
  public Condition condition;
  String ID;

  public Policy(String id) {
    ID = id;
  }

  public Policy(String id, Condition condition) {
    ID = id;
    this.condition = condition;
  }
}
