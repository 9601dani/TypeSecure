package com.danimo.models;

public class Assingment extends Instruccion {
    public String id;
    public Variable.VariableType type;
    public Instruccion value;


    public Assingment(int line, int column, String id, Variable.VariableType type, Instruccion value){
        super(line,column);
        this.id= id;
        this.type= type;
        this.value= value;
    }

    @Override
    Object run(TablaSimbolos table) {
        return null;
    }

    @Override
    public String toString() {
        return "\nAssingment{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", value=" + value +
                ", line=" + line +
                ", column=" + column +
                '}';
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }
}
