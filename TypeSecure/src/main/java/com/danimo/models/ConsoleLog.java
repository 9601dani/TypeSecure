package com.danimo.models;

import java.util.ArrayList;

public class ConsoleLog  extends Instruccion{
    public ArrayList<Instruccion> instruccions;

    public ConsoleLog(final int line, final int column, final ArrayList<Instruccion> instruccions) {
        super(line, column);
        this.instruccions = instruccions;
    }

    @Override
    Object run(TablaSimbolos table) {
        return null;
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
