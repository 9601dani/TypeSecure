package com.danimo.models;

import java.math.BigInteger;

public class Value extends Instruccion{
    public Object value;
    public  ValueType type;

    public Value(int line,int column, Object value, ValueType type) {
        super(line, column);
        this.value = value;
        this.type = type;
    }

    @Override
    Variable run(TablaSimbolos table) {
        Variable variable = new Variable();
        switch (this.type){
            case ENTERO -> {
                variable.type= Variable.VariableType.NUMBER;
                variable.value=(Double) this.value;
                return  variable;
            }
            case NUM_DECIMAL -> {
                variable.type= Variable.VariableType.NUMBER;
                variable.value= (int) this.value;
                return  variable;
            }
            case BIG_INT -> {
                variable.type= Variable.VariableType.BIGINT;
                variable.value= (BigInteger) this.value;
                return variable;
            }
            case CADENA -> {
                variable.type= Variable.VariableType.STRING;
                variable.value= (String) this.value;
                return variable;
            }
            case BOOLEAN -> {
                variable.type= Variable.VariableType.BOOLEAN;
                variable.value= (Boolean) this.value;
                return variable;
            }
            case LITERAL -> {
                Variable variable_busca= table.getWithId((String)this.value);
                if(variable != null){
                    System.out.println("la variable "+this.value+" no ha isdo definida");
                    throw  new Error("Variable no declarada");
                }
                variable=variable_busca;
                return variable;
            }
        }
        return null;
    }
    public enum ValueType{
        BIG_INT,
        ENTERO,
        NUM_DECIMAL,
        CADENA,
        FALSE,
        TRUE,
        BOOLEAN,
        LITERAL
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    public ValueType getType() {
        return this.type;
    }

    public void setType(final ValueType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "\nValue{" +
                "value=" + value +
                ", type=" + type +
                ", line=" + line +
                ", column=" + column +
                '}';
    }
}


