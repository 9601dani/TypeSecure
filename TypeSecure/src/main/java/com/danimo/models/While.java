package com.danimo.models;

import com.danimo.visitor.Visitor;

import java.util.ArrayList;

public class While extends Instruccion {
    private Instruccion operation;
    private ArrayList<Instruccion> instruccions;

    public While(final int line, final int column, final Instruccion operation, final ArrayList<Instruccion> instruccions) {
        super(line, column);
        this.operation = operation;
        this.instruccions = instruccions;
    }

    public Instruccion getOperation() {
        return this.operation;
    }

    public void setOperation(final Instruccion operation) {
        this.operation = operation;
    }

    public ArrayList<Instruccion> getInstruccions() {
        return this.instruccions;
    }

    public void setInstruccions(final ArrayList<Instruccion> instruccions) {
        this.instruccions = instruccions;
    }

    @Override
    public String toString() {
        return "\nWhile{" +
                "operation=" + operation +
                ", instruccions=" + instruccions +
                ", line=" + line +
                ", column=" + column +
                '}';
    }

    @Override
    Object run(TablaSimbolos table) {
        return null;
    }

    @Override
    public Instruccion accept(Visitor v) {
        return v.visit(this);

    }
}
