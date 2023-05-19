package com.danimo.models;

import java.io.FileWriter;
import java.io.IOException;
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
    public void graphic(){
        String graph="digraph G {\n";
        graph+="node [shape=box];\n";
        graph+="node0[label=\"AST\"];\n";
        graph+="node1[label=\"INSTRUCCIONES\"];\n";
        graph+="node0->node1;\n";
        int contador=2;
        for(Instruccion instruccion: this.arbol_ast){
            graph+="node"+contador+"[label=\""+instruccion.toString()+"\"];\n";
            graph+="node1->node"+contador+";\n";
            contador++;
        }
        graph+="}";
    }
    public void createGraphvizFile() {
        String graph = "digraph G {\n";
        graph += "node [shape=box];\n";
        graph += "node0[label=\"AST\"];\n";
        graph += "node1[label=\"INSTRUCCIONES\"];\n";
        graph += "node0->node1;\n";
        int contador = 2;
        for (Instruccion instruccion : this.arbol_ast) {
            graph += "node" + contador + "[label=\"" + instruccion.toString() + "\"];\n";
            graph += "node1->node" + contador + ";\n";
            contador++;
        }
        graph += "}";

        String filename = "graph.png";

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(graph);
            System.out.println("Archivo Graphviz creado exitosamente: " + filename);
        } catch (IOException e) {
            System.err.println("Error al crear el archivo Graphviz: " + e.getMessage());
        }
    }
    @Override
    public String toString() {
        return "Ast{" +
                "arbol_ast=" + arbol_ast +
                '}';
    }
}
