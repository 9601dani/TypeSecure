package com.danimo.models;

import com.danimo.visitor.Visitor;

public class Assingment extends Instruccion {
    private String id;
    private Variable.VariableType type;
    private Instruccion value;


    public Assingment(int line, int column, String id, Variable.VariableType type, Instruccion value){
        super(line,column);
        this.id= id;
        this.type= type;
        this.value= value;
    }

    @Override
    Object run(TablaSimbolos table) {
        return null;
    }

    @Override
    public Variable accept(Visitor v) {
        return  v.visit(this);
    }

    @Override
    public String toString() {
        return "\nAssingment{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", value=" + value +
                ", line=" + line +
                ", column=" + column +
                '}';
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Variable.VariableType getType() {
        return this.type;
    }

    public void setType(final Variable.VariableType type) {
        this.type = type;
    }

    public Instruccion getValue() {
        return this.value;
    }

    public void setValue(final Instruccion value) {
        this.value = value;
    }
}
