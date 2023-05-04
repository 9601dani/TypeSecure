package com.danimo.models;

import java.util.ArrayList;

public class ElseState extends Instruccion {
    public ArrayList<Instruccion> instruccions;

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
}
