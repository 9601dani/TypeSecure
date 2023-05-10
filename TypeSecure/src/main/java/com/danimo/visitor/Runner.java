package com.danimo.visitor;

import com.danimo.manageError.ObjectErr;
import com.danimo.manageError.TypeSecureError;
import com.danimo.models.*;

import java.math.BigInteger;
import java.util.ArrayList;


public class Runner extends Visitor{
    private static ArrayList<ObjectErr> errorForClient=TypeSecureError.getTypeErrorSingleton().errores;
    private TablaSimbolos table= new TablaSimbolos(null);
    @Override
    public Variable visit(Assingment i) {
        //Obtengo el valor
        Variable variable= new Variable();
        if(i.value==null){
             if(i.type.equals(Variable.VariableType.DEFINIRLA)){
                System.out.println("entre a definirla");
                variable.setId(i.id);
                variable.setValue("undefined");
                variable.setType(i.type);
                return  variable;
            }else{
                 variable.setId(i.id);
                 variable.setValue("undefined");
                 variable.setType(i.type);
                 return  variable;
            }
        }else{
            Variable value= new Variable();
            value= (Variable) i.value.accept(this);
            if(value!=null){ //
                    if(i.type.equals(value.type)){
                        variable.setId(i.id);
                        variable.setValue(value.value);
                        variable.setType(i.type);
                        return variable;
                    }else if(i.type.equals(Variable.VariableType.DEFINIRLA)){
                        System.out.println("entre a definirla");
                        variable.setId(i.id);
                        variable.setValue(value.value);
                        variable.setType(value.type);
                        return  variable;
                    }else{
                        errorForClient.add(new ObjectErr(i.id,i.getLine(), i.getColumn(), "SEMANTICO","Tipo "+ i.type+ " no puedes ponerle un "+ value.type));
                        return null;
                    }
            }else{
                errorForClient.add(new ObjectErr(value.id,i.getLine(), i.getColumn(), "SEMANTICO","No existe un valor para esto"));
                return null;
            }
        }
    }

    @Override
    public Variable visit(OnlyAssingment i) {
        Variable variable=this.table.getWithId(i.id);
        Variable tmp= new Variable();
        if(variable!=null){
            if(!variable.type_modi.equals(Variable.TypeV.CONST)){
                tmp=(Variable) i.value.accept(this);
                if(variable.type.equals(tmp.type)){
                    variable.setValue(tmp.getValue());
                    return  variable;
                }else{
                    errorForClient.add(new ObjectErr(variable.id,i.getLine(), i.getColumn(), "SEMANTICO","Variable tipo: "+variable.type+" y dato nuevo es: "+ tmp.type));
                    return null;
                }
            }else{
                errorForClient.add(new ObjectErr(variable.id,i.getLine(), i.getColumn(), "SEMANTICO","La variable es de tipo CONST, no puedes modificarla"));
                return null;
            }
        }else{
            errorForClient.add(new ObjectErr(variable.id,i.getLine(), i.getColumn(), "SEMANTICO","No existe la variable"));
            return null;
        }
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
    public void visit(Declare i) {

        if(i.type_modi!=null){
            i.asignaciones.forEach(ele->{
                Variable vr1= new Variable();
                vr1.setType_modi(i.type_modi);
                Variable asigment=(Variable) ele.accept(this);
                if(asigment!= null){
                    if(i.type_modi.equals(Variable.TypeV.CONST) && asigment.value.equals("undefined")){
                        errorForClient.add(new ObjectErr(asigment.id, i.getLine(),i.getColumn(),"SEMANTICO", "Una variable CONST debe tener un valor asignado"));
                    }else{
                        vr1.setId(asigment.id);
                        vr1.setType(asigment.type);
                        vr1.setValue(asigment.value);
                        this.table.nuevo(vr1);
                    }
                }else{
                    System.out.println("la asignacion en nula");
                }
            });
        }else{
            System.out.println("error en el tipo de dato en la declaracion");
        }


    }

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
        Variable variable = new Variable();
        switch (i.type){
            case ENTERO -> {
                variable.type= Variable.VariableType.NUMBER;
                variable.value=Integer.parseInt((String)i.value )  ;
                System.out.println("retorne un numero");
                return  variable;
            }
            case NUM_DECIMAL -> {
                variable.type= Variable.VariableType.NUMBER;
                variable.value= Double.valueOf((String)i.value);
                System.out.println("retorne un numero");
                return  variable;
            }
            case BIG_INT -> {
                variable.type= Variable.VariableType.BIGINT;
                variable.value= (String) i.value;
                System.out.println("retorne un bigint");
                return variable;
            }
            case CADENA -> {
                variable.type= Variable.VariableType.STRING;
                variable.value= (String) i.value;
                System.out.println("retorne un string");
                return variable;
            }
            case BOOLEAN -> {
                variable.type= Variable.VariableType.BOOLEAN;
                variable.value= (Boolean) i.value;
                System.out.println("retorne un boolean");
                return variable;
            }
            case LITERAL -> {
                Variable variable_busca= this.table.getWithId((String)i.value);
                if(variable != null){
                    System.out.println("la variable "+i.value+" no ha isdo definida");
                    throw  new Error("Variable no declarada");
                }
                variable=variable_busca;
                return variable;
            }
        }
        if(i.value==null){
            variable.setValue("undefined");
            System.out.println("retorne undefined");
            return variable;
        }
        return null;
    }

    @Override
    public Instruccion visit(While i) {
        return null;
    }

    public TablaSimbolos getTable() {
        return this.table;
    }

    public void setTable(final TablaSimbolos table) {
        this.table = table;
    }
}
