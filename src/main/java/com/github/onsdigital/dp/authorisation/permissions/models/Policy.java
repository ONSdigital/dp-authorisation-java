package com.github.onsdigital.dp.authorisation.permissions.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Policy {
    String ID;
    public Condition condition;

    public Policy(String id) {
        ID = id;
    }

    public Policy(String id, Condition condition) {
        ID = id;
        this.condition = condition;
    }
}
