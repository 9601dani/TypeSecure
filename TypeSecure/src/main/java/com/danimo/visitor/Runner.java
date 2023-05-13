package com.danimo.visitor;

import com.danimo.manageError.ObjectErr;
import com.danimo.manageError.TypeSecureError;
import com.danimo.models.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Runner extends Visitor{
    private static ArrayList<ObjectErr> errorForClient=TypeSecureError.getTypeErrorSingleton().errores;
    private TablaSimbolos table= new TablaSimbolos(null);
    @Override
    public Variable visit(Assingment i) {
        return null;
    }

    @Override
    public Variable visit(OnlyAssingment i) {
        return null;
    }


    @Override
    public Variable visit(Cast i) {
        return null;
    }

    @Override
    public Variable visit(ConsoleLog i) {
        return null;
    }

    @Override
    public void visit(Declare i) {}

    @Override
    public Instruccion visit(DoWhile i) {
        return null;
    }

    @Override
    public Instruccion visit(ElseState i) {
        return null;
    }

    @Override
    public Instruccion visit(ForState i) {
        return null;
    }

    @Override
    public Instruccion visit(Function i) {
        return null;
    }

    @Override
    public Instruccion visit(IfState i) {
        return null;
    }

    @Override
    public Variable visit(MethodMath i) {
        return null;
    }

    @Override
    public Variable visit(MethodString i) {
        return null;
    }

    @Override
    public Variable visit(OperacionBinaria i) {
        return null;
    }

    @Override
    public Variable visit(Parametro i) {
        return null;
    }

    @Override
    public Variable visit(Value i) {
        return null;
    }

    @Override
    public Instruccion visit(While i) {
        return null;
    }

    @Override
    public Instruccion visit(Return i) {
        return null;
    }

    @Override
    public Instruccion visit(Continue i) {
        return null;
    }

    @Override
    public Instruccion visit(Break i) {
        return null;
    }

    @Override
    public Variable visit(Call i) {
        return null;
    }

    public TablaSimbolos getTable() {
        return this.table;
    }

    public void setTable(final TablaSimbolos table) {
        this.table = table;
    }
/*    private Boolean verificarOperaciones(Variable left, Variable rigth){
        if(left.getType().equals(Variable.VariableType.STRING)){
            return false;
        }
        if(left.type=== VariableType.BOOLEAN){
            return false;
        }
        if(right.type=== VariableType.TEXT){
            return false;
        }
        if(right.type=== VariableType.BOOLEAN){
            return false;
        }
        return true;
    }*/
}
