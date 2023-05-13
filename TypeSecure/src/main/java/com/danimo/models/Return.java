package com.danimo.models;

import com.danimo.visitor.Visitor;

public class Return extends Instruccion{
    private Instruccion instruccion;

    public Return(final int line, final int column, final Instruccion instruccion) {
        super(line, column);
        this.instruccion = instruccion;
    }

    @Override
    Object run(TablaSimbolos table) {
        return null;
    }

    @Override
    public Instruccion accept(Visitor v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return "Return{" +
                "instruccion=" + instruccion +
                ", line=" + line +
                ", column=" + column +
                '}';
    }

    public Instruccion getInstruccion() {
        return this.instruccion;
    }

    public void setInstruccion(final Instruccion instruccion) {
        this.instruccion = instruccion;
    }
}
