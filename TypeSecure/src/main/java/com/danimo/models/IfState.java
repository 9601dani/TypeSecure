package com.danimo.models;

import com.danimo.visitor.Visitor;

import java.util.ArrayList;

public class IfState extends Instruccion{
    private Instruccion instruccion;
    private ArrayList<Instruccion> bloque_verdadero;
    private Instruccion bloque_falso;

    public IfState(int line, int column, Instruccion instruccion, ArrayList<Instruccion> bloque_verdadero,Instruccion bloque_falso){
        super(line,column);
        this.instruccion= instruccion;
        this.bloque_verdadero=bloque_verdadero;
        this.bloque_falso= bloque_falso;

    }

    public Instruccion getInstruccion() {
        return this.instruccion;
    }

    public void setInstruccion(final Instruccion instruccion) {
        this.instruccion = instruccion;
    }

    public ArrayList<Instruccion> getBloque_verdadero() {
        return this.bloque_verdadero;
    }

    public void setBloque_verdadero(final ArrayList<Instruccion> bloque_verdadero) {
        this.bloque_verdadero = bloque_verdadero;
    }

    public Instruccion getBloque_falso() {
        return this.bloque_falso;
    }

    public void setBloque_falso(final Instruccion bloque_falso) {
        this.bloque_falso = bloque_falso;
    }

    @Override
    public String toString() {
        return "\nIfState{" +
                "instruccion=" + instruccion +
                ", bloque_verdadero=" + bloque_verdadero +
                ", bloque_falso=" + bloque_falso +
                ", line=" + line +
                ", column=" + column +
                '}';
    }

    @Override
    Object run(TablaSimbolos table) {
        return null;
    }

    @Override
    public Instruccion accept(Visitor v) {
        return v.visit(this);
    }
}
