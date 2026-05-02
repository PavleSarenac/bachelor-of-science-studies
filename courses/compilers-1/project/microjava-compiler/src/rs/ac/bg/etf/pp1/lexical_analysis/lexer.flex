package rs.ac.bg.etf.pp1.lexical_analysis.generated;

import rs.ac.bg.etf.pp1.syntax_analysis.generated.sym;
import java_cup.runtime.Symbol;
import org.apache.log4j.*;

%%

%{
	private Logger logger = Logger.getLogger(Yylex.class);

	private Symbol new_symbol(int type) {
		return new Symbol(type, yyline + 1, yycolumn);
	}
	
	private Symbol new_symbol(int type, Object value) {
		return new Symbol(type, yyline + 1, yycolumn, value);
	}
%}

%public
%class Yylex

%cup
%line
%column

%xstate COMMENT

%eofval{
	return new_symbol(sym.EOF);
%eofval}

%%

" "   { }
"\b"  { }
"\t"  { }
"\r"  { }
"\n"  { }
"\f"  { }

"program"    { return new_symbol(sym.PROGRAM, yytext()); }
"break"      { return new_symbol(sym.BREAK, yytext()); }
"class"      { return new_symbol(sym.CLASS, yytext()); }
"else"       { return new_symbol(sym.ELSE, yytext()); }
"const"      { return new_symbol(sym.CONST, yytext()); }
"if"         { return new_symbol(sym.IF, yytext()); }
"new"        { return new_symbol(sym.NEW, yytext()); }
"print"      { return new_symbol(sym.PRINT, yytext()); }
"read"       { return new_symbol(sym.READ, yytext()); }
"return"     { return new_symbol(sym.RETURN, yytext()); }
"void"       { return new_symbol(sym.VOID, yytext()); }
"extends"    { return new_symbol(sym.EXTENDS, yytext()); }
"continue"   { return new_symbol(sym.CONTINUE, yytext()); }
"union"      { return new_symbol(sym.UNION, yytext()); }
"do"         { return new_symbol(sym.DO, yytext()); }
"while"      { return new_symbol(sym.WHILE, yytext()); }
"map"        { return new_symbol(sym.MAP, yytext()); }
"interface"  { return new_symbol(sym.INTERFACE, yytext()); }

"++"  { return new_symbol(sym.INCREMENT, yytext()); }
"--"  { return new_symbol(sym.DECREMENT, yytext()); }
"=="  { return new_symbol(sym.EQUALS, yytext()); }
">="  { return new_symbol(sym.GREATER_THAN_OR_EQUAL, yytext()); }
"<="  { return new_symbol(sym.LESS_THAN_OR_EQUAL, yytext()); }
"&&"  { return new_symbol(sym.LOGICAL_AND, yytext()); }
"||"  { return new_symbol(sym.LOGICAL_OR, yytext()); }
"!="  { return new_symbol(sym.NOT_EQUALS, yytext()); }
"+"   { return new_symbol(sym.PLUS, yytext()); }
"-"   { return new_symbol(sym.MINUS, yytext()); }
"*"   { return new_symbol(sym.MULTIPLY, yytext()); }
"/"   { return new_symbol(sym.DIVIDE, yytext()); }
"%"   { return new_symbol(sym.MODULO, yytext()); }
">"   { return new_symbol(sym.GREATER_THAN, yytext()); }
"<"   { return new_symbol(sym.LESS_THAN, yytext()); }
"="   { return new_symbol(sym.ASSIGN, yytext()); }
";"   { return new_symbol(sym.SEMICOLON, yytext()); }
","   { return new_symbol(sym.COMMA, yytext()); }
"."   { return new_symbol(sym.DOT, yytext()); }
"("   { return new_symbol(sym.LEFT_PARENTHESES, yytext()); }
")"   { return new_symbol(sym.RIGHT_PARENTHESES, yytext()); }
"["   { return new_symbol(sym.LEFT_BRACKET, yytext()); }
"]"   { return new_symbol(sym.RIGHT_BRACKET, yytext()); }
"{"   { return new_symbol(sym.LEFT_BRACE, yytext()); }
"}"   { return new_symbol(sym.RIGHT_BRACE, yytext()); }

<YYINITIAL> "//"  { yybegin(COMMENT); }
<COMMENT> .       { yybegin(COMMENT); }
<COMMENT> "\r\n"  { yybegin(YYINITIAL); }

[0-9]+[a-zA-Z_][^" "\b\t\r\n\f]*  { logger.error("Lexical analysis error during tokenization of '" + yytext() + "' (line " + (yyline + 1) + ", column " + (yycolumn + 1) + ")."); }
[0-9]+                            { return new_symbol(sym.NUMBER, Integer.valueOf(yytext())); }

['][']           { logger.error("Lexical analysis error during tokenization of '" + yytext() + "' (line " + (yyline + 1) + ", column " + (yycolumn + 1) + ")."); }
['][^'][^']+[']  { logger.error("Lexical analysis error during tokenization of '" + yytext() + "' (line " + (yyline + 1) + ", column " + (yycolumn + 1) + ")."); }
['][^'][']       { return new_symbol(sym.CHARACTER, yytext().charAt(1)); }

"true"   { return new_symbol(sym.BOOLEAN, true); }
"false"  { return new_symbol(sym.BOOLEAN, false); }

[a-zA-Z][a-zA-Z0-9_]*  { return new_symbol(sym.IDENTIFICATOR, yytext()); }

.  { logger.error("Lexical analysis error during tokenization of '" + yytext() + "' (line " + (yyline + 1) + ", column " + (yycolumn + 1) + ")."); }