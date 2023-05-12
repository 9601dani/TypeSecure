package com.danimo.visitor;

import com.danimo.manageError.ObjectErr;
import com.danimo.manageError.TypeSecureError;
import com.danimo.models.*;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DebugRunner extends Visitor {
    private static ArrayList<ObjectErr> errorForClient= TypeSecureError.getTypeErrorSingleton().errores;
    private TablaSimbolos table= new TablaSimbolos(null);
    @Override
    public Variable visit(Assingment i) {
        //Obtengo el valor
        Variable variable= new Variable();
        if(i.getValue()==null){
            variable.setId(i.getId());
            variable.setValue("undefined");
            variable.setType(i.getType());
            return  variable;
        }else{
            Variable value= new Variable();
            value= (Variable) i.getValue().accept(this);
            System.out.println(value);
            if(value!=null){ //
                if(i.getType().equals(value.getType())){
                    variable.setId(i.getId());
                    variable.setValue(value.getValue());
                    variable.setType(i.getType());
                    return variable;
                }else if(i.getType().equals(Variable.VariableType.DEFINIRLA)){
                    System.out.println("entre a definirla");
                    variable.setId(i.getId());
                    variable.setValue(value.getValue());
                    variable.setType(value.getType());
                    return  variable;
                }else{
                    //   System.out.println("o aqui");
                    errorForClient.add(new ObjectErr(i.getId(),i.getLine(), i.getColumn(), "SEMANTICO","Tipo "+ i.getType()+ " no puedes ponerle un "+ value.getType()));
                    return null;
                }
            }else{
                System.out.println("aqui");
                // errorForClient.add(new ObjectErr(" ",i.getLine(), i.getColumn(), "SEMANTICO","No existe un valor para esto"));
                return null;
            }
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
        if(variable!=null){
            switch (i.getTipoCast()){
                case STRING -> {
                    try{
                        String result= (String)variable.getValue();
                        variable_return.setType(Variable.VariableType.STRING);
                        variable_return.setId(variable.getId());
                        if(result.equalsIgnoreCase("undefined") || result.equalsIgnoreCase("")){
                            variable_return.setValue("undefined");
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
                                Double result= (double)extractNumber((String)variable.getValue());
                                if(result==-1){
                                    new Exception("error");
                                }
                                variable_return.setType(Variable.VariableType.NUMBER);
                                variable_return.setId(variable.getId());
                                if(result== Math.floor(result)){
                                    variable_return.setValue(String.valueOf(result.intValue()));
                                    return variable_return;
                                }
                                variable_return.setValue(Double.toString(result));
                                return variable_return;
                            }
                            case NUMBER -> {
                                /*Double result= Double.parseDouble((String)variable.getValue());
                                variable_return.setType(Variable.VariableType.NUMBER);
                                variable_return.setId(variable.getId());
                                if(result== Math.floor(result)){
                                    variable_return.setValue(String.valueOf(result.intValue()) );
                                    return variable_return;
                                }
                                variable_return.setValue(Double.toString(result));
                                return variable_return;*/
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
                                    System.out.println("desde cast");
                                    variable_return.setValue(String.valueOf(result.intValue()) );
                                    System.out.println(variable_return.getValue());
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
                   /* System.out.println("entre el cast de bigInt");
                    System.out.println("-"+variable.getType());*/
                    try{
                        switch (variable.getType()){
                            case BIGINT -> {
                                return  variable;
                            }
                            case BOOLEAN -> {
                                System.out.println("jauno");
                                //REVISAR ERROR
                               /* System.out.println("---------"+(String)variable.getValue());*/
                                System.out.println("algo pmt");
                                Boolean result=Boolean.valueOf((String)variable.getValue());
                                System.out.println("result "+result);
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
                                if(valor.equalsIgnoreCase(" ")){
                                    variable_return.setValue("0n");
                                    return variable_return;
                                }
                                // error al castear
                                int result=Integer.parseInt((String)((String) variable.getValue()).replace("n",""));
                                System.out.println("- "+result);
                                if(result==-1){
                                    new Exception("error");
                                }
                                variable_return.setValue(Integer.toString(result)+"n");
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
            //errorForClient.add(new ObjectErr("mande error en cast",i.getLine(), i.getColumn(), "SEMANTICO","Variable no definida"));
            System.out.println("casteo null");
            return null;
        }
        return null;
    }

    @Override
    public Variable visit(ConsoleLog i) {
        /*System.out.println("DEBUG CONSOLE");
        i.getInstruccions().forEach(ele->{
            ele.accept(this);
        });*/
        return null;
    }

    @Override
    public void visit(Declare i) {
        if(i.getAsignaciones()!=null){
            if(i.getType_modi()!=null){
                i.getAsignaciones().forEach(ele->{
                    Variable vr1= new Variable();
                    vr1.setType_modi(i.getType_modi());
                    Variable asigment=(Variable) ele.accept(this);
                    if(asigment!= null){
                        if(i.getType_modi().equals(Variable.TypeV.CONST) && asigment.getValue().equals("undefined")){
                            errorForClient.add(new ObjectErr(asigment.getId(), i.getLine(),i.getColumn(),"SEMANTICO", "Una variable CONST debe tener un valor asignado"));
                        }else{
                            vr1.setId(asigment.getId());
                            vr1.setType(asigment.getType());
                            vr1.setValue(asigment.getValue());
                            this.table.nuevo(vr1);
                        }
                    }else{
                        //errorForClient.add(new ObjectErr(" ", i.getLine(),i.getColumn(),"SEMANTICO", "No se pudo asignar un valor"));
                        System.out.println("la asignacion en nula");
                    }
                });
            }else{
                errorForClient.add(new ObjectErr(" ", i.getLine(),i.getColumn(),"SEMANTICO", "No se asigno si la variable es const o let"));
                System.out.println("error en el tipo de dato en la declaracion");
            }
        }

    }

    @Override
    public Instruccion visit(DoWhile i) {
       /* System.out.println("DEBUG DOWHILE");
        i.getOperation().accept(this);
        i.getInstruccions().forEach(ele->{
            ele.accept(this);
        });*/
        return null;
    }

    @Override
    public Instruccion visit(ElseState i) {
       /*i.getInstruccions().forEach(ele->{
           ele.accept(this);
       });*/
        if(i!=null){
            i.getInstruccions().forEach(ele->{
                ele.accept(this);
            });
        }
        return null;
    }

    @Override
    public Instruccion visit(ForState i) {
       /* System.out.println("DEBUG FOR");
        i.getDeclaraciones().forEach(ele->{
            ele.accept(this);
        });
        i.getCondition().accept(this);
        i.getSalto().accept(this);
        i.getInstruccions().forEach(ele->{
            ele.accept(this);
        });*/
        return null;
    }

    @Override
    public Instruccion visit(Function i) {
        /*System.out.println("DEBUG FUNCTION");*/
        return null;
    }

    @Override
    public Instruccion visit(IfState i) {
        /*Variable variable=(Variable) i.getInstruccion().accept(this);
        if(variable!=null){
            if(Boolean.valueOf((String) variable.getValue()) ==true){
                i.getBloque_verdadero().forEach(ele->{
                    ele.accept(this);
                });
            }else{
               i.getBloque_falso().accept(this);
            }
        }*/
        i.getInstruccion().accept(this);
        i.getBloque_verdadero().forEach(ele->{
            ele.accept(this);
        });
        if(i.getBloque_falso()!=null){
            i.getBloque_falso().accept(this);
            return null;
        }
        return null;

    }

    @Override
    public Variable visit(MethodMath i) {
       /* System.out.println("DEBUG METHODMATH");*/
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
                }
                errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El dato no se encontro _ ABS"));
                return null;
            }
            case CEIL -> {
                variable_return.setType(Variable.VariableType.NUMBER);
                Variable vr= (Variable) i.getOperador_izquierdo().accept(this);
                if(vr!=null){
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
                }
                errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El dato no se encontro _ CEIL"));
                return null;
            }
            case COS -> {
                variable_return.setType(Variable.VariableType.NUMBER);
                Variable vr= (Variable) i.getOperador_izquierdo().accept(this);
                if(vr!=null){
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
                }
                errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El dato no se encontro _ COS"));
                return null;
            }
            case SIN -> {
                variable_return.setType(Variable.VariableType.NUMBER);
                Variable vr= (Variable) i.getOperador_izquierdo().accept(this);
                if(vr!=null){
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
                }
                errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El dato no se encontro _ SIN"));
                return null;
            }
            case TAN -> {
                variable_return.setType(Variable.VariableType.NUMBER);
                Variable vr= (Variable) i.getOperador_izquierdo().accept(this);
                if(vr!=null){
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
                }
                errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El dato no se encontro _ TAN"));
                return null;
            }
            case EXP -> {
                variable_return.setType(Variable.VariableType.NUMBER);
                Variable vr= (Variable) i.getOperador_izquierdo().accept(this);
                if(vr!=null){
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
                }
                errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El dato no se encontro _ EXP"));
                return null;
            }
            case FLOOR -> {
                variable_return.setType(Variable.VariableType.NUMBER);
                Variable vr= (Variable) i.getOperador_izquierdo().accept(this);
                if(vr!=null){
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
                }
                errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El dato no se encontro _ FLOOR"));
                return null;
            }
            case POW -> {
                variable_return.setType(Variable.VariableType.NUMBER);
                Variable vr_left= (Variable) i.getOperador_izquierdo().accept(this);
                Variable vr_rigth= (Variable) i.getOperador_derecho().accept(this);
                if(vr_left!=null && vr_rigth!=null){
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
                }
                errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El dato no se encontro _ POW"));
                return null;
            }
            case SQRT -> {
                variable_return.setType(Variable.VariableType.NUMBER);
                Variable vr= (Variable) i.getOperador_izquierdo().accept(this);
                if(vr!=null){
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
                }
                errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","El dato no se encontro _ SQRT"));
                return null;
            }
        }
        return null;
    }

    @Override
    public Variable visit(MethodString i) {
        /*System.out.println("DEBUG METHODSTRING");*/
        Variable variable_return= new Variable();
        switch (i.getType()){
            case LOWERCASE -> {
                Variable variable=this.table.getWithId(i.getId());
                if(variable!=null){
                    if(variable.getType().equals(Variable.VariableType.STRING)){
                        variable_return.setValue(((String)variable.getValue()).toLowerCase());
                        variable_return.setType(Variable.VariableType.STRING);
                        return variable_return;
                    }else{
                        errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","Se necesita ser STRING para metodo LOWECASE"));
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
                    if(variable.getType().equals(Variable.VariableType.STRING)){
                        variable_return.setValue(((String)variable.getValue()).toUpperCase());
                        variable_return.setType(Variable.VariableType.STRING);
                        return variable_return;
                    }else{
                        errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","Se necesita ser STRING para metodo UpperCASE"));
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
                    if(variable.getType().equals(Variable.VariableType.STRING)){
                        variable_return.setValue(((String)variable.getValue()).length());
                        variable_return.setType(Variable.VariableType.NUMBER);
                        return variable_return;
                    }else{
                        errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO","Se necesita ser STRING para metodo length"));
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
                                errorForClient.add(new ObjectErr(variable.getId(),i.getLine(),i.getColumn(),"SEMANTICO","Se necesita enviar una variable numero"));
                                return null;
                            }
                        }else{
                            errorForClient.add(new ObjectErr(vr.getId(),i.getLine(),i.getColumn(),"SEMANTICO","Se necesita ser String para metodo CharAt"));
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
        }
        return null;
    }

    @Override
    public Variable visit(OperacionBinaria i) {
        Variable ope_left= (Variable) i.getOperador_izquierdo().accept(this);
        Variable ope_rigth= (Variable) i.getOperador_derecho().accept(this);
        Variable variable_return= new Variable();
        /*System.out.println(ope_left.toString());
        System.out.printf(ope_rigth.toString());
        System.out.printf(i.getType().toString());*/
        if(ope_left!=null && ope_rigth!=null ){
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
                                variable_return.setValue(String.valueOf(result.intValue()) );
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
                                    System.out.println("hare un number");
                                    Double result=Double.parseDouble((String) ope_left.getValue())/Double.parseDouble((String) ope_rigth.getValue());
                                    variable_return.setType(Variable.VariableType.NUMBER);
                                    if(result== Math.floor(result)){
                                        variable_return.setValue(String.valueOf(result.intValue()) );
                                        return variable_return;
                                    }
                                    variable_return.setValue(String.valueOf(result) );
                                    return variable_return;
                                } else if (ope_left.getType().equals(Variable.VariableType.BIGINT)) {
                                    System.out.println("hare un bignit");
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
                            Boolean result=((String)ope_left.getValue())==((String)ope_rigth.getValue());
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
                            System.out.println("retorne en AND"+result);
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
                    case MAS_MAS -> {
                        if(ope_left.getType().equals(Variable.VariableType.BIGINT) && ope_left.getType().equals(Variable.VariableType.NUMBER)){
                            switch (ope_left.getType()){
                                case BIGINT -> {
                                        /*aqui sigue le metodo, revisar cup*/
                                }
                            }
                        }else{
                            errorForClient.add(new ObjectErr(ope_left.getType().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "El tipo no es operable con incrementar '++'"));
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
                    System.out.println("diferentes operaciones");
                    errorForClient.add(new ObjectErr(ope_left.getType()+ " y " + ope_rigth.getType(), i.getLine(),i.getColumn(),"SEMANTICO", "No se puede operar un "+ ope_left.getType()+" con " +ope_rigth.getType()));
                    return null;
                }
            }else {
                errorForClient.add(new ObjectErr(ope_left.getType()+ " y " + ope_rigth.getType(), i.getLine(),i.getColumn(),"SEMANTICO", "Los tipos de dato son distintos y no pueden ser operados"));
                return null;
            }
        }
       // errorForClient.add(new ObjectErr(" ", i.getLine(),i.getColumn(),"SEMANTICO", "Hubo error en la operacion, hay valores nulos"));
        System.out.println("nulos");
        return null;
    }

    @Override
    public Variable visit(Parametro i) {
        /*System.out.println("DEBUG PARAMETRO");*/
        return null;
    }

    @Override
    public Variable visit(Value i) {
        Variable variable = new Variable();
        switch (i.getType()){
            case ENTERO, NUM_DECIMAL -> {
                variable.setType(Variable.VariableType.NUMBER);
                variable.setValue((String) i.getValue());
                System.out.println("retorne un numero");
                return  variable;
            }
            case BIG_INT -> {
                variable.setType(Variable.VariableType.BIGINT);
                variable.setValue((String) i.getValue());
                System.out.println("retorne un bigint");
                return variable;
            }
            case CADENA -> {
                variable.setType(Variable.VariableType.STRING);
                variable.setValue((String) i.getValue());
                System.out.println("retorne un string");
                return variable;
            }
            case BOOLEAN -> {
                variable.setType(Variable.VariableType.BOOLEAN);
                variable.setValue((String) i.getValue());
                System.out.println("retorne un boolean");
                return variable;
            }
            case LITERAL -> {
                Variable variable_busca= this.table.getWithId((String)i.getValue());
                if(variable_busca == null){
                    errorForClient.add(new ObjectErr(i.getValue().toString(), i.getLine(),i.getColumn(),"SEMANTICO", "Variable no declarada"));
                    return null;
                }
                variable=variable_busca;
                return variable;
            }
        }
        if(i.getValue()==null){
            variable.setValue(Variable.VariableType.UNDEFINED);
            System.out.println("retorne undefined");
            return variable;
        }
        System.out.println("aqui voy a retornar null");
        return null;
    }

    @Override
    public Instruccion visit(While i) {
       /* System.out.println("DEBUG WHILE");
        i.getOperation().accept(this);
        i.getInstruccions().forEach(ele->{
            ele.accept(this);
        });*/
        return null;
    }

    public TablaSimbolos getTable() {
        return this.table;
    }

    public void setTable(final TablaSimbolos table) {
        this.table = table;
    }
    public static int extractNumber(String input) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }

        return -1; // si no se encontró ningún número
    }

    @Override
    public String toString() {
        return "DebugRunner{" +
                "table=" + table +
                '}';
    }
}
