package com.danimo.visitor;

import com.danimo.models.*;

public class DebugRunner extends Visitor {

    @Override
    public Variable visit(Assingment i) {
        System.out.println("DEBUG ASSIGMENT");
        i.value.accept(this);
        return null;
    }

    @Override
    public Variable visit(OnlyAssingment i) {
        System.out.println("DEBUG ASSIG");
        i.value.accept(this);
        return null;
    }

    @Override
    public Variable visit(Cast i) {
        System.out.println("DEBUG CAST");
        i.id.accept(this);
        return null;
    }

    @Override
    public Variable visit(ConsoleLog i) {
        System.out.println("DEBUG CONSOLE");
        i.instruccions.forEach(ele->{
            ele.accept(this);
        });
        return null;
    }

    @Override
    public void visit(Declare i) {
        System.out.println("DEBUG DECLARE");
        i.asignaciones.forEach(ele->{
            ele.accept(this);
        });
    }

    @Override
    public Instruccion visit(DoWhile i) {
        System.out.println("DEBUG DOWHILE");
        i.operation.accept(this);
        i.instruccions.forEach(ele->{
            ele.accept(this);
        });
        return null;
    }

    @Override
    public Instruccion visit(ElseState i) {
        System.out.println("DEBUG ELSESTATE");
        i.instruccions.forEach(ele->{
            ele.accept(this);
        });
        return null;
    }

    @Override
    public Instruccion visit(ForState i) {
        System.out.println("DEBUG FOR");
        i.declaraciones.forEach(ele->{
            ele.accept(this);
        });
        i.condition.accept(this);
        i.salto.accept(this);
        i.instruccions.forEach(ele->{
            ele.accept(this);
        });
        return null;
    }

    @Override
    public Instruccion visit(Function i) {
        System.out.println("DEBUG FUNCTION");
        return null;
    }

    @Override
    public Instruccion visit(IfState i) {
        System.out.println("DEBUG IF");
        i.bloque_verdadero.forEach(ele->{
            ele.accept(this);
        });
        return null;
    }

    @Override
    public Variable visit(MethodMath i) {
        System.out.println("DEBUG METHODMATH");
        return null;
    }

    @Override
    public Variable visit(MethodString i) {
        System.out.println("DEBUG METHODSTRING");
        return null;
    }

    @Override
    public Variable visit(OperacionBinaria i) {
        System.out.println("DEBUG OPERACIONBINARIA");
        return null;
    }

    @Override
    public Variable visit(Parametro i) {
        System.out.println("DEBUG PARAMETRO");
        return null;
    }

    @Override
    public Variable visit(Value i) {
        System.out.println("DEBUG VALUE");
        return null;
    }

    @Override
    public Instruccion visit(While i) {
        System.out.println("DEBUG WHILE");
        i.operation.accept(this);
        i.instruccions.forEach(ele->{
            ele.accept(this);
        });
        return null;
    }
}
