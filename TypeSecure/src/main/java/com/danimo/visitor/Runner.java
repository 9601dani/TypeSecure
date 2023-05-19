package com.danimo.visitor;

import com.danimo.manageError.ObjectErr;
import com.danimo.manageError.TypeSecureError;
import com.danimo.models.*;

import javax.swing.*;
import java.math.BigInteger;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.danimo.Main.view_console;
import static com.danimo.ParserHandleSecure.generarTablaErrores;
import static com.danimo.ParserHandleSecure.generarTablaSym;


public class Runner extends Visitor{
    private static ArrayList<ObjectErr> errorForClient=TypeSecureError.getTypeErrorSingleton().errores;
    private TablaSimbolos table= new TablaSimbolos(null);
    private ArrayList<Variable> variables_nuevas_funcion= new ArrayList<>();
    @Override
    public Variable visit(Assingment i) {
        //Obtengo el valor
        Variable variable= new Variable();
        if(this.table.getWithId(i.getId())==null){
            if(i.getValue()==null){
                variable.setId(i.getId());
                variable.setValue(Variable.VariableType.UNDEFINED.toString());
                variable.setType(i.getType());
                return  variable;
            }else{
                Variable value= new Variable();
                value= (Variable) i.getValue().accept(this);
                if(value!=null){ //
                    if(i.getType().equals(value.getType())){
                        variable.setId(i.getId());
                        variable.setValue(value.getValue());
                        variable.setType(i.getType());
                        return variable;
                    }else if(i.getType().equals(Variable.VariableType.DEFINIRLA)){
                        variable.setId(i.getId());
                        variable.setValue(value.getValue());
                        variable.setType(value.getType());
                        return  variable;
                    }else{
                        errorForClient.add(new ObjectErr(i.getId(),i.getLine(), i.getColumn(), "SEMANTICO","Tipo "+ i.getType()+ " no puedes ponerle un "+ value.getType()));
                        return null;
                    }
                }else{
                    // errorForClient.add(new ObjectErr(" ",i.getLine(), i.getColumn(), "SEMANTICO","No existe un valor para esto"));
                    return null;
                }
            }

        }else {
            errorForClient.add(new ObjectErr(i.getId(),i.getLine(), i.getColumn(), "SEMANTICO","Variable ya declarada"));
            return null;
        }
    }

    @Override
    public Variable visit(OnlyAssingment i) {
        Variable variable=this.table.getWithId(i.getId());
        Variable tmp= new Variable();
        if(variable!=null){
            if(!variable.getType_modi().equals(Variable.TypeV.CONST)){
                tmp=(Variable) i.getValue().accept(this);
                if(variable.getType().equals(tmp.getType())){
                    variable.setValue(tmp.getValue());
                    return  variable;
                }else{
                    errorForClient.add(new ObjectErr(variable.getId(),i.getLine(), i.getColumn(), "SEMANTICO","Variable tipo: "+variable.getType()+" y dato nuevo es: "+ tmp.getType()));
                    return null;
                }
            }else{
                errorForClient.add(new ObjectErr(variable.getId(),i.getLine(), i.getColumn(), "SEMANTICO","La variable es de tipo CONST, no puedes modificarla"));
                return null;
            }
        }else{
            errorForClient.add(new ObjectErr(i.getId(),i.getLine(), i.getColumn(), "SEMANTICO","Variable no definida"));
            return null;
        }
    }


