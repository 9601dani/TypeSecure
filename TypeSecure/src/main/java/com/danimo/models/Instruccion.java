package com.danimo.models;

import com.danimo.visitor.Visitor;

public abstract class Instruccion {
    int line;
    int column;

    protected Instruccion(final int line, final int column) {
        this.line = line;
        this.column = column;
    }
     abstract Object run ( TablaSimbolos table);
     public abstract Object accept(Visitor v);
    public int getLine() {
        return this.line;
    }

    public void setLine(final int line) {
        this.line = line;
    }

    public int getColumn() {
        return this.column;
    }

    public void setColumn(final int column) {
        this.column = column;
    }


}
