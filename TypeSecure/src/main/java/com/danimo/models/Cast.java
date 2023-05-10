package com.danimo.models;

import com.danimo.visitor.Visitor;

public class Cast extends Instruccion {
    public CastType tipoCast;
    public Instruccion id;

    public Cast(final int line, final int column, final CastType tipoCast, final Instruccion id) {
        super(line, column);
        this.tipoCast = tipoCast;
        this.id = id;
    }

    @Override
    Object run(TablaSimbolos table) {
        return null;
    }

    @Override
    public Variable accept(Visitor v) {
        return v.visit(this);
    }

    public CastType getTipoCast() {
        return this.tipoCast;
    }

    public void setTipoCast(final CastType tipoCast) {
        this.tipoCast = tipoCast;
    }

    public Instruccion getId() {
        return this.id;
    }

    public void setId(final Instruccion id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "\nCast{" +
                "tipoCast=" + tipoCast +
                ", id=" + id +
                ", line=" + line +
                ", column=" + column +
                '}';
    }

    public enum CastType{
        NUMBER,
        BIGINT,
        BOOLEAN,
        STRING
    }


}

