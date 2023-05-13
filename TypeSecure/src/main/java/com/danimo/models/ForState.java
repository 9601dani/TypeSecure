package com.danimo.models;

import com.danimo.visitor.Visitor;

import java.util.ArrayList;

public class ForState extends Instruccion {
    private Instruccion declaraciones;
    private Instruccion condition;
    private Instruccion salto;
    private ArrayList<Instruccion> instruccions;

    public ForState(final int line, final int column, final Instruccion declaraciones, final Instruccion condition, final Instruccion salto, final ArrayList<Instruccion> instruccions) {
        super(line, column);
        this.declaraciones = declaraciones;
        this.condition = condition;
        this.salto = salto;
        this.instruccions = instruccions;
    }


    public Instruccion getDeclaraciones() {
        return this.declaraciones;
    }

    public void setDeclaraciones(final Instruccion declaraciones) {
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

    @Override
    public Instruccion accept(Visitor v) {
        return v.visit(this);

    }
}
