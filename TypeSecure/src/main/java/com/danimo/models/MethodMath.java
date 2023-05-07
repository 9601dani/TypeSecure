package com.danimo.models;

public class MethodMath extends Instruccion {
    public TypeMath type;
    public Instruccion operador_derecho;
    public Instruccion operador_izquierdo;

    public MethodMath(final int line, final int column, final TypeMath type, final Instruccion operador_derecho, final Instruccion operador_izquierdo) {
        super(line, column);
        this.type = type;
        this.operador_derecho = operador_derecho;
        this.operador_izquierdo = operador_izquierdo;
    }

    @Override
    Object run(TablaSimbolos table) {
        return null;
    }
    public TypeMath getType() {
        return this.type;
    }

    public void setType(final TypeMath type) {
        this.type = type;
    }

    public Instruccion getOperador_derecho() {
        return this.operador_derecho;
    }

    public void setOperador_derecho(final Instruccion operador_derecho) {
        this.operador_derecho = operador_derecho;
    }

    public Instruccion getOperador_izquierdo() {
        return this.operador_izquierdo;
    }

    public void setOperador_izquierdo(final Instruccion operador_izquierdo) {
        this.operador_izquierdo = operador_izquierdo;
    }

    public enum TypeMath{
        E,
        PI,
        SQRT2,
        ABS,
        CEIL,
        COS,
        SIN,
        TAN,
        EXP,
        FLOOR,
        POW,
        SQRT,
        RANDOM
    }

    @Override
    public String toString() {
        return "\nMethodMath{" +
                "type=" + type +
                ", operador_derecho=" + operador_derecho +
                ", operador_izquierdo=" + operador_izquierdo +
                ", line=" + line +
                ", column=" + column +
                '}';
    }
}
