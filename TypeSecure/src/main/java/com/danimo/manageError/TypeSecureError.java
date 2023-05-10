package com.danimo.manageError;

import java.util.ArrayList;

public class TypeSecureError {
    public static TypeSecureError typeSecureError;
    public ArrayList<ObjectErr> errores;

    public TypeSecureError() {
        this.errores= new ArrayList<>();
    }

    public static TypeSecureError getTypeErrorSingleton(){
        if(typeSecureError == null){
            typeSecureError= new TypeSecureError();
        }
        return typeSecureError;
    }
    public void clear(){
        this.errores= new ArrayList<ObjectErr>();
    }

    @Override
    public String toString() {
        return "TypeSecureError{" +
                "errores=" + errores +
                '}';
    }
}
