package com.danimo.models;

import java.util.ArrayList;

public class Ast {
    private ArrayList<Instruccion> arbol_ast;
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

}