    @Override
    public Variable visit(Cast i) {
        Variable variable= (Variable)i.getId().accept(this);
        Variable variable_return= new Variable();
        if(variable!=null /*&& (String)variable.getValue()!= Variable.VariableType.UNDEFINED.toString()*/){
            switch (i.getTipoCast()){
                case STRING -> {
                    try{
                        String result= (String)variable.getValue();
                        variable_return.setType(Variable.VariableType.STRING);
                        variable_return.setId(variable.getId());
                        if(result.equalsIgnoreCase(Variable.VariableType.UNDEFINED.toString()) || result.equalsIgnoreCase("")){
                            variable_return.setValue(Variable.VariableType.UNDEFINED.toString());
                            return variable_return;
                        }
                        variable_return.setValue(result);
                        return variable_return;
                    }catch (Exception e){
                        errorForClient.add(new ObjectErr(variable.getId(),i.getLine(), i.getColumn(), "SEMANTICO","No se puede castear a string"));
                        return null;
                    }
                }
                case NUMBER -> {
                    try{
                        switch (variable.getType()){
                            case BIGINT -> {
                                int result=  extractNumber((String)variable.getValue());
                                if(result==-1){
                                    JOptionPane.showMessageDialog(null,"error aqui");
                                    new Exception("error");
                                }
                                variable_return.setType(Variable.VariableType.NUMBER);
                                variable_return.setId(variable.getId());
                                if(result== Math.floor(result)){
                                    variable_return.setValue(String.valueOf(result));
                                    return variable_return;
                                }
                                variable_return.setValue(Double.toString(result));
                                return variable_return;
                            }
                            case NUMBER -> {
                                return variable;
                            }
                            case STRING -> {
                                String valor= (String)variable.getValue();
                                variable_return.setType(Variable.VariableType.NUMBER);
                                variable_return.setId(variable.getId());
                                if(valor.equalsIgnoreCase(" ")){
                                    variable_return.setValue("0");
                                    return variable_return;
                                }
                                Double result=Double.parseDouble((String)variable.getValue());
                                if(result== Math.floor(result)){
                                    variable_return.setValue(String.valueOf(result.intValue()) );
                                    return variable_return;
                                }
                                variable_return.setValue(result.toString());
                                return variable_return;
                            }
                            case BOOLEAN -> {
                                Boolean result=Boolean.valueOf((String)variable.getValue());
                                variable_return.setType(Variable.VariableType.NUMBER);
                                variable_return.setId(variable.getId());
                                if(result){
                                    variable_return.setValue("1");
                                    return variable_return;
                                }
                                variable_return.setValue("0");
                                return variable_return;
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        errorForClient.add(new ObjectErr(variable.getId(),i.getLine(), i.getColumn(), "SEMANTICO","No se puede castear a Number"));
                        return null;
                    }
                }
                case BOOLEAN -> {
                    switch (variable.getType()){
                        case BOOLEAN -> {
                            return variable;
                        }
                        case UNDEFINED -> {
                            variable_return.setType(Variable.VariableType.BOOLEAN);
                            variable_return.setId(variable.getId());
                            variable_return.setValue("false");
                            return variable_return;
                        }
                        case NUMBER -> {
                            String valor= (String) variable.getValue();
                            variable_return.setType(Variable.VariableType.BOOLEAN);
                            variable_return.setId(variable.getId());
                            if(valor.equalsIgnoreCase("0") || valor.equalsIgnoreCase("-0") || valor.equalsIgnoreCase("undefined") ){
                                variable_return.setValue("false");
                                return variable_return;
                            }
                            variable_return.setValue("true");
                            return  variable_return;
                        }
                        case BIGINT -> {
                            String valor= (String) variable.getValue();
                            variable_return.setType(Variable.VariableType.BOOLEAN);
                            variable_return.setId(variable.getId());
                            if(valor.equalsIgnoreCase("0n") || valor.equalsIgnoreCase("-0n") || valor.equalsIgnoreCase("undefined")){
                                variable_return.setValue("false");
                                return variable_return;
                            }
                            variable_return.setValue("true");
                            return  variable_return;
                        }
                        case STRING -> {
                            String valor= (String) variable.getValue();
                            variable_return.setType(Variable.VariableType.BOOLEAN);
                            variable_return.setId(variable.getId());
                            if(valor.equalsIgnoreCase("") || valor.equalsIgnoreCase("undefined")){
                                variable_return.setValue("false");
                                return variable_return;
                            }
                            variable_return.setValue("true");
                            return  variable_return;
                        }
                    }
                }
                case BIGINT -> {
                    try{
                        switch (variable.getType()){
                            case BIGINT -> {
                                return  variable;
                            }
                            case BOOLEAN -> {
                                Boolean result=Boolean.valueOf((String)variable.getValue());
                                variable_return.setType(Variable.VariableType.BIGINT);
                                variable_return.setId(variable.getId());
                                if(result){
                                    variable_return.setValue("1n");
                                    return variable_return;
                                }
                                variable_return.setValue("0n");
                                return variable_return;
                            }
                            case NUMBER -> {
                                Double result = Double.parseDouble((String)variable.getValue());
                                variable_return.setType(Variable.VariableType.BIGINT);
                                variable_return.setId(variable.getId());
                                if(result== Math.floor(result)){
                                    if(result<0){
                                        variable_return.setValue(String.valueOf(result.intValue())+"n" );
                                        return variable_return;
                                    }
                                    variable_return.setValue(String.valueOf(result.intValue())+"n" );
                                    return variable_return;
                                }
                                errorForClient.add(new ObjectErr(result.toString(),i.getLine(), i.getColumn(), "SEMANTICO","Perdida de precision"));
                                return null;
                            }
                            case STRING -> {
                                String valor= (String)variable.getValue();
                                variable_return.setType(Variable.VariableType.BIGINT);
                                variable_return.setId(variable.getId());
                                valor= valor.replace(" ","");
                                valor=valor.replace("n","");
                                if(valor.equalsIgnoreCase("")){
                                    variable_return.setValue("0n");
                                    return variable_return;
                                }
                                variable_return.setValue((valor)+"n");
                                return variable_return;
                            }
                        }

                    }catch (Exception e){
                        errorForClient.add(new ObjectErr(variable.getId(),i.getLine(), i.getColumn(), "SEMANTICO","No se puede castear a BigInt"));
                        return null;
                    }
                }
            }
        }else{
            if(variable!=null){
                errorForClient.add(new ObjectErr(variable.getId(),i.getLine(), i.getColumn(), "SEMANTICO","Variable no definida/undefined"));
                return null;
            }
            errorForClient.add(new ObjectErr(null,i.getLine(), i.getColumn(), "SEMANTICO","Variable no definida/undefined"));
            return null;
        }
        return null;
    }

    @Override
    public Variable visit(ConsoleLog i) {
        ArrayList<Variable> arrayDatos= new ArrayList<>();
        if(i.getInstruccions()!=null){
            String dat="";
            for(Instruccion instruccion_console: i.getInstruccions()){
                Variable txts = (Variable) instruccion_console.accept(this);
                if(txts!=null){
                    dat+=(String) txts.getValue();
                }
            }
            if(!dat.equals("")){
                view_console.add(dat+"\n");
            }
            return null;
        }else{
            errorForClient.add(new ObjectErr(null, i.getLine(),i.getColumn(),"Semantico","El valor es nulo en console"));
            return null;
        }
    }

    @Override
    public void visit(Declare i) {
        if(i.getAsignaciones()!=null){
            if(i.getType_modi()!=null){
                i.getAsignaciones().forEach(ele->{
                    Variable vr1= new Variable();
                    vr1.setType_modi(i.getType_modi());
                    if(ele!=null){
                        Variable asigment=(Variable) ele.accept(this);
                        if(asigment!= null){
                            if(i.getType_modi().equals(Variable.TypeV.CONST) && asigment.getValue().equals(Variable.VariableType.UNDEFINED.toString())){
                                errorForClient.add(new ObjectErr(asigment.getId(), i.getLine(),i.getColumn(),"SEMANTICO", "Una variable CONST debe tener un valor asignado"));
                            }else{
                                vr1.setId(asigment.getId());
                                vr1.setType(asigment.getType());
                                vr1.setValue(asigment.getValue());
                                vr1.setLine(i.getLine());
                                vr1.setColumn(i.getColumn());
                                this.table.nuevo(vr1);
                            }
                        }else{
                            errorForClient.add(new ObjectErr(" ", i.getLine(),i.getColumn(),"SEMANTICO", "No se pudo asignar un valor, no se le asigno valor/tipo de variable"));
                        }
                    }
                });
            }else{
                errorForClient.add(new ObjectErr(" ", i.getLine(),i.getColumn(),"SEMANTICO", "No se asigno si la variable es const o let"));
            }
        }else{
            errorForClient.add(new ObjectErr(" ", i.getLine(),i.getColumn(),"SEMANTICO", "la variable no tiene ninguna asignacion, debes asignarle"));
        }
    }

    @Override
    public Instruccion visit(DoWhile i) {
        try{
            Boolean isBreak=false;
            Variable vr=new Variable();
            vr.setValue("true");
            vr.setType(Variable.VariableType.BOOLEAN);
            if(vr.getType()== Variable.VariableType.BOOLEAN){
                TablaSimbolos tmp= new TablaSimbolos(this.table);
                this.table=tmp;
                while(((String)vr.getValue()).equalsIgnoreCase("true")){
                    Boolean isContinue=false;
                    if(i.getInstruccions()==null){
                        break;
                    }else{
                        int contador=1;
                        for(Instruccion ele: i.getInstruccions()){
                            Object inst= ele.accept(this);
                            if(inst!=null){
                                if(inst.getClass().equals(Break.class)){
                                            /*vr=(Variable) i.getOperation().accept(this);*/
                                            if(this.table.getParent()!=null){
                                                this.table=this.table.getParent();
                                            }
                                            isBreak=true;
                                            break;
                                } else if (inst.getClass().equals(Continue.class)) {
                                    if(this.table.getParent()!=null){
                                        this.table=this.table.getParent();
                                    }
                                    isContinue=true;
                                    break;
                                }else if (inst.getClass().equals(Return.class)) {
                                    Return rst= (Return) inst;
                                    if(this.table.getParent()!=null){
                                        this.table= this.table.getParent();
                                    }
                                    return (Instruccion)rst;
                                }
                                else{
                                    vr=(Variable) inst;
                                }
                            }else{
                               /* ele.accept(this);*/
                            }
                        }
                    }
                    if(isBreak){
                        vr.setValue("false");
                    }else{
                        vr=(Variable) i.getOperation().accept(this);
                    }
                    if((String)vr.getValue()==null){
                        vr.setValue("false");
                    }
                }
                if(this.table.getParent()!=null){
                    this.table=this.table.getParent();
                }
            }else {
                errorForClient.add(new ObjectErr(null,i.getOperation().getLine(),i.getOperation().getColumn(),"Semantico","La condicion debe de ser un boolean"));
                return null;
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Instruccion visit(ElseState i) {
        TablaSimbolos table_else= new TablaSimbolos(this.table);
        this.table= table_else;
        if(i.getInstruccions()!=null){
            for (Instruccion ele_else: i.getInstruccions()){
                Object ret=ele_else.accept(this);
                if(ret!=null){
                    if(ret.getClass().equals(Break.class)){
                        Break eleme= new Break(i.getLine(), i.getColumn());
                        if(this.table.getParent()!=null){
                            this.table= this.table.getParent();
                        }
                        return(Instruccion) eleme;
                    } else if (ret.getClass().equals(Continue.class)) {
                        Continue eleme= new Continue(ele_else.getLine(),ele_else.getColumn());
                        if(this.table.getParent()!=null){
                            this.table= this.table.getParent();
                        }
                        return(Instruccion) eleme;
                    }else if (ret.getClass().equals(Return.class)) {
                        Return rst= (Return) ret;
                        if(this.table.getParent()!=null){
                            this.table= this.table.getParent();
                        }
                        return (Instruccion)rst;
                    }
                }

            }
        }
         if(this.table.getParent()!=null){
                        this.table= this.table.getParent();
                    }
        return null;
    }

    @Override
    public Instruccion visit(ForState i) {
        TablaSimbolos tmp= new TablaSimbolos(this.table);
        tmp.setFunciones(this.table.getFunciones());
        this.table= tmp;
        Boolean isBreak=false;
        for(Instruccion asignacion_for: i.getDeclaraciones()){
            asignacion_for.accept(this);
        }
        Variable vr= (Variable)i.getCondition().accept(this);
        if(vr==null){
            errorForClient.add(new ObjectErr(null, i.getLine(),i.getColumn(),"SEMANTICO", "La condicion es nula"));
            this.table= this.table.getParent();
            return null;
        }
        if(vr.getType()!= Variable.VariableType.BOOLEAN){
            errorForClient.add(new ObjectErr(null, i.getLine(),i.getColumn(),"SEMANTICO", "La comparacion del for debe ser tipo Boolean"));
            this.table= this.table.getParent();
            return null;
        }
        int line;
        int column;
        while(((String)vr.getValue()).equalsIgnoreCase("true")){
            Boolean isContinue=false;
            if(i.getInstruccions()!=null){
                for(Instruccion instr_for: i.getInstruccions()){
                    Object tps=instr_for.accept(this);
                    if(tps!=null){
                        if(tps.getClass().equals(Break.class)){
                            isBreak=true;
                            break;
                        } else if (tps.getClass().equals(Continue.class)) {
                            isContinue=true;
                            break;
                        }else if (tps.getClass().equals(Return.class)) {
                            Return rst= (Return) tps;
                            if(this.table.getParent()!=null){
                                this.table= this.table.getParent();
                            }
                            return (Instruccion)rst;
                        }
                    }else{
                    }
                }
                if(!isBreak){
                    i.getSalto().accept(this);
                    vr= (Variable)i.getCondition().accept(this);
                }else{
                    return null;
                }
                if(isContinue){
                }
            }else{
                break;
            }
            if(isBreak){
                vr.setValue("false");
            }else{
                vr=(Variable) i.getCondition().accept(this);
            }
            if((String)vr.getValue()==null){
                vr.setValue("false");
            }
        }
        if(this.table.getParent()!=null){
            this.table= this.table.getParent();
        }
        return null;
    }

    @Override
    public Variable visit(Function i) {
        if(!i.getOnTable()){
        }else{
            ArrayList<Function> tmp= this.table.getFunciones();
            this.table= new TablaSimbolos(this.table);
            this.table.setFunciones(tmp);
            ArrayList<Variable> vrs= new ArrayList<>();
            if(i.getParametros()!=null){
                for (Instruccion parametro_fun: i.getParametros()){
                    if(parametro_fun!=null){
                        Variable n_variable= (Variable)parametro_fun.accept(this);
                        if(n_variable== null){
                            //ERROR PARAMETRO NO DEFINIDO
                        }else{
                           /* this.table.nuevo(n_variable);*/
                        }
                    }
                }
            }
            if(i.getInstruccions()!=null){
                for(Instruccion instr: i.getInstruccions()){
                    Object pso=instr.accept(this);
                    if(i.getType().equals(Variable.VariableType.VOID)){
                        if(pso!=null){
                            if(pso.getClass().equals(Return.class)){
                                errorForClient.add(new ObjectErr(null, i.getLine(),i.getColumn(),"SEMANTICO", "la funcion es tipo void, no debes retornar nada"));
                                return null;
                            }
                        }
                    }else{
                        if(this.verificarVariables(i.getInstruccions())){
                            if(i.getInstruccions().get(i.getInstruccions().size()-1) instanceof Return){
                                    Object variable_returrn=((Variable)pso);
                                    if(variable_returrn!=null){
                                    if(variable_returrn.getClass().equals(Variable.class)){
                                            if(i.getType()==null || i.getType()== Variable.VariableType.DEFINIRLA){
                                                i.setType(((Variable) variable_returrn).getType());
                                            }
                                            Variable variable_de_retorno= ((Variable)variable_returrn);
                                            if(((Variable) variable_de_retorno).getType().equals(i.getType())){
                                                /*i.setOnTable(true);
                                                this.table.getFunciones().add(i);
                                                this.table=this.table.getParent();*/
                                                return variable_de_retorno;
                                            }else{
                                                errorForClient.add(new ObjectErr(null, i.getLine(),i.getColumn(),"SEMANTICO", "La variable de retorno no es tipo -> "+ i.getType()));
                                            }
                                    } else if (variable_returrn.getClass().equals(Return.class)) {
                                        System.out.println("-------------------------------------------------------------- TIPO RETURN ");
                                        /*System.out.println("TYPO VARIABLE RETORNO-> "+((Variable) variable_returrn).getType());*/
                                        /*if(i.getType()==null || i.getType()== Variable.VariableType.DEFINIRLA){
                                            i.setType(((Variable) variable_returrn).getType());
                                        }
                                        Variable variable_de_retorno= ((Variable)variable_returrn);
                                        if(((Variable) variable_de_retorno).getType().equals(i.getType())){
                                                *//*i.setOnTable(true);
                                                this.table.getFunciones().add(i);
                                                this.table=this.table.getParent();*//*
                                            return (Return)
                                                    variable_de_retorno;
                                        }else{
                                            errorForClient.add(new ObjectErr(null, i.getLine(),i.getColumn(),"SEMANTICO", "La variable de retorno no es tipo -> "+ i.getType()));
                                            System.out.println("LA VARIABLE DE RETORNO ES DISTINTO AL DE LA FUNCION");
                                        }*/
                                    } else{
                                            errorForClient.add(new ObjectErr(null, i.getLine(),i.getColumn(),"SEMANTICO", "SE DEBE RETORNAR UNA VARIABLE"));
                                        }
                                    }
                            }else{
                                errorForClient.add(new ObjectErr(null, i.getInstruccions().get(i.getInstruccions().size()-1).getLine(),i.getInstruccions().get(i.getInstruccions().size()-1).getColumn(),"SEMANTICO", "La ultima instruccion debe ser un RETURN o si hay instrucciones abajo del return no se ejecutaran"));
                            }
                        }else{
                            errorForClient.add(new ObjectErr(null, i.getLine(),i.getColumn(),"SEMANTICO", "Todos los return de la funcion deben ser del mismo tipo"));
                        }
                    }
                }

            }
            this.table= this.table.getParent();
            return null;

        }
        return null;
    }
    public Boolean verificarVariables(ArrayList<Instruccion> array_instr){
        ArrayList<Variable> vr = new ArrayList<>();
        array_instr.forEach(elementos->{
            Object ts= elementos.accept(this);
            if(ts!=null){
                if(ts.getClass().equals(Return.class)){
                    vr.add((Variable) elementos.accept(this));
                }
            }
        });
        if(vr.size()>0){
            Variable.VariableType tipo= vr.get(0).getType();
            for (int i = 0; i < vr.size(); i++) {
                if (vr.get(i).getType() != tipo) {
                    return false; // Se encontró un elemento con un tipo diferente, por lo tanto no todos los elementos tienen el mismo tipo.
                }
            }
            return true; // Todos los elementos tienen el mismo tipo.

        }else{
            return true;
        }
    }
    @Override
    public Instruccion visit(IfState i) {
        try{
            if(i.getInstruccion()!=null){
                Variable comparacion=(Variable) i.getInstruccion().accept(this);
                if(comparacion.getValue()!=null){
                    if(((String)comparacion.getValue()).equalsIgnoreCase("true")){
                        TablaSimbolos tmp_if= new TablaSimbolos(this.table);
                        tmp_if.setFunciones(this.table.getFunciones());
                        this.table=tmp_if;
                        for(Instruccion ele: i.getBloque_verdadero()){
                            Object ret= ele.accept(this);
                            if(ret!=null){
                                if(ret.getClass().equals(Break.class)){
                                    Break eleme= new Break(i.getLine(), i.getColumn());
                                    if(this.table.getParent()!=null){
                                        this.table= this.table.getParent();
                                    }
                                    return(Instruccion) eleme;
                                } else if (ret.getClass().equals(Continue.class)) {
                                    Continue eleme= new Continue(i.getLine(), i.getColumn());
                                    if(this.table.getParent()!=null){
                                        this.table= this.table.getParent();
                                    }
                                    return(Instruccion) eleme;
                                } else if (ret.getClass().equals(Return.class)) {
                                    Return rst= (Return) ret;
                                    if(this.table.getParent()!=null){
                                        this.table= this.table.getParent();
                                    }
                                    return rst;
                                }
                            }else{
                            }
                        }
                        if(this.table.getParent()!=null){
                            this.table= this.table.getParent();
                        }
                        return null;
                    }else{
                        if(i.getBloque_falso()!=null){
                            TablaSimbolos tmp_else= new TablaSimbolos(this.table);
                            tmp_else.setFunciones(this.table.getFunciones());
                            this.table=tmp_else;
                            Object return_posible= i.getBloque_falso().accept(this);
                            if(return_posible!=null){
                                if(return_posible.getClass().equals(Break.class)){
                                    Break eleme= new Break(i.getLine(), i.getColumn());
                                    if(this.table.getParent()!=null){
                                        this.table= this.table.getParent();
                                    }
                                    return(Instruccion) eleme;
                                } else if (return_posible.getClass().equals(Continue.class)) {
                                    Continue eleme= new Continue(i.getBloque_falso().getLine(),i.getBloque_falso().getColumn());
                                    if(this.table.getParent()!=null){
                                        this.table= this.table.getParent();
                                    }
                                    return(Instruccion) eleme;
                                }else if (return_posible.getClass().equals(Return.class)) {
                                    Return rst= (Return) return_posible;
                                    if(this.table.getParent()!=null){
                                        this.table= this.table.getParent();
                                    }
                                    return (Instruccion)rst;
                                }
                            }
                            if(this.table.getParent()!=null){
                                this.table= this.table.getParent();
                            }
                            return null;
                        }else{
                        }
                        return null;
                    }

                }else{
                    errorForClient.add(new ObjectErr(null,i.getLine(), i.getColumn(),"SEMANTICO", "Comparacion nula/undefined _IF"));
                    return null;
                }
            }else{
                errorForClient.add(new ObjectErr(null,i.getLine(), i.getColumn(),"SEMANTICO", "Falta de comparacion en _IF"));
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Variable visit(MethodMath i) {
        Variable variable_return = new Variable();

        switch (i.getType()){
            case E->{
                variable_return.setType(Variable.VariableType.NUMBER);
                variable_return.setValue(Double.toString(Math.E));
                return  variable_return;
            }
            case PI -> {
                variable_return.setType(Variable.VariableType.NUMBER);
                variable_return.setValue(Double.toString(Math.PI));
                return  variable_return;
            }
            case SQRT2 -> {
                variable_return.setType(Variable.VariableType.NUMBER);
                variable_return.setValue(Double.toString(Math.sqrt(2)));
                return  variable_return;
            }
            case ABS -> {
                variable_return.setType(Variable.VariableType.NUMBER);

                Variable vr= (Variable) i.getOperador_izquierdo().accept(this);
                if(vr!=null){
                    if((String)vr.getValue()!= Variable.VariableType.UNDEFINED.toString()){
                        if(vr.getType().equals(Variable.VariableType.NUMBER)){
                            Double value_return=(Math.abs(Double.parseDouble((String)vr.getValue())));
                            if(value_return== Math.floor(value_return)){
                                variable_return.setValue(Integer.toString(value_return.intValue()));
                                return variable_return;
                            }
                            variable_return.setValue(Double.toString(value_return));
                            return  variable_return;
                        }else{
                            errorForClient.add(new ObjectErr(vr.getId(),i.getLine(),i.getColumn(),"SEMANTICO","el dato no es tipo Number, no puedes utilizar funcion ABS"));
                            return null;
                        }
                    }else{
                        errorForClient.add(new ObjectErr(vr.getId(),i.getLine(),i.getColumn(),"SEMANTICO","El valor de la variable es undefined _ABS"));
                        return null;
                    }
                }
                errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El dato no se encontro _ ABS"));
                return null;
            }
            case CEIL -> {
                variable_return.setType(Variable.VariableType.NUMBER);
                Variable vr= (Variable) i.getOperador_izquierdo().accept(this);
                if(vr!=null){
                    if((String)vr.getValue()!= Variable.VariableType.UNDEFINED.toString()){
                        if(vr.getType().equals(Variable.VariableType.NUMBER)){
                            Double value_return=(Math.ceil(Double.parseDouble((String)vr.getValue())));
                            if(value_return== Math.floor(value_return)){
                                variable_return.setValue(Integer.toString(value_return.intValue()));
                                return variable_return;
                            }
                            variable_return.setValue(Double.toString(value_return));
                            return  variable_return;
                        }else{
                            errorForClient.add(new ObjectErr(vr.getId(),i.getLine(),i.getColumn(),"SEMANTICO","el dato no es tipo Number, no puedes utilizar funcion Ceil"));
                            return null;
                        }

                    }else{
                        errorForClient.add(new ObjectErr(vr.getId(),i.getLine(),i.getColumn(),"SEMANTICO","El valor de la variable es undefined _CEIL"));
                        return null;
                    }
                }
                errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El dato no se encontro _ CEIL"));
                return null;
            }
            case COS -> {
                variable_return.setType(Variable.VariableType.NUMBER);
                Variable vr= (Variable) i.getOperador_izquierdo().accept(this);
                if(vr!=null){
                    if((String)vr.getValue()!= Variable.VariableType.UNDEFINED.toString()){
                        if(vr.getType().equals(Variable.VariableType.NUMBER)){
                            Double value_return=(Math.cos(Double.parseDouble((String)vr.getValue())));
                            if(value_return== Math.floor(value_return)){
                                variable_return.setValue(Integer.toString(value_return.intValue()));
                                return variable_return;
                            }
                            variable_return.setValue(Double.toString(value_return));
                            return  variable_return;
                        }else{
                            errorForClient.add(new ObjectErr(vr.getId(),i.getLine(),i.getColumn(),"SEMANTICO","el dato no es tipo Number, no puedes utilizar funcion Cos"));
                            return null;
                        }
                    }else{
                        errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El valor de la variable es undefined _COS"));
                        return null;
                    }
                }
                errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El dato no se encontro _ COS"));
                return null;
            }
            case SIN -> {
                variable_return.setType(Variable.VariableType.NUMBER);
                Variable vr= (Variable) i.getOperador_izquierdo().accept(this);
                if(vr!=null){
                    if((String)vr.getValue()!= Variable.VariableType.UNDEFINED.toString()){
                        if(vr.getType().equals(Variable.VariableType.NUMBER)){
                            Double value_return=(Math.sin(Double.parseDouble((String)vr.getValue())));
                            if(value_return== Math.floor(value_return)){
                                variable_return.setValue(Integer.toString(value_return.intValue()));
                                return variable_return;
                            }
                            variable_return.setValue(Double.toString(value_return));
                            return  variable_return;
                        }else{
                            errorForClient.add(new ObjectErr(vr.getId(),i.getLine(),i.getColumn(),"SEMANTICO","el dato no es tipo Number, no puedes utilizar funcion SIN"));
                            return null;
                        }
                    }else {
                        errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El valor de la variable es undefined _SIN"));
                        return null;
                    }
                }
                errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El dato no se encontro _SIN"));
                return null;
            }
            case TAN -> {
                variable_return.setType(Variable.VariableType.NUMBER);
                Variable vr= (Variable) i.getOperador_izquierdo().accept(this);
                if(vr!=null){
                    if((String)vr.getValue()!= Variable.VariableType.UNDEFINED.toString()){
                        if(vr.getType().equals(Variable.VariableType.NUMBER)){
                            Double value_return=(Math.tan(Double.parseDouble((String)vr.getValue())));
                            if(value_return== Math.floor(value_return)){
                                variable_return.setValue(Integer.toString(value_return.intValue()));
                                return variable_return;
                            }
                            variable_return.setValue(Double.toString(value_return));
                            return  variable_return;
                        }else{
                            errorForClient.add(new ObjectErr(vr.getId(),i.getLine(),i.getColumn(),"SEMANTICO","el dato no es tipo Number, no puedes utilizar funcion TAN"));
                            return null;
                        }
                    }else {
                        errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El valor de la variable es undefined _TAN"));
                        return null;
                    }
                }
                errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El dato no se encontro _ TAN"));
                return null;
            }
            case EXP -> {
                variable_return.setType(Variable.VariableType.NUMBER);
                Variable vr= (Variable) i.getOperador_izquierdo().accept(this);
                if(vr!=null){
                    if((String)vr.getValue()!= Variable.VariableType.UNDEFINED.toString()){
                        if(vr.getType().equals(Variable.VariableType.NUMBER)){
                            Double value_return=(Math.exp(Double.parseDouble((String)vr.getValue())));
                            if(value_return== Math.floor(value_return)){
                                variable_return.setValue(Integer.toString(value_return.intValue()));
                                return variable_return;
                            }
                            variable_return.setValue(Double.toString(value_return));
                            return  variable_return;
                        }else{
                            errorForClient.add(new ObjectErr(vr.getId(),i.getLine(),i.getColumn(),"SEMANTICO","el dato no es tipo Number, no puedes utilizar funcion EXP"));
                            return null;
                        }
                    }else{
                        errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El valor de la variable es undefined _EXP"));
                        return null;
                    }
                }
                errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El dato no se encontro _ EXP"));
                return null;
            }
            case FLOOR -> {
                variable_return.setType(Variable.VariableType.NUMBER);
                Variable vr= (Variable) i.getOperador_izquierdo().accept(this);
                if(vr!=null){
                    if((String)vr.getValue()!= Variable.VariableType.UNDEFINED.toString()){
                        if(vr.getType().equals(Variable.VariableType.NUMBER)){
                            Double value_return=(Math.floor(Double.parseDouble((String)vr.getValue())));
                            if(value_return== Math.floor(value_return)){
                                variable_return.setValue(Integer.toString(value_return.intValue()));
                                return variable_return;
                            }
                            variable_return.setValue(Double.toString(value_return));
                            return  variable_return;
                        }else{
                            errorForClient.add(new ObjectErr(vr.getId(),i.getLine(),i.getColumn(),"SEMANTICO","el dato no es tipo Number, no puedes utilizar funcion FLOOR"));
                            return null;
                        }
                    }else {
                        errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El valor de la variable es undefined _FLOOR"));
                        return null;
                    }
                }
                errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El dato no se encontro _ FLOOR"));
                return null;
            }
            case POW -> {
                variable_return.setType(Variable.VariableType.NUMBER);
                Variable vr_left= (Variable) i.getOperador_izquierdo().accept(this);
                Variable vr_rigth= (Variable) i.getOperador_derecho().accept(this);
                if(vr_left!= null && vr_rigth!=null){
                    if((String)vr_left.getValue()!= Variable.VariableType.UNDEFINED.toString() && (String)vr_rigth.getValue()!= Variable.VariableType.UNDEFINED.toString()){
                        if(vr_left.getType().equals(Variable.VariableType.NUMBER) && vr_rigth.getType().equals(Variable.VariableType.NUMBER)){
                            Double value_return=(Math.pow(Double.parseDouble((String)vr_left.getValue()), Double.parseDouble((String)vr_rigth.getValue())));
                            if(value_return== Math.floor(value_return)){
                                variable_return.setValue(Integer.toString(value_return.intValue()));
                                return variable_return;
                            }
                            variable_return.setValue(Double.toString(value_return));
                            return  variable_return;
                        }else{
                            errorForClient.add(new ObjectErr(vr_left.getId()+""+vr_rigth.getId() ,i.getLine(),i.getColumn(),"SEMANTICO","Hay un dato que no es tipo Number, no puedes utilizar funcion POW"));
                            return null;
                        }
                    }else {
                        errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El valor de alguna variable es undefined _POW"));
                        return null;
                    }
                }
                errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El dato no se encontro _ POW"));
                return null;
            }
            case SQRT -> {
                variable_return.setType(Variable.VariableType.NUMBER);
                Variable vr= (Variable) i.getOperador_izquierdo().accept(this);
                if(vr!=null){
                    if((String)vr.getValue()!= Variable.VariableType.UNDEFINED.toString()){
                        if(vr.getType().equals(Variable.VariableType.NUMBER)){
                            Double value_return=(Math.sqrt(Double.parseDouble((String)vr.getValue())));
                            if(value_return== Math.floor(value_return)){
                                variable_return.setValue(Integer.toString(value_return.intValue()));
                                return variable_return;
                            }
                            variable_return.setValue(Double.toString(value_return));
                            return  variable_return;
                        }else{
                            errorForClient.add(new ObjectErr(vr.getId(),i.getLine(),i.getColumn(),"SEMANTICO","el dato no es tipo Number, no puedes utilizar funcion SQRT"));
                            return null;
                        }
                    }else {
                        errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El valor de la variable es undefined _SQRT"));
                        return null;
                    }
                }
                errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El dato no se encontro _ SQRT"));
                return null;
            }
        }
        return null;
    }

    @Override
    public Variable visit(MethodString i) {
        Variable variable_return= new Variable();
        switch (i.getType()){
            case LOWERCASE -> {
                Variable variable=this.table.getWithId(i.getId());
                if(variable!=null ){
                    if((String)variable.getValue()!= Variable.VariableType.UNDEFINED.toString()){
                        if(variable.getType().equals(Variable.VariableType.STRING)){
                            variable_return.setValue(((String)variable.getValue()).toLowerCase());
                            variable_return.setType(Variable.VariableType.STRING);
                            return variable_return;
                        }else{
                            errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","Se necesita ser STRING para metodo LOWECASE no "+variable.getType()));
                            return null;
                        }
                    }else{
                        errorForClient.add(new ObjectErr(i.getId(),i.getLine(),i.getColumn(),"SEMANTICO","El valor de la variable es undefined _lowercase"));
                        return null;
                    }
                }else{
                    errorForClient.add(new ObjectErr(i.getId(),i.getLine(),i.getColumn(),"SEMANTICO","Variable no definida"));
                    return null;
                }
            }
            case UPPERCASE -> {
                Variable variable=this.table.getWithId(i.getId());
                if(variable!=null){
                    if((String)variable.getValue()!= Variable.VariableType.UNDEFINED.toString()){
                        if(variable.getType().equals(Variable.VariableType.STRING)){
                            variable_return.setValue(((String)variable.getValue()).toUpperCase());
                            variable_return.setType(Variable.VariableType.STRING);
                            return variable_return;
                        }else{
                            errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","Se necesita ser STRING para metodo UpperCASE no "+variable.getType()));
                            return null;
                        }
                    }else {
                        errorForClient.add(new ObjectErr(i.getId(),i.getLine(),i.getColumn(),"SEMANTICO","Valor de la variable es undefined _Uppercase"));
                        return null;
                    }
                }else{
                    errorForClient.add(new ObjectErr(i.getId(),i.getLine(),i.getColumn(),"SEMANTICO","Variable no definida"));
                    return null;
                }
            }
            case LENGTH -> {
                Variable variable=this.table.getWithId(i.getId());
                if(variable!=null){
                    if((String)variable.getValue()!= Variable.VariableType.UNDEFINED.toString()){
                        if(variable.getType().equals(Variable.VariableType.STRING)){
                            variable_return.setValue(Integer.toString(((String)variable.getValue()).length()));
                            variable_return.setType(Variable.VariableType.NUMBER);
                            return variable_return;
                        }else{
                            errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","Se necesita ser STRING para metodo length no "+variable.getType()));
                            return null;
                        }
                    }else {
                        errorForClient.add(new ObjectErr(i.getId(),i.getLine(),i.getColumn(),"SEMANTICO","Valor de la variable es undefined _lenght"));
                        return null;
                    }
                }else{
                    errorForClient.add(new ObjectErr(i.getId(),i.getLine(),i.getColumn(),"SEMANTICO","Variable no definida"));
                    return null;
                }
            }
            case CHARAT -> {
                Variable variable=(Variable) i.getOperador_derecho().accept(this);
                if(variable!=null){
                    Variable vr= this.table.getWithId(i.getId());
                    if(vr!=null){
                        if((String)variable.getValue()!= Variable.VariableType.UNDEFINED.toString()){
                            if(vr.getType().equals(Variable.VariableType.STRING)){
                                if(variable.getType().equals(Variable.VariableType.NUMBER)){
                                    int r= Integer.parseInt((String)variable.getValue());
                                    if(0<= r &&r<((String) vr.getValue()).length()){
                                        variable_return.setValue(((String)vr.getValue()).charAt(Integer.parseInt((String)variable.getValue())));
                                        variable_return.setType(Variable.VariableType.NUMBER);
                                        return variable_return;
                                    }else{
                                        errorForClient.add(new ObjectErr(vr.getId(),i.getLine(),i.getColumn(),"SEMANTICO","Indice fuera de los limites -> "+(String) variable.getValue()));
                                        return null;
                                    }
                                }else{
                                    errorForClient.add(new ObjectErr(variable.getId(),i.getLine(),i.getColumn(),"SEMANTICO","Se necesita enviar una variable numero no "+variable.getType()+ " metodo CHARAT"));
                                    return null;
                                }
                            }else{
                                errorForClient.add(new ObjectErr(vr.getId(),i.getLine(),i.getColumn(),"SEMANTICO","Se necesita ser String para metodo CharAt"));
                                return null;
                            }
                        }else {
                            errorForClient.add(new ObjectErr(i.getId(),i.getLine(),i.getColumn(),"SEMANTICO","Valor de la variable es undefined _CharAt"));
                            return null;
                        }
                    }else{
                        errorForClient.add(new ObjectErr(i.getId(),i.getLine(),i.getColumn(),"SEMANTICO","No existe la variable"));
                        return null;
                    }
                }else{
                    errorForClient.add(new ObjectErr(i.getId(),i.getLine(),i.getColumn(),"SEMANTICO","dato erroneo"));
                    return null;
                }
            }
            case CONCAT -> {
                Variable variable=(Variable) i.getOperador_derecho().accept(this);
                if(variable!=null){
                    Variable vr= this.table.getWithId(i.getId());
                    if(vr!=null){
                        if((String)variable.getValue()!= Variable.VariableType.UNDEFINED.toString()){
                            if(vr.getType().equals(Variable.VariableType.STRING)){
                                if(variable.getType().equals(Variable.VariableType.STRING)){
                                    String r= ((String)variable.getValue());
                                    variable_return.setValue(((String)vr.getValue()).concat((String)variable.getValue()));
                                    variable_return.setType(Variable.VariableType.STRING);
                                    return variable_return;
                                }else{
                                    errorForClient.add(new ObjectErr(variable.getId(),i.getLine(),i.getColumn(),"SEMANTICO","Se necesita enviar una variable String no "+variable.getType() +" metodo_concat"));
                                    return null;
                                }
                            }else{
                                errorForClient.add(new ObjectErr(vr.getId(),i.getLine(),i.getColumn(),"SEMANTICO","Se necesita ser String para metodo CONCAT"));
                                return null;
                            }
                        }else {
                            errorForClient.add(new ObjectErr(i.getId(),i.getLine(),i.getColumn(),"SEMANTICO","Valor de la variable es undefined _Concat"));
                            return null;
                        }
                    }else{
                        errorForClient.add(new ObjectErr(i.getId(),i.getLine(),i.getColumn(),"SEMANTICO","No existe la variable_ CONCAT "));
                        return null;
                    }
                }else{
                    errorForClient.add(new ObjectErr(i.getId(),i.getLine(),i.getColumn(),"SEMANTICO","dato erroneo en CONCAT"));
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public Variable visit(OperacionBinaria i) {
        Variable ope_left= (Variable) i.getOperador_izquierdo().accept(this);
        Variable ope_rigth= null;
        if(i.getOperador_derecho()!=null){
            ope_rigth= (Variable) i.getOperador_derecho().accept(this);
        }else{
            ope_rigth= null;
        }
        Variable variable_return= new Variable();
        if(ope_left!=null && ope_rigth!=null && (String)ope_left.getValue()!= Variable.VariableType.UNDEFINED.toString() && (String)ope_rigth.getValue()!= Variable.VariableType.UNDEFINED.toString()){
            if(ope_left.getType().equals(ope_rigth.getType())){
                switch (i.getType()){
                    case MAS -> {
                        if(ope_left.getType().equals(Variable.VariableType.STRING)){
                            variable_return.setValue((String)ope_left.getValue()+(String)ope_rigth.getValue());
                            variable_return.setType(Variable.VariableType.STRING);
                            return variable_return;
                        } else if (ope_left.getType().equals(Variable.VariableType.NUMBER)) {
                            Double result=Double.parseDouble((String) ope_left.getValue()) +Double.parseDouble((String) ope_rigth.getValue());
                            variable_return.setType(Variable.VariableType.NUMBER);
                            if(result== Math.floor(result)){
                                variable_return.setValue(Integer.toString(result.intValue()) );
                                return variable_return;
                            }
                            variable_return.setValue(String.valueOf(result) );
                            return variable_return;
                        } else if (ope_left.getType().equals(Variable.VariableType.BIGINT)) {
                            int result= extractNumber((String) ope_left.getValue())+extractNumber((String)ope_rigth.getValue());
                            variable_return.setValue(Integer.toString(result)+"n");
                            variable_return.setType(Variable.VariableType.BIGINT);
                            return variable_return;
                        }else{
                            errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable aritmeticamente"));
                            return null;
                        }
                    }
                    case MENOS -> {
                        if(ope_left.getType().equals(Variable.VariableType.NUMBER) || ope_left.getType().equals(Variable.VariableType.BIGINT)){
                            if (ope_left.getType().equals(Variable.VariableType.NUMBER)) {
                                Double result=Double.parseDouble((String) ope_left.getValue())-Double.parseDouble((String) ope_rigth.getValue());
                                variable_return.setType(Variable.VariableType.NUMBER);
                                if(result== Math.floor(result)){
                                    variable_return.setValue(String.valueOf(result.intValue()) );
                                    return variable_return;
                                }
                                variable_return.setValue(String.valueOf(result) );
                                return variable_return;
                            } else if (ope_left.getType().equals(Variable.VariableType.BIGINT)) {
                                int result= extractNumber((String) ope_left.getValue())-extractNumber((String)ope_rigth.getValue());
                                variable_return.setValue(Integer.toString(result)+"n");
                                variable_return.setType(Variable.VariableType.BIGINT);
                                return variable_return;
                            }else{
                                errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable aritmeticamente"));
                                return null;
                            }
                        }else{
                            errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable aritmeticamente"));
                            return null;
                        }
                    }
                    case DIVIDE -> {
                        if(ope_left.getType().equals(Variable.VariableType.NUMBER) || ope_left.getType().equals(Variable.VariableType.BIGINT)){
                            String data= (String.valueOf(extractNumber((String)ope_rigth.getValue()))) ;
                            if(Double.parseDouble(data)!=0){
                                if (ope_left.getType().equals(Variable.VariableType.NUMBER)) {
                                    Double result=Double.parseDouble((String) ope_left.getValue())/Double.parseDouble((String) ope_rigth.getValue());
                                    variable_return.setType(Variable.VariableType.NUMBER);
                                    if(result== Math.floor(result)){
                                        variable_return.setValue(String.valueOf(result.intValue()) );
                                        return variable_return;
                                    }
                                    variable_return.setValue(String.valueOf(result) );
                                    return variable_return;
                                } else if (ope_left.getType().equals(Variable.VariableType.BIGINT)) {
                                    int result= extractNumber((String) ope_left.getValue()) / (extractNumber((String)ope_rigth.getValue()));
                                    variable_return.setValue(Integer.toString(result)+"n");
                                    variable_return.setType(Variable.VariableType.BIGINT);
                                    return variable_return;
                                }else{
                                    errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable aritmeticamente"));
                                    return null;
                                }
                            }else{
                                errorForClient.add(new ObjectErr(" ", i.getLine(),i.getColumn(),"SEMANTICO", "Estas diviendo por 0"));
                                return null;
                            }
                        }else{
                            errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable aritmeticamente"));
                            return null;
                        }
                    }
                    case MULTIPLY -> {
                        if(ope_left.getType().equals(Variable.VariableType.NUMBER) || ope_left.getType().equals(Variable.VariableType.BIGINT)){
                            if (ope_left.getType().equals(Variable.VariableType.NUMBER)) {
                                Double result=Double.parseDouble((String) ope_left.getValue())*Double.parseDouble((String) ope_rigth.getValue());
                                variable_return.setType(Variable.VariableType.NUMBER);
                                if(result== Math.floor(result)){
                                    variable_return.setValue(String.valueOf(result.intValue()) );
                                    return variable_return;
                                }
                                variable_return.setValue(String.valueOf(result) );
                                return variable_return;
                            } else if (ope_left.getType().equals(Variable.VariableType.BIGINT)) {
                                int result= extractNumber((String) ope_left.getValue())*extractNumber((String)ope_rigth.getValue());
                                variable_return.setValue(Integer.toString(result)+"n");
                                variable_return.setType(Variable.VariableType.BIGINT);
                                return variable_return;
                            }else{
                                errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable aritmeticamente"));
                                return null;
                            }
                        }else{
                            errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable aritmeticamente"));
                            return null;
                        }
                    }
                    case MOD -> {
                        if(ope_left.getType().equals(Variable.VariableType.NUMBER) || ope_left.getType().equals(Variable.VariableType.BIGINT)){
                            if (ope_left.getType().equals(Variable.VariableType.NUMBER)) {
                                Double result=Double.parseDouble((String) ope_left.getValue())%Double.parseDouble((String) ope_rigth.getValue());
                                variable_return.setType(Variable.VariableType.NUMBER);
                                if(result== Math.floor(result)){
                                    variable_return.setValue(String.valueOf(result.intValue()) );
                                    return variable_return;
                                }
                                variable_return.setValue(String.valueOf(result) );
                                return variable_return;
                            } else if (ope_left.getType().equals(Variable.VariableType.BIGINT)) {
                                int result= extractNumber((String) ope_left.getValue())%extractNumber((String)ope_rigth.getValue());
                                variable_return.setValue(Integer.toString(result)+"n");
                                variable_return.setType(Variable.VariableType.BIGINT);
                                return variable_return;
                            }else{
                                errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable aritmeticamente"));
                                return null;
                            }
                        }else{
                            errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable aritmeticamente"));
                            return null;
                        }
                    }
                    case MENOR_QUE -> {
                        if(ope_left.getType().equals(Variable.VariableType.NUMBER) || ope_left.getType().equals(Variable.VariableType.BIGINT)){
                            if (ope_left.getType().equals(Variable.VariableType.NUMBER)) {
                                Boolean result=Double.parseDouble((String) ope_left.getValue())< Double.parseDouble((String) ope_rigth.getValue());
                                variable_return.setValue(String.valueOf(result.toString()) );
                                variable_return.setType(Variable.VariableType.BOOLEAN);
                                return variable_return;
                            } else if (ope_left.getType().equals(Variable.VariableType.BIGINT)) {
                                Boolean result= extractNumber((String) ope_left.getValue())<extractNumber((String)ope_rigth.getValue());
                                variable_return.setValue(result.toString());
                                variable_return.setType(Variable.VariableType.BOOLEAN);
                                return variable_return;
                            }else{
                                errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable con 'Menor que'"));
                                return null;
                            }
                        }else{
                            errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable con 'Menor que'"));
                            return null;
                        }
                    }
                    case MAYOR_QUE -> {
                        if(ope_left.getType().equals(Variable.VariableType.NUMBER) || ope_left.getType().equals(Variable.VariableType.BIGINT)){
                            if (ope_left.getType().equals(Variable.VariableType.NUMBER)) {
                                Boolean result=Double.parseDouble((String) ope_left.getValue())> Double.parseDouble((String) ope_rigth.getValue());
                                variable_return.setValue(String.valueOf(result.toString()) );
                                variable_return.setType(Variable.VariableType.BOOLEAN);
                                return variable_return;
                            } else if (ope_left.getType().equals(Variable.VariableType.BIGINT)) {
                                Boolean result= extractNumber((String) ope_left.getValue())>extractNumber((String)ope_rigth.getValue());
                                variable_return.setValue(result.toString());
                                variable_return.setType(Variable.VariableType.BOOLEAN);
                                return variable_return;
                            }else{
                                errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable con 'Mayor que'"));
                                return null;
                            }
                        }else{
                            errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable con 'Mayor que'"));
                            return null;
                        }
                    }
                    case MENOR_IGUAL_QUE -> {
                        if(ope_left.getType().equals(Variable.VariableType.NUMBER) || ope_left.getType().equals(Variable.VariableType.BIGINT)){
                            if (ope_left.getType().equals(Variable.VariableType.NUMBER)) {
                                Boolean result=Double.parseDouble((String) ope_left.getValue())<= Double.parseDouble((String) ope_rigth.getValue());
                                variable_return.setValue(String.valueOf(result.toString()) );
                                variable_return.setType(Variable.VariableType.BOOLEAN);
                                return variable_return;
                            } else if (ope_left.getType().equals(Variable.VariableType.BIGINT)) {
                                Boolean result= extractNumber((String) ope_left.getValue())<=extractNumber((String)ope_rigth.getValue());
                                variable_return.setValue(result.toString());
                                variable_return.setType(Variable.VariableType.BOOLEAN);
                                return variable_return;
                            }else{
                                errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable con 'Menor igual que'"));
                                return null;
                            }
                        }else{
                            errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable con 'Menor igual que'"));
                            return null;
                        }
                    }
                    case MAYOR_IGUAL_QUE -> {
                        if(ope_left.getType().equals(Variable.VariableType.NUMBER) || ope_left.getType().equals(Variable.VariableType.BIGINT)){
                            if (ope_left.getType().equals(Variable.VariableType.NUMBER)) {
                                Boolean result=Double.parseDouble((String) ope_left.getValue())>= Double.parseDouble((String) ope_rigth.getValue());
                                variable_return.setValue(String.valueOf(result.toString()) );
                                variable_return.setType(Variable.VariableType.BOOLEAN);
                                return variable_return;
                            } else if (ope_left.getType().equals(Variable.VariableType.BIGINT)) {
                                Boolean result= extractNumber((String) ope_left.getValue())>=extractNumber((String)ope_rigth.getValue());
                                variable_return.setValue(result.toString());
                                variable_return.setType(Variable.VariableType.BOOLEAN);
                                return variable_return;
                            }else{
                                errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable con 'Mayor igual que'"));
                                return null;
                            }
                        }else{
                            errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable con 'Mayor igual que'"));
                            return null;
                        }
                    }
                    case DOBLE_IGUAL -> {
                        variable_return.setType(Variable.VariableType.BOOLEAN);
                        if(ope_left.getType().equals(Variable.VariableType.STRING)){
                            Boolean result=((String)ope_left.getValue()).equals((String)ope_rigth.getValue());
                            variable_return.setValue(result.toString());
                            return variable_return;
                        } else if (ope_left.getType().equals(Variable.VariableType.NUMBER)) {
                            Boolean result=Double.parseDouble((String) ope_left.getValue())== Double.parseDouble((String) ope_rigth.getValue());
                            variable_return.setValue(result.toString());
                            return variable_return;
                        } else if (ope_left.getType().equals(Variable.VariableType.BIGINT)) {
                            Boolean result= (extractNumber((String)ope_left.getValue()))==(extractNumber((String)ope_rigth.getValue()));
                            variable_return.setValue(result.toString());
                            return variable_return;
                        }else{
                            Boolean result= ((Boolean)ope_left.getValue())==((Boolean)ope_rigth.getValue());
                            variable_return.setValue(result.toString());
                            return variable_return;
                        }
                    }
                    case DISTINTO_QUE -> {
                        variable_return.setType(Variable.VariableType.BOOLEAN);
                        if(ope_left.getType().equals(Variable.VariableType.STRING)){
                            Boolean result=(!((String)ope_left.getValue()).equals((String)ope_rigth.getValue()));
                            variable_return.setValue(result.toString());
                            return variable_return;
                        } else if (ope_left.getType().equals(Variable.VariableType.NUMBER)) {
                            Boolean result=Double.parseDouble((String) ope_left.getValue())!= Double.parseDouble((String) ope_rigth.getValue());
                            variable_return.setValue(result.toString());
                            return variable_return;
                        } else if (ope_left.getType().equals(Variable.VariableType.BIGINT)) {
                            Boolean result= (extractNumber((String)ope_left.getValue()))!=(extractNumber((String)ope_rigth.getValue()));
                            variable_return.setValue(result.toString());
                            return variable_return;
                        }else{
                            Boolean result= ((Boolean)ope_left.getValue())!=((Boolean)ope_rigth.getValue());
                            variable_return.setValue(result.toString());
                            return variable_return;
                        }
                    }
                    case OR -> {
                        if(ope_left.getType().equals(Variable.VariableType.BOOLEAN)){
                            Boolean result= Boolean.valueOf(ope_left.getValue().toString()) || Boolean.valueOf(ope_rigth.getValue().toString());
                            variable_return.setValue(result.toString());
                            variable_return.setType(Variable.VariableType.BOOLEAN);
                            return variable_return;
                        }else{
                            errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable con 'OR'"));
                            return null;
                        }
                    }
                    case AND -> {
                        if(ope_left.getType().equals(Variable.VariableType.BOOLEAN)){
                            Boolean result= Boolean.valueOf(ope_left.getValue().toString()) && Boolean.valueOf(ope_rigth.getValue().toString());
                            variable_return.setValue(result.toString());
                            variable_return.setType(Variable.VariableType.BOOLEAN);
                            return variable_return;
                        }else{
                            errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable con 'AND'"));
                            return null;
                        }
                    }
                    case NOT -> {
                        if(ope_left.getType().equals(Variable.VariableType.BOOLEAN)){
                            Boolean result= !Boolean.valueOf(ope_left.getValue().toString());
                            variable_return.setValue((result.toString()));
                            variable_return.setType(Variable.VariableType.BOOLEAN);
                            return variable_return;
                        }else{
                            errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable con 'NOT'"));
                            return null;
                        }
                    }
                    case MAS_UNARIO -> {
                        if(ope_left.getType().equals(Variable.VariableType.NUMBER) || ope_left.getType().equals(Variable.VariableType.BIGINT)){
                            if (ope_left.getType().equals(Variable.VariableType.NUMBER)) {
                                Double result=Double.parseDouble((String) ope_left.getValue());
                                variable_return.setType(Variable.VariableType.NUMBER);
                                if(result== Math.floor(result)){
                                    variable_return.setValue(String.valueOf(result.intValue()) );
                                    return variable_return;
                                }
                                variable_return.setValue(String.valueOf(result) );
                                return variable_return;
                            } else if (ope_left.getType().equals(Variable.VariableType.BIGINT)) {
                                int result= extractNumber((String) ope_left.getValue());
                                variable_return.setValue(Integer.toString(result)+"n");
                                variable_return.setType(Variable.VariableType.BIGINT);
                                return variable_return;
                            }else{
                                errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable aritmeticamente"));
                                return null;
                            }
                        }else{
                            errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable aritmeticamente"));
                            return null;
                        }
                    }
                    case MENOS_UNARIO -> {
                        if(ope_left.getType().equals(Variable.VariableType.NUMBER) || ope_left.getType().equals(Variable.VariableType.BIGINT)){
                            if (ope_left.getType().equals(Variable.VariableType.NUMBER)) {
                                Double result=Double.parseDouble((String) ope_left.getValue());
                                variable_return.setType(Variable.VariableType.NUMBER);
                                if(result== Math.floor(result)){
                                    variable_return.setValue(String.valueOf("-"+result.intValue()) );
                                    return variable_return;
                                }
                                variable_return.setValue("-"+String.valueOf(result) );
                                return variable_return;
                            } else if (ope_left.getType().equals(Variable.VariableType.BIGINT)) {
                                int result= extractNumber((String) ope_left.getValue());
                                variable_return.setValue("-"+Integer.toString(result)+"n");
                                variable_return.setType(Variable.VariableType.BIGINT);
                                return variable_return;
                            }else{
                                errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable aritmeticamente"));
                                return null;
                            }
                        }else{
                            errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable aritmeticamente"));
                            return null;
                        }
                    }
                }
            } else if (ope_left.getType().equals(Variable.VariableType.STRING) || ope_rigth.getType().equals(Variable.VariableType.STRING)) {
                if(i.getType().equals(OperationType.MAS)){
                    variable_return.setType(Variable.VariableType.STRING);
                    variable_return.setValue((String)ope_left.getValue()+(String)ope_rigth.getValue());
                    return  variable_return;
                }else{
                    errorForClient.add(new ObjectErr(ope_left.getType()+ " y " + ope_rigth.getType(), i.getLine(),i.getColumn(),"SEMANTICO", "No se puede operar un "+ ope_left.getType()+" con " +ope_rigth.getType()));
                    return null;
                }
            }else {
                errorForClient.add(new ObjectErr(ope_left.getType()+ " y " + ope_rigth.getType(), i.getLine(),i.getColumn(),"SEMANTICO", "Los tipos de dato son distintos y no pueden ser operados"));
                return null;
            }
        }else if(ope_left!=null && (String)ope_left.getValue()!= Variable.VariableType.UNDEFINED.toString()){
            switch (i.getType()){
                case MAS_MAS -> {
                    if(ope_left.getType().equals(Variable.VariableType.BIGINT) || ope_left.getType().equals(Variable.VariableType.NUMBER)){
                        switch (ope_left.getType()){
                            case BIGINT -> {
                                Variable vr=this.table.getWithId(ope_left.getId());
                                int tmp= extractNumber((String)ope_left.getValue());
                                tmp++;
                                vr.setValue(Integer.toString(tmp)+"n");
                                return variable_return;
                            }
                            case NUMBER -> {
                                Variable vr=this.table.getWithId(ope_left.getId());
                                Double tmp= Double.parseDouble((String)ope_left.getValue());
                                tmp++;
                                variable_return.setType(Variable.VariableType.NUMBER);
                                if(tmp==Math.floor(tmp)){
                                    vr.setValue(Integer.toString(tmp.intValue()));
                                    return variable_return;
                                }
                                vr.setValue(Double.toString(tmp));
                                return  variable_return;
                            }
                        }
                    }else{
                        errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable con incrementar '++'"));
                        return null;
                    }

                }
                case MENOS_MENOS -> {
                    if(ope_left.getType().equals(Variable.VariableType.BIGINT) || ope_left.getType().equals(Variable.VariableType.NUMBER)){
                        switch (ope_left.getType()){
                            case BIGINT -> {
                                Variable vr=this.table.getWithId(ope_left.getId());
                                int tmp= extractNumber((String)ope_left.getValue());
                                tmp--;
                                vr.setValue(Integer.toString(tmp)+"n");
                                /*variable_return.setType(Variable.VariableType.BIGINT);*/
                                return variable_return;
                            }
                            case NUMBER -> {
                                Variable vr=this.table.getWithId(ope_left.getId());
                                Double tmp= Double.parseDouble((String)ope_left.getValue());
                                tmp--;
                                variable_return.setType(Variable.VariableType.NUMBER);
                                if(tmp==Math.floor(tmp)){
                                    vr.setValue(Integer.toString(tmp.intValue()));
                                    return  variable_return;
                                }
                                vr.setValue(Double.toString(tmp));
                                return  variable_return;
                            }
                        }
                    }else{
                        errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable con incrementar '++'"));
                        return null;
                    }
                }
            }
        }
        errorForClient.add(new ObjectErr(" ", i.getLine(),i.getColumn(),"SEMANTICO", "Hubo error en la operacion, hay valores nulos/undefined"));
        return null;
    }

    @Override
    public Variable visit(Parametro i) {
        Variable vr= new Variable();
        vr.setId(i.getId());
        vr.setType(i.getType());
       /* vr.setValue(null);*/
        return vr;
    }
    public String asignarValorPredeterminado(Variable.VariableType vr){
        switch (vr){
            case BIGINT -> {
                return "1n";
            }
            case STRING ->{
                return "hola";
            }
            case NUMBER -> {
                return "1";
            }
            case BOOLEAN -> {
                return "false";
            }
            default -> {
                return "1";
            }
        }
    }

    @Override
    public Variable visit(Value i) {
        Variable variable = new Variable();
        if(i.getValue()==null){
            variable.setValue(Variable.VariableType.UNDEFINED);
            return variable;
        }
        switch (i.getType()){
            case ENTERO, NUM_DECIMAL -> {
                variable.setType(Variable.VariableType.NUMBER);
                variable.setValue((String) i.getValue());
                return  variable;
            }
            case BIG_INT -> {
                variable.setType(Variable.VariableType.BIGINT);
                variable.setValue((String) i.getValue());
                return variable;
            }
            case CADENA -> {
                variable.setType(Variable.VariableType.STRING);
                variable.setValue((String) i.getValue());
                return variable;
            }
            case BOOLEAN -> {
                variable.setType(Variable.VariableType.BOOLEAN);
                variable.setValue((String) i.getValue());
                return variable;
            }
            case LITERAL -> {;
                Variable variable_busca= this.table.getWithId((String)i.getValue());
                if(variable_busca == null){
                    /*errorForClient.add(new ObjectErr(i.getValue().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "Variable no declarada"));
                   */ return null;
                }
                variable=variable_busca;
                return variable;
            }
        }
        return null;
    }

    @Override
    public Instruccion visit(While i) {
        try{
            Boolean isBreak=false;
            Variable vr=(Variable) i.getOperation().accept(this);

            if(vr.getType()== Variable.VariableType.BOOLEAN){
                TablaSimbolos tmp= new TablaSimbolos(this.table);
                tmp.setFunciones(this.table.getFunciones());
                this.table=tmp;
                while(((String)vr.getValue()).equalsIgnoreCase("true")){
                    Boolean isContinue=false;
                    if(i.getInstruccions()!=null){
                        int contador=1;
                        for(Instruccion ele: i.getInstruccions()){
                            contador++;
                            Object inst= ele.accept(this);
                            if(inst!=null){
                                if(inst.getClass().equals(Break.class)){
                                        if(inst.getClass().equals(Break.class)){
                                            /*vr=(Variable) i.getOperation().accept(this);*/
                                            isBreak=true;
                                            break;
                                        }
                                } else if (inst.getClass().equals(Continue.class)) {
                                    isContinue=true;
                                    break;
                                } else if (inst.getClass().equals(Return.class)) {
                                    Return rst= (Return) inst;
                                    if(this.table.getParent()!=null){
                                        this.table= this.table.getParent();
                                    }
                                    return (Instruccion)rst;
                                }else{
                                    /*vr=(Variable) inst;*/
                                }
                            }else{
                            }
                        }
                    }else{
                        break;
                    }
                    if(isBreak){
                        if(this.table.getParent()!=null){
                            this.table=this.table.getParent();
                        }
                        vr.setValue("false");
                        return new Break(i.getLine(),i.getColumn());
                    }else{
                        vr=(Variable) i.getOperation().accept(this);
                    }
                    if(vr==null){
                        vr= new Variable();
                        vr.setValue("false");
                    }
                }
            }else {
                errorForClient.add(new ObjectErr(null,i.getOperation().getLine(),i.getOperation().getColumn(),"Semantico","La condicion debe de ser un boolean"));
                return null;
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Instruccion visit(Return i) {
        if(this.table.getParent()==null){
            errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO", "El return solamente se usa dentro de ciclos/funciones"));
        }
        if(i.getInstruccion()!=null){
            if(i==null){
                errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO", "Error en la oepracion del Return "));
                return null;
            }else {
                return i;
            }

        }
        errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO", "Error porque no se retorna nada/ nulo"));
        return null;
    }

    @Override
    public Instruccion visit(Continue i) {
        //si si, literalmente hay que hacer un break
        if(this.table.getParent()!= null){
            return i;
        }
        errorForClient.add(new ObjectErr("CONTINUE",i.getLine(), i.getColumn(),"SEMANTICO","Solamente puedes usar CONTINUE dentro de un ciclo "));
        return null;
    }

    @Override
    public Instruccion visit(Break i) {
        //si si, literalmente hay que hacer un break
        if(this.table.getParent()!= null){
            return i;
        }
        errorForClient.add(new ObjectErr("BREAK",i.getLine(), i.getColumn(),"SEMANTICO","Solamente puedes usar break dentro de un ciclo "));
        return null;
    }

    @Override
    public Variable visit(Call i) {
        ArrayList<Function> tmp= this.table.getFunciones();
        this.table= new TablaSimbolos(this.table);
        this.table.setFunciones(tmp);
        Variable vr= new Variable();
        if(i.getAsignaciones()!=null){
            if(this.table.getFunctionWithName(i.getName())!=null){
                this.variables_nuevas_funcion.clear();
                for(Instruccion asignaciones_call: i.getAsignaciones()){
                    Object result=asignaciones_call.accept(this);
                    if(result.getClass().equals(Variable.class)){
                        Object vdd_result= ((Variable)result);
                        if(vdd_result.getClass().equals(Variable.class)){
                            vr.setId(((Variable) vdd_result).getId());
                            vr.setValue((String)((Variable) vdd_result).getValue());
                            vr.setType(((Variable) vdd_result).getType());
                            this.variables_nuevas_funcion.add(vr);
                        }else{
                            errorForClient.add(new ObjectErr(i.getName(),i.getLine(), i.getColumn(),"SEMANTICO","No retorna un valor valido"));
                            if(this.table.getParent()!=null){
                                this.table=this.table.getParent();
                            }
                            return null;
                        }
                    }
                }
                Function funcion_ejecutar= ((Function)this.table.getFunctionWithName(i.getName()));
                if(funcion_ejecutar.getParametros().size() == this.variables_nuevas_funcion.size()){
                    for(int p=0;p<funcion_ejecutar.getParametros().size(); p++){
                        funcion_ejecutar.getParametros().get(p).setValor((String)this.variables_nuevas_funcion.get(p).getValue());
                        Variable nv_parametro= new Variable();
                        nv_parametro.setId(funcion_ejecutar.getParametros().get(p).getId());
                        nv_parametro.setType(funcion_ejecutar.getParametros().get(p).getType());
                        nv_parametro.setValue((String)((Variable)i.getAsignaciones().get(p).accept(this)).getValue());
                        this.table.nuevo(nv_parametro);
                    }
                    for(Instruccion fs: funcion_ejecutar.getInstruccions()){
                        Object pp= fs.accept(this);
                        if(pp!=null){
                            if(pp.getClass().equals(Variable.class)){
                                Variable vr_return=((Variable)pp);
                                if(this.table.getParent()!=null){
                                    this.table=this.table.getParent();
                                }
                                return  vr_return;
                            } else if (pp.getClass().equals(Return.class)) {
                                Variable vr_return=(Variable) (((Return)pp).getInstruccion().accept(this));
                                if(this.table.getParent()!=null){
                                    this.table=this.table.getParent();
                                }
                                return vr_return;
                            }
                        }
                    }
                }else{
                    errorForClient.add(new ObjectErr(i.getName(),i.getLine(), i.getColumn(),"SEMANTICO","La cantidad de atributos de la funcion es distinto"));
                    this.table= this.table.getParent();
                    return null;
                }
                this.table= this.table.getParent();
                return null;
            }else{
                this.table= this.table.getParent();
                errorForClient.add(new ObjectErr(i.getName(),i.getLine(), i.getColumn(),"SEMANTICO","La funcion no existe "));
                return null;
            }
        }else{
            if(this.table.getFunctionWithName(i.getName())!=null){
                Function funcion_ejecutar= ((Function)this.table.getFunctionWithName(i.getName()));
                if(funcion_ejecutar.getType().equals(Variable.VariableType.VOID)){
                    for(Instruccion fs: funcion_ejecutar.getInstruccions()){
                        Object pp= fs.accept(this);
                        if(pp!=null){
                            if(pp.getClass().equals(Variable.class)){

                                Variable vr_return=((Variable)pp);
                                if(this.table.getParent()!=null){
                                    this.table=this.table.getParent();
                                }
                                return vr_return;
                            } else if (pp.getClass().equals(Return.class)) {
                                Variable vr_return=(Variable) (((Return)pp).getInstruccion().accept(this));
                                if(this.table.getParent()!=null){
                                    this.table=this.table.getParent();
                                }
                                return vr_return;
                            }
                        }
                    }
                    this.table=this.table.getParent();
                    return null;
                }else{
                    for(Instruccion fs: funcion_ejecutar.getInstruccions()){
                        Object pp= fs.accept(this);
                        if(pp!=null){
                            if(pp.getClass().equals(Variable.class)){
                                Variable vr_return=((Variable)pp);
                                if(this.table.getParent()!=null){
                                    this.table=this.table.getParent();
                                }
                                return vr_return;
                            } else if (pp.getClass().equals(Return.class)) {
                                Variable vr_return=(Variable) (((Return)pp).getInstruccion().accept(this));
                                if(this.table.getParent()!=null){
                                    this.table=this.table.getParent();
                                }
                                return vr_return;
                            }
                        }
                    }
                }
            }else{
                this.table= this.table.getParent();
                errorForClient.add(new ObjectErr(i.getName(),i.getLine(), i.getColumn(),"SEMANTICO","La funcion no existe "));
                return null;
            }
        }
        /*pedir la lsita de funciones de la tabla de simbolos, que guarda sus instrucciones, entonces
         * crear un metodo que guardara las funciones si estan bien y si si guardar la instruccion de funcion , para luego buscarla y ejecutar la instruccion*/
       /* vr.setId("yo mande");
        vr.setValue("3");
        vr.setType(Variable.VariableType.NUMBER);
        return vr;*/
        return null;
    }

    @Override
    public Object visit(GetTable i) {
        if(this.table!=null){
            generarTablaSym(this.table);
            return null;
        }
        return null;
    }

    public TablaSimbolos getTable() {
        return this.table;
    }

    public void setTable(final TablaSimbolos table) {
        this.table = table;
    }
    @Override
    public String toString() {
        return "Runner{" +
                "table=" + table +
                '}';
    }
    public static int extractNumber(String input) {
        if(input!=null){
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(input);

            if (matcher.find()) {
                return Integer.parseInt(matcher.group());
            }
        }

        return -1; // si no se encontró ningún número
    }
}
