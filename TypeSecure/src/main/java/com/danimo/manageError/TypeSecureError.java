package com.danimo.manageError;

import java.util.ArrayList;

public class TypeSecureError {
    public static TypeSecureError typeSecureError;
    public ArrayList<ObjectErr> errores;

    public TypeSecureError() {
    }

    public static TypeSecureError getTypeErrorSingleton(){
        if(typeSecureError == null){
            typeSecureError= new TypeSecureError();
        }else{
        }
        return typeSecureError;
    }
    public void clear(){
        this.errores= new ArrayList<ObjectErr>();
    }
}
