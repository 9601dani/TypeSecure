package com.danimo.models;

import com.danimo.visitor.Visitor;

public class OperacionBinaria extends Instruccion {
    private OperationType type;
    private Instruccion operador_izquierdo;
    private Instruccion operador_derecho;

    public OperacionBinaria(final int line, final int column, final OperationType type, final Instruccion operador_izquierdo, final Instruccion operador_derecho) {
        super(line, column);
        this.type = type;
        this.operador_izquierdo = operador_izquierdo;
        this.operador_derecho = operador_derecho;
    }

    public OperationType getType() {
        return this.type;
    }

    public void setType(final OperationType type) {
        this.type = type;
    }

    public Instruccion getOperador_izquierdo() {
        return this.operador_izquierdo;
    }

    public void setOperador_izquierdo(final Instruccion operador_izquierdo) {
        this.operador_izquierdo = operador_izquierdo;
    }

    public Instruccion getOperador_derecho() {
        return this.operador_derecho;
    }

    public void setOperador_derecho(final Instruccion operador_derecho) {
        this.operador_derecho = operador_derecho;
    }

    @Override
    public String toString() {
        return "\nOperacionBinaria{" +
                "type=" + type +
                ", operador_izquierdo=" + operador_izquierdo +
                ", operador_derecho=" + operador_derecho +
                ", line=" + line +
                ", column=" + column +
                '}';
    }

    @Override
    Object run(TablaSimbolos table) {
        return null;
    }

    @Override
    public Variable accept(Visitor v) {
        return  v.visit(this);
    }

}
