package com.danimo;
import java_cup.runtime.Symbol;
import com.danimo.objects.Token;
import static com.danimo.ParserSecureSym.*;
%%
%public
%class SecureLex
%unicode
%line
%column
%type java_cup.runtime.Symbol
%cup

lineTerminator = \r|\n|\r\n
whiteSpace     = {lineTerminator} |  [\t\f | " "]
TRUE           =  ("true" | "TRUE")
FALSE          =  ("false" | "FALSE")
ENTERO         = [0-9]+
NUM_DECIMAL    = [0-9]+("."[0-9]+)
BIG_INT        = ([0-9]+"n")
COMENTARIO_SIMPLE =  "//".*
COMENTARIO_COM    = [/][*][^*]*[*][/]
SYM =   [@¨~$¡?·]+


%{
     private Symbol token(int type, Object value) {
      System.out.println("encontre : "+value.toString());
             return new Symbol(type,  new Token(value.toString(), type,  yycolumn + 1, yyline + 1));
     }

     private Symbol token(int type) {
      System.out.println("encontres : "+yytext() +" "+type);
             return new Symbol(type,  new Token(yytext(), type, yycolumn + 1, yyline + 1));
     }
       /* private Token token(int type){
            Token tok= new Token(yytext(), type, yycolumn + 1, yyline + 1);
            System.out.printf(tok.toString());
            return tok;
        }*/
//return token(DECIMAL, yytext());

%}

%eofval{
        return token(EOF);
//    return new Token(yytext(), EOF, yycolumn + 1, yyline + 1);
%eofval}
%eofclose
%%

<YYINITIAL>{
      {COMENTARIO_SIMPLE}   {}     /*ignore*/ // comentario simple línea
      {COMENTARIO_COM}		{}     // comentario multiple líneas
      "bigint"     { return token (T_BIGINT); }
      "number"      { return token (T_NUMBER); }
      "string"      { return token (T_STRING); }
      "boolean"     { return token (T_BOOLEAN); }
      "void"        { return token (T_VOID); }
      "undefined"   { return token (T_UNDEFINED); }
      "const"       { return token (CONST); }
      "let"         { return token (LET); }
      "console.log" { return token (CONSOLE); }
      "Number"      { return token (F_NUMBER); }
      "BigInt"      { return token (F_BIGINT); }
      "Boolean"     { return token (F_BOOLEAN); }
      "String"      { return token (F_STRING); }
      ".length"      { return token (LENGTH); }
      ".charAt"      { return token (CHARAT); }
      ".toLowerCase()" { return token(TO_LOWER_CASE); }
      ".toUpperCase()" { return token(TO_UPPER_CASE); }
      ".concat"      { return token(CONCAT); }
      "if"          { return token(IF); }
      "else"        { return token(ELSE); }
      "for"         { return token(FOR); }
      "while"       { return token(WHILE); }
      "do"          { return token(DO); }
      "break"       { return token(BREAK); }
      "continue"    { return token(CONTINUE); }
      "function"    { return token(FUNCTION); }
      "return"      { return token(RETURN); }
      "Math.E"      { return token(MATH_E); }
      "Math.PI"     { return token(MATH_PI); }
      "Math.SQRT2"  { return token(MATH_SQRT2); }
      "Math.abs"    { return token(MATH_ABS); }
      "Math.ceil"   { return token(MATH_CEIL); }
      "Math.cos"    { return token(MATH_COS); }
      "Math.sin"    { return token(MATH_SIN); }
      "Math.tan"    { return token(MATH_TAN); }
      "Math.exp"    { return token(MATH_EXP); }
      "Math.floor"  { return token(MATH_FLOOR); }
      "Math.pow"    { return token(MATH_POW); }
      "Math.sqrt"   { return token(MATH_SQRT); }
      "Math.random()" { return token(MATH_RANDOM); }
      "printAst"    { return token(PRINT_AST); }
      "getSymbolTable" { return token(GET_SYMBOL_TABLE); }
      {TRUE}           { return token(TRUE); }
      {FALSE}          { return token(FALSE); }
      ":"       { return token(DOS_PUNTOS);}
      "=="      { return token (DOBLE_IGUAL);}
      "<="      { return token(MENOR_IGUAL_QUE); }
      ">="      { return token(MAYOR_IGUAL_QUE); }
      "<"       { return token(MENOR_QUE); }
      ">"       { return token(MAYOR_QUE); }
      "!="      { return token(DISTINTO_QUE); }
      "!"       { return token(NOT);}
      "||"    { return token(OR); }
      "&&"    {return token(AND);}
      "("       {return token(L_PARENT);}
      ")"       {return token(R_PARENT);}
      "{"       {return token(L_LLAVE);}
      "}"       {return token(R_LLAVE);}
      "++"      {return token(MAS_MAS);}
      "--"    {return token(MENOS_MENOS);}
      "-"       {return token(MENOS);}
      "+"       {return token(MAS);}
      "%"       {return token(MOD);}
      "/"       {return token(DIVIDE);}
      "*"       {return token(MULTIPLY);}
      "="       {return token(IGUAL);}
      ";"       {return token(PUNTO_COMA);}
      ","       {return token(COMA);}
      (\"[^\"]*\")  { return token(CADENA, yytext().substring(1,yylength()-1)); }
      (\'[^\']*\')		 { return token(CADENA, yytext().substring(1,yylength()-1)); }
      ([a-zA-Z_][a-zA-Z0-9_]*) { return token(LITERAL); }
      {ENTERO}				{return token(ENTERO);}
      {NUM_DECIMAL}   {return token(NUM_DECIMAL);}
      {BIG_INT}   {return token(BIG_INT);}
      {SYM}    {return token(SYM);}
      {whiteSpace}  {/*ignore*/}
      [^] {return token(ERROR);}
}