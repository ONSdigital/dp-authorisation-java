package com.github.onsdigital.dp.authorisation.permissions.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Condition.
 */
public class Condition {

    private String attribute;
    private String operator;
    private List<String> values;

    /**
     * Condition.
     *
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

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

}
