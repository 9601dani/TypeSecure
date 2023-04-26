package com.danimo.models;

public abstract class Instruccion {
    int line;
    int column;

    protected Instruccion(final int line, final int column) {
        this.line = line;
        this.column = column;
    }
    abstract Object run ( TablaSimbolos table);
}
