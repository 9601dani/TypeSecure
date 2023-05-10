package com.danimo.models;

import com.danimo.visitor.Visitor;

public class OnlyAssingment extends Instruccion {
    public String id;
    public Variable.VariableType type;
    public Instruccion value;

    public OnlyAssingment(final int line, final int column, final String id, final Variable.VariableType type, final Instruccion value) {
        super(line, column);
        this.id = id;
        this.type = type;
        this.value = value;
    }

    @Override
    Object run(TablaSimbolos table) {
        return null;
    }

    @Override
    public Variable accept(Visitor v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return "\nOnlyAssingment{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", value=" + value +
                ", line=" + line +
                ", column=" + column +
                '}';
    }
}
