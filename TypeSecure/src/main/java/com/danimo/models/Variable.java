package com.danimo.models;

public class Variable {
    public String id;
    public VariableType type;
    public Object value;

    public Variable() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public VariableType getType() {
        return this.type;
    }

    public void setType(final VariableType type) {
        this.type = type;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    enum VariableType{
        NUMBER,
        BIGINT,
        STRING,
        BOOLEAN,
        VOID,
        UNDEFINED
    }

}


