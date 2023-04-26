package com.danimo.models;

import java.util.ArrayList;

public class TablaSimbolos extends ArrayList<Variable> {
    public TablaSimbolos(TablaSimbolos parent) {
        super();
        if (parent != null) {
            this.addAll(parent);
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
        return null;
    }

    public boolean exist(String id) {
        for (Variable variable : this) {
            if (variable.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

}
