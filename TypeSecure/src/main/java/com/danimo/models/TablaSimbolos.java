package com.danimo.models;

import java.util.ArrayList;

public class TablaSimbolos {
    private ArrayList<Variable> listaVariable;
    private TablaSimbolos parent;
    private  ArrayList<Function> funciones;
    public TablaSimbolos(TablaSimbolos parent) {
        super();
        if (parent != null) {
           this.parent= parent;
        }
        this.listaVariable= new ArrayList<>();

    }
    public boolean nuevo(Variable variable) {
        return listaVariable.add(variable);
    }

    public Variable getWithId(String id) {
       TablaSimbolos table= this;
       Variable variable_return=null;

       do{
           variable_return= table.listaVariable.stream().filter(var-> var.getId().equals(id)).findFirst().orElse(null);
           if(variable_return!=null){
               return variable_return;
           }
           table= table.parent;
       }while(table!=null);
        return null;
    }
    public Instruccion getFunctionWithName(String name){
        Function fuc_return=null;
        if(this.funciones!=null){
            for( Function fc: funciones){
                if(fc.getName().equals(name)){
                    fuc_return= fc;
                    return fuc_return;
                }
            }
            if(fuc_return!=null){
                return fuc_return;
            }
        }
        return null;
    }

    public boolean exist(String id) {
        TablaSimbolos table= this;
        Variable variable_return=null;

        do{
            variable_return= table.listaVariable.stream().filter(var-> var.getId().equals(id)).findFirst().orElse(null);
            if(variable_return!=null){
                return true;
            }
            table= table.parent;
        }while(table!=null);
        return false;
    }

    @Override
    public String toString() {
        if(parent==null){
            return "TablaSimbolos{" +
                    "listaVariable=" + listaVariable +
                    '}';
        }
        return "TablaSimbolos{" +
                "listaVariable=" + listaVariable +
                ", parent=" + parent +
                '}';
    }

    public ArrayList<Variable> getListaVariable() {
        return this.listaVariable;
    }

    public void setListaVariable(final ArrayList<Variable> listaVariable) {
        this.listaVariable = listaVariable;
    }

    public TablaSimbolos getParent() {
        return this.parent;
    }

    public void setParent(final TablaSimbolos parent) {
        this.parent = parent;
    }

    public  ArrayList<Function> getFunciones() {
        if(funciones==null){
            funciones= new ArrayList<>();
        }
        return this.funciones;
    }

    public  void setFunciones(final ArrayList<Function> funciones) {
        this.funciones = funciones;
    }

}
