package com.danimo.models;

import com.danimo.visitor.Visitor;

public class MethodString extends Instruccion {
    public String id;
    public MethodType type;
    public Instruccion operador_derecho;

    public MethodString(final int line, final int column, final String id, final MethodType type, final Instruccion operador_derecho) {
        super(line, column);
        this.id = id;
        this.type = type;
        this.operador_derecho = operador_derecho;
    }

    @Override
    Object run(TablaSimbolos table) {
        return null;
    }

    @Override
    public Variable accept(Visitor v) {
        return  v.visit(this);
    }

    public Instruccion getOperador_derecho() {
        return this.operador_derecho;
    }

    public void setOperador_derecho(final Instruccion operador_derecho) {
        this.operador_derecho = operador_derecho;
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public MethodType getType() {
        return this.type;
    }

    public void setType(final MethodType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "\nMethodString{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", operador_derecho=" + operador_derecho +
                ", line=" + line +
                ", column=" + column +
                '}';
    }

    public enum MethodType{
        LOWERCASE,
        UPPERCASE,
        CONCAT,
        LENGTH,
        CHARAT
    }
}
