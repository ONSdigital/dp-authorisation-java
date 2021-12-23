package com.github.onsdigital.dp.authorisation.permissions.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Policy {
    String ID;
    public List<Condition> conditions;

    public Policy(String id) {
        ID = id;
    }

    public Policy(String id, Condition condition) {
        ID = id;
        conditions = new ArrayList<Condition>() {{
                add(condition);
            }};
    }

    public Policy(String id, Condition[] conditions) {
        ID = id;
        this.conditions = new ArrayList<Condition>(Arrays.asList(conditions));

    }
}
