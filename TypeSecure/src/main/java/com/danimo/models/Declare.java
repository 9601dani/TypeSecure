package com.danimo.models;

import java.util.ArrayList;

public class Declare  extends  Instruccion{
    public Variable.TypeV type_modi;
    public ArrayList<Instruccion> asignaciones;

    public Declare(int line, int column, Variable.TypeV type_modi, ArrayList<Instruccion> asignaciones) {
        super(line, column);
        this.type_modi= type_modi;
        this.asignaciones= asignaciones;
    }

    @Override
    Object run(TablaSimbolos table) {
        return null;
    }

    public Variable.TypeV getType_modi() {
        return this.type_modi;
    }

    public void setType_modi(final Variable.TypeV type_modi) {
        this.type_modi = type_modi;
    }

    public ArrayList<Instruccion> getAsignaciones() {
        return this.asignaciones;
    }

    public void setAsignaciones(final ArrayList<Instruccion> asignaciones) {
        this.asignaciones = asignaciones;
    }

    @Override
    public String toString() {
        return "\nDeclare{" +
                "type_modi=" + type_modi +
                ", asignaciones=" + asignaciones +
                ", line=" + line +
                ", column=" + column +
                '}';
    }
}


