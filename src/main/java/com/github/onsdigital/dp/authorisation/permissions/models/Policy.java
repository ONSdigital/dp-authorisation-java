package com.github.onsdigital.dp.authorisation.permissions.models;

/**
 * Policy.
 */
public class Policy {
    private Condition condition;
    private String ID;

    public Policy(String id) {
        this.ID = id;
    }
    public Policy(String id, Condition condition) {
        this.ID = id;
        this.condition = condition;
    }
    public Condition getCondition() {
        return condition;
    }

    public String getID()  {
        return ID;
    }

}
