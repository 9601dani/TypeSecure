package com.danimo.models;

import java.util.ArrayList;

public class ForState extends Instruccion {
    public ArrayList<Instruccion> declaraciones;
    public Instruccion condition;
    public Instruccion salto;
    public ArrayList<Instruccion> instruccions;

    public ForState(final int line, final int column, final ArrayList<Instruccion> declaraciones, final Instruccion condition, final Instruccion salto, final ArrayList<Instruccion> instruccions) {
        super(line, column);
        this.declaraciones = declaraciones;
        this.condition = condition;
        this.salto = salto;
        this.instruccions = instruccions;
    }


    public ArrayList<Instruccion> getDeclaraciones() {
        return this.declaraciones;
    }

    public void setDeclaraciones(final ArrayList<Instruccion> declaraciones) {
        this.declaraciones = declaraciones;
    }

    public Instruccion getCondition() {
        return this.condition;
    }

    public void setCondition(final Instruccion condition) {
        this.condition = condition;
    }

    public Instruccion getSalto() {
        return this.salto;
    }

    public void setSalto(final Instruccion salto) {
        this.salto = salto;
    }

    public ArrayList<Instruccion> getInstruccions() {
        return this.instruccions;
    }

    public void setInstruccions(final ArrayList<Instruccion> instruccions) {
        this.instruccions = instruccions;
    }

    @Override
    Object run(TablaSimbolos table) {
        return null;
    }
}
