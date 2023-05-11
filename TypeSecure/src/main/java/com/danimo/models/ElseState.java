package com.danimo.models;

import com.danimo.visitor.Visitor;

import java.util.ArrayList;

public class ElseState extends Instruccion {
    private ArrayList<Instruccion> instruccions;

    public ElseState(int line,int column, ArrayList<Instruccion> instruccions) {
        super(line, column);
        this.instruccions = instruccions;
    }

    public ArrayList<Instruccion> getInstruccions() {
        return this.instruccions;
    }

    public void setInstruccions(final ArrayList<Instruccion> instruccions) {
        this.instruccions = instruccions;
    }

    @Override
    public String toString() {
        return "\nElseState{" +
                "instruccions=" + instruccions +
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
