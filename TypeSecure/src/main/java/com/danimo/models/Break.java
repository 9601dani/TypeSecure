package com.danimo.models;

import com.danimo.visitor.Visitor;

public class Break extends Instruccion {
    public Break(final int line, final int column) {
        super(line, column);
    }

    @Override
    Object run(TablaSimbolos table) {
        return null;
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
