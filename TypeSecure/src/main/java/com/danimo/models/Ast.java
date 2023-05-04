package com.danimo.models;

import java.util.ArrayList;

public class Ast {
    public ArrayList<Instruccion> arbol_ast;
    private static Ast instancia;

    public Ast(){
        this.arbol_ast= new ArrayList<>();
    }
    public static Ast getInstancia(){
        if(Ast.instancia== null){
            Ast.instancia= new Ast();
        }
        return instancia;
    }

    public ArrayList<Instruccion> getArbol_ast() {
        return this.arbol_ast;
    }

    public void setArbol_ast(final ArrayList<Instruccion> arbol_ast) {
        this.arbol_ast = arbol_ast;
    }

    @Override
    public String toString() {
        return "Ast{" +
                "arbol_ast=" + arbol_ast +
                '}';
    }
}
