package com.github.onsdigital.dp.authorisation.permissions.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Condition.
 */
public class Condition {

    public String attribute;
    public String operator;
    public List<String> values;

    /**
     * Condition.
     * @param attribute
     * @param operator
     * @param value
     */
    public Condition(String attribute, String operator, String value) {
        this.attribute = attribute;
        this.operator = operator;
        values = new ArrayList<String>() {
            {
                add(value);
            }
        };
    }

}
