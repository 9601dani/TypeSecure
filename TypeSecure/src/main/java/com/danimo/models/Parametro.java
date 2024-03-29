package com.danimo.models;

import com.danimo.visitor.Visitor;

public class Parametro extends Instruccion {
    private String id;
    private Variable.VariableType type;
    private String valor;

    public Parametro(final int line, final int column, final String id, final Variable.VariableType type) {
        super(line, column);
        this.id = id;
        this.type = type;
        this.valor=null;
    }

    @Override
    Object run(TablaSimbolos table) {
        return null;
    }

    @Override
    public Variable accept(Visitor v) {
        return  v.visit(this);
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Variable.VariableType getType() {
        return this.type;
    }

    public void setType(final Variable.VariableType type) {
        this.type = type;
    }

    public String getValor() {
        return this.valor;
    }

    public void setValor(final String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "\nParametro{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", line=" + line +
                ", column=" + column +
                '}';
    }
}
