package com.danimo.models;

import com.danimo.visitor.Visitor;

import java.util.ArrayList;

public class Call extends Instruccion {
    private String name;
    private ArrayList<Instruccion> asignaciones;

    public Call(final int line, final int column, final String name, final ArrayList<Instruccion> asignaciones) {
        super(line, column);
        this.name = name;
        this.asignaciones = asignaciones;
    }

    @Override
    Object run(TablaSimbolos table) {
        return null;
    }

    @Override
    public Variable accept(Visitor v) {
        return v.visit(this);
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public ArrayList<Instruccion> getAsignaciones() {
        return this.asignaciones;
    }

    public void setAsignaciones(final ArrayList<Instruccion> asignaciones) {
        this.asignaciones = asignaciones;
    }
}
