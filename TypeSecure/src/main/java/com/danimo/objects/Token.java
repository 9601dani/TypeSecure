package com.danimo.objects;

public class Token {
    public Object getTokenType;
    private String lexeme;
    private int tokenType;
    private int column;
    private int line;

    public Token(String lexeme, int tokenType, int column, int line) {
        this.lexeme = lexeme;
        this.tokenType = tokenType;
        this.column = column;
        this.line = line;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public int getTokenType() {
        return tokenType;
    }

    public Object getGetTokenType() {
        return getTokenType;
    }

    public void setGetTokenType(Object getTokenType) {
        this.getTokenType = getTokenType;
    }

    public void setTokenType(int tokenType) {
        this.tokenType = tokenType;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    @Override
  /*  public String toString() {
        return "Token{" +
                "lexeme='" + lexeme + '\'' +
                ", tokenType=" + tokenType +
                ", column=" + column +
                ", line=" + line +
                '}' + "\n";
    }*/

    public String toString() {
        return lexeme;
    }
}
