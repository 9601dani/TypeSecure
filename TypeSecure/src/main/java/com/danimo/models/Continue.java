package com.danimo.models;

import com.danimo.visitor.Visitor;

public class Continue extends Instruccion{
    public Continue(final int line, final int column) {
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
