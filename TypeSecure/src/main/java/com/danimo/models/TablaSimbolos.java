package com.danimo.models;

import java.util.ArrayList;

public class TablaSimbolos extends ArrayList<Variable> {
    private TablaSimbolos parent;
    private static ArrayList<Function> funciones;
    public TablaSimbolos(TablaSimbolos parent) {
        super();
        if (parent != null) {
           this.parent= parent;
        }
    }
    public boolean nuevo(Variable variable) {
        return this.add(variable);
    }

    public Variable getWithId(String id) {
        for (Variable variable : this) {
            if (variable.getId().equals(id)) {
                return variable;
            }
        }
        for (Variable variable : this.parent) {
            if (variable.getId().equals(id)) {
                return variable;
            }
        }

        return null;
    }

    public boolean exist(String id) {
        for (Variable variable : this) {
            if (variable.getId().equals(id)) {
                return true;
            }
        }
        if(!parent.isEmpty()){
            for (Variable variable : this.parent) {
                if (variable.getId().equals(id)) {
                    return true;
                }
            }
        }

        return false;
    }


}
