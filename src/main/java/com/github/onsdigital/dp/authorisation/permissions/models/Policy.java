package com.github.onsdigital.dp.authorisation.permissions.models;

/**
 * Policy.
 */
public class Policy {
    private Condition condition;
    private String identity;



    public Policy(String id) {
        this.identity = id;
    }
    public Policy(String id, Condition condition) {
        this.identity = id;
        this.condition = condition;
    }
    public Condition getCondition() {
        return condition;
    }

    public String getID()  {
        return identity;
    }

}
