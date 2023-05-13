package com.danimo.visitor;

import com.danimo.models.*;

public abstract class Visitor {
    private TablaSimbolos table;
    public abstract Variable visit(Assingment i);
    public abstract Variable visit(OnlyAssingment i);
    public abstract Variable visit(Cast i);
    public abstract Variable visit(ConsoleLog i);
    public abstract void visit(Declare i);
    public abstract Instruccion visit(DoWhile i);
    public abstract Instruccion visit(ElseState i);
    public abstract Instruccion visit(ForState i);
    public abstract Instruccion visit(Function i);
    public abstract Instruccion visit(IfState i);
    public abstract Variable visit(MethodMath i);
    public abstract Variable visit(MethodString i);
    public abstract Variable visit(OperacionBinaria i);
    public abstract Variable visit(Parametro i);
    public abstract  Variable visit(Value i);
    public abstract Instruccion visit(While i);
    public abstract Instruccion visit(Return i);
    public abstract Instruccion visit(Continue i);
    public abstract Instruccion visit(Break i);
    public abstract Variable visit(Call i);

}
