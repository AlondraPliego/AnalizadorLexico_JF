package CODIGO;
import static codigo.Tokens.*;

%%
%class Lexer
%type Tokens
%line
%column

L=[a-zA-Z]
D=[0-9]
espacio=[ \t\r\n]+

%{
    public String lexeme;
%}

%%
"Robot"                             { lexeme = yytext(); return Palabra_r; }
"="                                 { lexeme = yytext(); return Igual; }
"."                                 { lexeme = yytext(); return Punto; }
"base"|"cuerpo"|"garra"             { lexeme = yytext(); return Metodo; }
"iniciar"                           { lexeme = yytext(); return Accion; }
{L}({L}|{D}|_)*                       { lexeme = yytext(); return Identificador; }
{D}+                                { lexeme = yytext(); return Numero; }
{espacio}                           { /* Ignorar */ }
"//".*                              { /* Ignorar comentarios */ }
.                                   { lexeme = yytext(); return No_identificada; }