package com.danimo.models;

import com.danimo.visitor.Visitor;

import java.util.ArrayList;

public class ConsoleLog  extends Instruccion{
    private ArrayList<Instruccion> instruccions;

    public ConsoleLog(final int line, final int column, final ArrayList<Instruccion> instruccions) {
        super(line, column);
        this.instruccions = instruccions;
    }

    @Override
    Object run(TablaSimbolos table) {
        return null;
    }

    @Override
    public Variable accept(Visitor v) {
        return v.visit(this);
    }

    public ArrayList<Instruccion> getInstruccions() {
        return this.instruccions;
    }

    public void setInstruccions(final ArrayList<Instruccion> instruccions) {
        this.instruccions = instruccions;
    }

    @Override
    public String toString() {
        return "\nConsoleLog{" +
                "instruccions=" + instruccions +
                ", line=" + line +
                ", column=" + column +
                '}';
    }
}
