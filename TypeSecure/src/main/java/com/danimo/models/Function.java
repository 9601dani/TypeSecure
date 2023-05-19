package com.danimo.models;

import com.danimo.visitor.Visitor;

import java.util.ArrayList;

public class Function extends  Instruccion{
    private String name;
    private ArrayList<Parametro> parametros;
    private ArrayList<Instruccion> instruccions;
    private Variable.VariableType  type;
    private Boolean isOnTable;

    public Function(final int line, final int column, final String name, final ArrayList<Parametro> parametros, final ArrayList<Instruccion> instruccions, final Variable.VariableType type) {
        super(line, column);
        this.name = name;
        this.parametros = parametros;
        this.instruccions = instruccions;
        this.type = type;
        this.isOnTable=false;
    }

    @Override
    Object run(TablaSimbolos table) {
        return null;
    }

    @Override
    public Variable accept(Visitor v) {
        return v.visit(this);
    }

    public String getname() {
        return this.name;
    }

    public void setname(final String name) {
        this.name = name;
    }

    public ArrayList<Parametro> getParametros() {
        return this.parametros;
    }

    public void setParametros(final ArrayList<Parametro> parametros) {
        this.parametros = parametros;
    }

    public ArrayList<Instruccion> getInstruccions() {
        return this.instruccions;
    }

    public void setInstruccions(final ArrayList<Instruccion> instruccions) {
        this.instruccions = instruccions;
    }

    public Variable.VariableType getType() {
        return this.type;
    }

    public void setType(final Variable.VariableType type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Boolean getOnTable() {
        return this.isOnTable;
    }

    public void setOnTable(final Boolean onTable) {
        this.isOnTable = onTable;
    }

    @Override
    public String toString() {
        return "\n\tFunction{" +
                "name='" + name + '\'' +
                ", parametros=" + parametros +
                ", instruccions=" + instruccions +
                ", type=" + type +
                ", line=" + line +
                ", column=" + column +
                '}';
    }
}
