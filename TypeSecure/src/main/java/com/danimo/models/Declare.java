package com.danimo.models;

import com.danimo.visitor.Visitor;

import java.util.ArrayList;

public class Declare  extends  Instruccion{
    private Variable.TypeV type_modi;
    private ArrayList<Instruccion> asignaciones;

    public Declare(int line, int column, Variable.TypeV type_modi, ArrayList<Instruccion> asignaciones) {
        super(line, column);
        this.type_modi= type_modi;
        this.asignaciones= asignaciones;
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
        return "Declare{" +
                "type_modi=" + type_modi +
                "node"  + "[label=\"" + asignaciones.toString() + "\"];\n"+
                "node1->node}";
    }
}


