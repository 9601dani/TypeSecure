package com.danimo.models;

import com.danimo.visitor.Visitor;

import java.math.BigInteger;

public class Value extends Instruccion{
    private Object value;
    private  ValueType type;

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
                variable.setType(Variable.VariableType.NUMBER);
                variable.setValue(Integer.parseInt((String)this.getValue()));
                System.out.println("retorne un numbero");
                return  variable;
            }
            case NUM_DECIMAL -> {
                variable.setType(Variable.VariableType.NUMBER);
                variable.setValue(Double.valueOf((String)this.getValue()));
                System.out.println("retorne un numbero");
                return  variable;
            }
            case BIG_INT -> {
                variable.setType(Variable.VariableType.BIGINT);
                variable.setValue((String) this.getValue());
                System.out.println("retorne un bigint");
                return variable;
            }
            case CADENA -> {
                variable.setType(Variable.VariableType.STRING);
                variable.setValue((String) this.getValue());
                System.out.println("retorne un string");
                return variable;
            }
            case BOOLEAN -> {
                variable.setType(Variable.VariableType.BOOLEAN);
                variable.setValue((Boolean) this.getValue());
                System.out.println("retorne un boolean");
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

    @Override
    public Variable accept(Visitor v) {
        return  v.visit(this);
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
        return "\n"+value;
    }
}


