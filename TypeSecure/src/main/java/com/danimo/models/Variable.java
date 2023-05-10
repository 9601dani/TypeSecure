package com.danimo.models;

public class Variable {
    public String id;
    public VariableType type;
    public Object value;
    public TypeV type_modi;

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

    public TypeV getType_modi() {
        return this.type_modi;
    }

    public void setType_modi(final TypeV type_modi) {
        this.type_modi = type_modi;
    }
    public enum VariableType{
        NUMBER,
        BIGINT,
        STRING,
        BOOLEAN,
        VOID,
        UNDEFINED,
        DEFINIRLA
    }
    public enum TypeV{
        LET,
        CONST
    }

    @Override
    public String toString() {
        return "\nVariable{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", value=" + value +
                ", type_modi=" + type_modi +
                '}';
    }
}



