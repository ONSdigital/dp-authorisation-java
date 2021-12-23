package com.github.onsdigital.dp.authorisation.permissions.models;

import java.util.ArrayList;
import java.util.List;

public class Condition {

    public List<String> attributes;
    public String operator;
    public List<String> values;

    public Condition(String attribute, String operator, String value) {
        attributes = new ArrayList<String>() {{
            add(attribute);
        }};
        this.operator = operator;
        values = new ArrayList<String>() {{
            add(value);
        }};
    }
}
