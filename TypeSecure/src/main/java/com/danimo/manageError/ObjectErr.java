package com.danimo.manageError;

public class ObjectErr {
    public String value;
    public int line;
    public int column;
    public String type_error;
    public String description;


    public ObjectErr(final String value,final int line, final int column, final String type_error, final String description) {
        this.value= value;
        this.line = line;
        this.column = column;
        this.type_error = type_error;
        this.description = description;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public int getLine() {
        return this.line;
    }

    public void setLine(final int line) {
        this.line = line;
    }

    public int getColumn() {
        return this.column;
    }

    public void setColumn(final int column) {
        this.column = column;
    }

    public String getType_error() {
        return this.type_error;
    }

    public void setType_error(final String type_error) {
        this.type_error = type_error;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "\nObjectErr{" +
                "value='" + value + '\'' +
                ", line=" + line +
                ", column=" + column +
                ", type_error='" + type_error + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
