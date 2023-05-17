package com.danimo.models;

import com.danimo.visitor.Visitor;

public class GetTable extends Instruccion {
    public GetTable(final int line, final int column) {
        super(line, column);
    }

    @Override
    Object run(TablaSimbolos table) {
        return null;
    }

    @Override
    public Object accept(Visitor v) {
        v.visit(this);
        return null;
    }
}
