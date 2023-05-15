package com.danimo.visitor;

import com.danimo.manageError.ObjectErr;
import com.danimo.manageError.TypeSecureError;
import com.danimo.models.*;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.danimo.Main.view_console;

public class DebugRunner extends Visitor {
    private static ArrayList<ObjectErr> errorForClient= TypeSecureError.getTypeErrorSingleton().errores;
    private TablaSimbolos table= new TablaSimbolos(null);
    @Override
    public Variable visit(Assingment i) {
        //Obtengo el valor
        Variable variable= new Variable();
        if(i.getValue()==null){
            variable.setId(i.getId());
            variable.setValue(Variable.VariableType.UNDEFINED.toString());
            variable.setType(i.getType());
            return  variable;
        }else{
            Variable value= new Variable();
            value= (Variable) i.getValue().accept(this);
            /*System.out.println(value);*/
            if(value!=null){ //
                if(i.getType().equals(value.getType())){
                    variable.setId(i.getId());
                    variable.setValue(value.getValue());
                    variable.setType(i.getType());
                    return variable;
                }else if(i.getType().equals(Variable.VariableType.DEFINIRLA)){
                    /*System.out.println("entre a definirla");*/
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
        if(variable!=null /*&& (String)variable.getValue()!= Variable.VariableType.UNDEFINED.toString()*/){
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
                                   /* System.out.println(variable_return.getValue());*/
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
                                // error al castear
                               /* int result=Integer.parseInt((String)((String) variable.getValue()).replace("n",""));*/
                                /*if(result==-1){
                                    new Exception("error");
                                }*/
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
                System.out.println("casteo null");
                return null;
            }
           errorForClient.add(new ObjectErr(null,i.getLine(), i.getColumn(), "SEMANTICO","Variable no definida/undefined"));
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
        ArrayList<Variable> arrayDatos= new ArrayList<>();
        if(i.getInstruccions()!=null){
            i.getInstruccions().forEach(instrucciones_console -> {
                Variable txts = (Variable) instrucciones_console.accept(this);
                if(txts!=null){
                    String value;
                    //  System.out.println("Variable-> "+txts.toString());
            /*if(txts.getValue().equals(Variable.VariableType.NUMBER)){
                value = Integer.toString((int)txts.getValue());
            }else{
                value = (String) txts.getValue();
            }

            if (value != null && !"undefined".equals(value) && !"UNDEFINED".equals(value)) {
                arrayDatos.add(txts);
            }*/
                    arrayDatos.add(txts);
                }
            });
            String txt="";
            for(int p=0; p<arrayDatos.size();p++){
                txt+=(String)  arrayDatos.get(p).getValue();
            }
            if(txt.equals(null)){
                errorForClient.add(new ObjectErr(null, i.getLine(),i.getColumn(),"Semantico","El valor es nulo en console"));
                return null;
            }
            System.out.println("CONSOLE \n" + txt);
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
                        if(asigment!= null && asigment.getType()!=null){
                            if(i.getType_modi().equals(Variable.TypeV.CONST) && asigment.getValue().equals("undefined")){
                                errorForClient.add(new ObjectErr(asigment.getId(), i.getLine(),i.getColumn(),"SEMANTICO", "Una variable CONST debe tener un valor asignado"));
                            }else{
                                vr1.setId(asigment.getId());
                                vr1.setType(asigment.getType());
                                vr1.setValue(asigment.getValue());
                                this.table.nuevo(vr1);
                            }
                        }else{
                            errorForClient.add(new ObjectErr(null, i.getLine(),i.getColumn(),"SEMANTICO", "No se pudo asignar un valor, no se le asigno valor/tipo de variable"));
                            System.out.println("la asignacion en nula");
                        }
                    }
                });
            }else{
                errorForClient.add(new ObjectErr(null, i.getLine(),i.getColumn(),"SEMANTICO", "No se asigno si la variable es const o let"));
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
        Variable vr=(Variable) i.getOperation().accept(this);
        if(vr!=null){
            TablaSimbolos tmp= new TablaSimbolos(this.table);
            this.table=tmp;
/*        for(Instruccion ele: i.getInstruccions()){
            if(ele.getClass().equals(Break.class)){
                break;
            }
            ele.accept(this);
        }*/
            i.getInstruccions().forEach(ele->{
                if(ele.getClass().equals(Break.class)){

                }
                ele.accept(this);

            });
            this.table=this.table.getParent();
            return null;
        }
        return null;
    }

    @Override
    public Instruccion visit(ElseState i) {
       /*i.getInstruccions().forEach(ele->{
           ele.accept(this);
       });*/
        TablaSimbolos table_else= new TablaSimbolos(this.table);
        this.table= table_else;
        if(i!=null){
            i.getInstruccions().forEach(ele->{
                ele.accept(this);
            });
        }
        this.table= this.table.getParent();
        return null;
    }

    @Override
    public Instruccion visit(ForState i) {
        System.out.println("DEBUG FOR");
        TablaSimbolos tmp= new TablaSimbolos(this.table);
        this.table= tmp;
        /*i.getDeclaraciones().accept(this);*/
        i.getDeclaraciones().forEach(declaraciones_for->{
            declaraciones_for.accept(this);
            System.out.println(declaraciones_for.getClass());
        });
        i.getCondition().accept(this);
        i.getSalto().accept(this);
        if(i.getInstruccions()!=null){
            i.getInstruccions().forEach(instrucciones_for->{
                instrucciones_for.accept(this);
            });
        }
        this.table= this.table.getParent();
        return null;
    }

    @Override
    public Instruccion visit(Function i) {
        System.out.println("DEBUG FUNCTION");
        this.table= new TablaSimbolos(this.table);
        ArrayList<Variable> vrs= new ArrayList<>();
        if(i.getParametros()!=null){
            i.getParametros().forEach(parametros_fun->{
                /*if(parametros_fun==null){
                    this.table.nuevo(parametros_fun.accept(this));
                }*/

            });
        }
        if(i.getInstruccions()!=null){
            System.out.println("no es nulo");
            vrs= getVariablesReturn(i.getInstruccions(), new ArrayList<>());
        }
        vrs.forEach(System.out::println);
        this.table=this.table.getParent();

/*        TablaSimbolos tabla_tmp=  new TablaSimbolos(this.table);
        this.table= tabla_tmp;
        if(i.getInstruccions()!=null){
            for(Instruccion ele: i.getInstruccions()){
                i.accept(this);
                return null;
            }
            return null;
        }
        this.table=this.table.getParent();*/
        /*function sayHello2(name: string): void {
	const greeting = 'Hello ' + name + '!';
	console.log(greeting);
}
*/
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
        try{

            i.getInstruccion().accept(this);
            /*        TablaSimbolos tb= this.table;*/
            TablaSimbolos tmp_if= new TablaSimbolos(this.table);
            this.table=tmp_if;
            for(Instruccion ele: i.getBloque_verdadero()){
                ele.accept(this);
                /*if(ele.getClass().equals(Break.class)){
                    Break eleme= new Break(i.getLine(), i.getColumn());
                    return(Instruccion) eleme;
                }
                return ele;*/
            }
            System.out.println("IF->\n"+this.table);
            this.table= this.table.getParent();
            TablaSimbolos tmp_else= new TablaSimbolos(this.table);
            this.table=tmp_else;
            if(i.getBloque_falso()!=null){
                i.getBloque_falso().accept(this);
                return null;
            }
            this.table= this.table.getParent();
            System.out.println("ELSE->\n"+this.table);
            return null;
        }catch (Exception e){
            return null;
        }


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
        /*System.out.println("DEBUG METHODSTRING");*/
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
        /*System.out.println(ope_left.toString());
        System.out.printf(ope_rigth.toString());
        System.out.printf(i.getType().toString());*/
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
        }else if(ope_left!=null && (String)ope_left.getValue()!= Variable.VariableType.UNDEFINED.toString()){
            switch (i.getType()){
                case MAS_MAS -> {
                    System.out.println((String) ope_left.getValue());
                    if(ope_left.getType().equals(Variable.VariableType.BIGINT) || ope_left.getType().equals(Variable.VariableType.NUMBER)){
                        switch (ope_left.getType()){
                            case BIGINT -> {
                                Variable vr=this.table.getWithId(ope_left.getId());
                                int tmp= extractNumber((String)ope_left.getValue());
                                tmp++;
                                vr.setValue(Integer.toString(tmp)+"n");
                                /*variable_return.setType(Variable.VariableType.BIGINT);*/
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
       /* System.out.println("nulos");*/
        return null;
    }

    @Override
    public Variable visit(Parametro i) {
        /*System.out.println("DEBUG PARAMETRO");*/
        Variable vr= new Variable();
        vr.setId(i.getId());
        vr.setType(i.getType());
        return vr;
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
                System.out.println("retorne variable "+ variable.toString());
                return variable;
            }
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
        try{
            Variable vr=(Variable) i.getOperation().accept(this);
            TablaSimbolos tmp= new TablaSimbolos(this.table);
            this.table=tmp;
            //este metodo es para ver el break;
        /*for(Instruccion ele: i.getInstruccions()){
            Instruccion inst=(Instruccion) ele.accept(this);
            if(inst.getClass().equals(Break.class)){
                System.out.println("recibi un break, en el while");
                break;
            }
        }*/
            i.getInstruccions().forEach(ele->{
                ele.accept(this);
                System.out.println("while");
                System.out.println(ele.getClass());
            });
            this.table=this.table.getParent();
            return null;
        }catch (Exception e){
            return null;
        }
       /* if(vr!=null &&  vr.getValue()!=null && (String) vr.getValue()!="undefined"){

        }else{
            errorForClient.add(new ObjectErr(null, i.getLine(),i.getColumn(),"SEMANTICO", "Error de comparacion en While"));
            return null;
        }*/
    }

    @Override
    public Variable visit(Return i) {
        if(this.table.getParent()==null){
            errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO", "El return solamente se usa dentro de ciclos/funciones"));
        }
        if(i.getInstruccion()!=null){
            Variable vr= (Variable) i.getInstruccion().accept(this);
            if(vr==null){
                errorForClient.add(new ObjectErr(null,i.getLine(),i.getColumn(),"SEMANTICO", "Error en la oepracion del Return "));
                return null;
            }else {
                return vr;
            }

        }
        return null;
    }

    @Override
    public Instruccion visit(Continue i) {
        return null;
    }

    @Override
    public Instruccion visit(Break i) {
        //si si, literalmente hay que hacer un break
        if(this.table.getParent()!= null){
            System.out.println("le retorne una instruccion");
            return i;
        }
        errorForClient.add(new ObjectErr("BREAK",i.getLine(), i.getColumn(),"SEMANTICO","Solamente puedes usar break dentro de un ciclo "));
        return i;
    }

    @Override
    public Variable visit(Call i) {
        System.out.println("ENCONTRE UN CALL");
        Variable vr= new Variable();
        if(i.getAsignaciones()!=null){
            i.getAsignaciones().forEach(asignaciones_call->{
                asignaciones_call.accept(this);
            });
            System.out.println("aqui mandare algo");
            vr.setId("yo mande");
            vr.setValue(2);
            vr.setType(Variable.VariableType.NUMBER);
            return vr;
        }
        return vr;
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
    public ArrayList<Variable> getVariablesReturn(ArrayList<Instruccion> f, ArrayList<Variable> vrs){
        if(f!=null){
            for(Instruccion instr: f){
                if(instr.getClass().equals(While.class)){
                    getVariablesReturn(((While) instr).getInstruccions(),vrs);
                }else if(instr.getClass().equals(DoWhile.class)){
                    getVariablesReturn(((DoWhile) instr).getInstruccions(),vrs);
                } else if (instr.getClass().equals(ForState.class)) {
                    getVariablesReturn(((ForState)instr).getInstruccions(),vrs);
                } else if (instr.getClass().equals(IfState.class)) {
                    if(((IfState) instr).getBloque_verdadero()!=null){
                        getVariablesReturn(((IfState) instr).getBloque_verdadero(),vrs);
                        System.out.println("dnaiel1");
                    }
                    if (((IfState) instr).getBloque_falso()!=null) {
                        if(((IfState) instr).getBloque_falso() instanceof ElseState pnElse){
                            getVariablesReturn(pnElse.getInstruccions(),vrs);
                        }else if(((IfState) instr).getBloque_falso().getClass().equals(IfState.class)){
                            System.out.println("dnaiel3");
                            getVariablesReturn(((IfState) instr).getBloque_verdadero(),vrs);
                        }
                    }
                } else if (instr.getClass().equals(ElseState.class)) {
                    getVariablesReturn(((ElseState) instr).getInstruccions(),vrs);
                } else if (instr.getClass().equals(Return.class)) {
                    Variable vr_return= new Variable();
                    vr_return= (Variable) ((Return) instr).getInstruccion().accept(this);
                    vrs.add(vr_return);
                }else {
                    instr.accept(this);
                }
            }
        }
        return vrs;
    }
}
