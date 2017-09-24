/* --------------------------Usercode Section------------------------ */

package it.poliba.sisinflab.semanticweb.lod.losm.sparl;

import java_cup.runtime.*;
%%

/* -----------------Options and Declarations Section----------------- */

%public
%class SparqlLexer
%cupsym SparqlSym
%cup
%cupdebug
%final
%unicode
%line
%column


%char
%function next_token

%{
      StringBuffer string = new StringBuffer();

      private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
      }
      private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
      }
      
   public String getWord(){
   		return yytext();
   }
   
   public int getLine()
   {
     return yyline+1;
   }
   
   public int getCurrentPos()
   {
      return zzCurrentPos;
   }
   
   public int getColumn()
   {
     return yycolumn;
   }

   public char[] getBuffer()
   {
     return zzBuffer;
   }
        private void error(String message) {
    System.out.println("Error at line "+(yyline+1)+", column "+(yycolumn+1)+" : "+message);
  }
  
  
%}


/*
  Declarations

  Code between %{ and %}, both of which must be at the beginning of a
  line, will be copied letter to letter into the lexer class source.
  Here you declare member variables and functions that are used inside
  scanner actions.
*/
%{
    /**
     * Default constructor is needed as we will always call the yyreset
     */
 
%}


/*
  Macro Declarations

  These declarations are regular expressions that will be used later
  in the Lexical Rules Section.
*/


IRIREF = "<" ([^\<\>\"\{\}\|\^\`\\\x00-\x20])* ">"
PNAME_NS = {PN_PREFIX}? ":"
PNAME_LN = {PNAME_NS} {PN_LOCAL}
BLANK_NODE_LABEL = "_:" ( {PN_CHARS_U} | [0-9] ) (({PN_CHARS}|".")* {PN_CHARS})?

VAR1 = "?" {VARNAME}
VAR2 = "$" {VARNAME}
LANGTAG = "@" [a-zA-Z]+ ("-" [a-zA-Z0-9]+)*

// numbers
INTEGER = [0-9]+
DECIMAL = ([0-9]+ "." [0-9]*) | ("." [0-9]+)
DOUBLE = ([0-9]+ "." [0-9]* {EXPONENT}) | ("." ([0-9])+ {EXPONENT}) | (([0-9])+ {EXPONENT})

INTEGER_POSITIVE = "+"{INTEGER}
DECIMAL_POSITIVE = "+"{DECIMAL}
DOUBLE_POSITIVE = "+"{DOUBLE}
INTEGER_NEGATIVE = "-"{INTEGER}
DECIMAL_NEGATIVE = "-"{DECIMAL}
DOUBLE_NEGATIVE = "-"{DOUBLE}

EXPONENT = [eE] [+-]? [0-9]+


// strings
STRING_LITERAL1 = "'" ( ([^\x27\x5C\x0A\x0D]) | {ECHAR} )* "'"
STRING_LITERAL2 = "\"" ( ([^\x22\x5C\x0A\x0D]) | {ECHAR} )* "\""
STRING_LITERAL_LONG1 = "'''" ( ( "'" | "''" )? ( [^'\\] | {ECHAR} ) )* "'''"
STRING_LITERAL_LONG2 = "\"\"\"" ( ( "\"" | "\"\"" )? ( [^\"\\] | {ECHAR} ) )* "\"\"\""
ECHAR = [\\][tbnrf\\\"\']

// character sets etc
NIL = "("{WS}*")"
WS = [\x20\x09\x0D\x0A]
ANON = "["{WS}*"]"

PN_CHARS_BASE = [A-Za-z\u00C0-\u00D6\u00DB-\u00F6\u00f8-\u02FF\u0370-\u037D\u037F-\u1FFF\u200C-\u200D\u2070-\u218F\u2C00-\u2FEF\u3001-\uD7FF\uF900-\uFDCF\uFDF0-\uFFFD]
PN_CHARS_U = {PN_CHARS_BASE}|"_"
VARNAME = ( {PN_CHARS_U} | [0-9] ) ( {PN_CHARS_U} | [0-9\u00B7\u0300-\u036F\u203F-\u2040] )*
PN_CHARS = {PN_CHARS_U} | [\-0-9\u00B7\u0300-\u036F\u203F-\u2040]
PN_PREFIX = {PN_CHARS_BASE} (({PN_CHARS}|".")* {PN_CHARS})?
PN_LOCAL = ({PN_CHARS_U} | ":" | [0-9] | {PLX} ) (({PN_CHARS} | "." | ":" | {PLX})* ({PN_CHARS} | ':' | {PLX}) )?
PLX = ("%"([0-9] | [A-F] | [a-f])([0-9] | [A-F] | [a-f])) | {PN_LOCAL_ESC}

PN_LOCAL_ESC =  	"\\" ( "_" | "~" | "." | "-" | "!" | "$" | "&" | "'" | "(" | ")" | "*" | "+" | "," | ";" | "=" | "/" | "?" | "#" | "@" | "%" )
%x PNAME_NS
%x PNAME_LN
%x PNAME_LN2

%x IRI_REF_BODY
%x IRI_REF_END


BASE =  [bB][aA][sS][eE] 
PREFIX =  [pP][rR][eE][fF][iI][xX] 
SELECT =  [sS][eE][lL][eE][cC][tT] 
CONSTRUCT =  [cC][oO][nN][sS][tT][rR][uU][cC][tT] 
DESCRIBE =  [dD][eE][sS][cC][rR][iI][bB][eE] 
ASK =  [aA][sS][kK]
ORDER =  [oO][rR][dD][eE][rR] 
BY =  [bB][yY] 
DESC =  [dD][eE][sS][cC] 
LIMIT =  [lL][iI][mM][iI][tT] 
OFFSET =  [oO][fF][fF][sS][eE][tT] 
DISTINCT =  [dD][iI][sS][tT][iI][nN][cC][tT] 
REDUCED =  [rR][eE][dD][uU][cC][eE][dD] 
FROM =  [fF][rR][oO][mM] 
NAMED =  [nN][aA][mM][eE][dD] 
WHERE =  [wW][hH][eE][rR][eE] 
GRAPH =  [gG][rR][aA][pP][hH] 
OPTIONAL =  [oO][pP][tT][iI][oO][nN][aA][lL] 
UNION =  [uU][nN][iI][oO][nN] 
FILTER =  [fF][iI][lL][tT][eE][rR] 
ISA =  "a" 
STR =  [sS][tT][rR] 
LANG =  [lL][aA][nN][gG]
LANGMATCHES =  [lL][aA][nN][gG][mM][sA][tT][cC][hH][eE][sS] 
DATATYPE =  [dD][aA][tT][aA][tT][yY][pP][eE] 
BOUND =  [bB][oO][uU][nN][dD] 
SAMETERM =  [sS][aA][mM][eE][tT][eE][rR][mM] 
ISURI =  [iI][sS][uU][rR][iI] 
ISIRI =  [iI][sS][iI][rR][iI] 
ISBLANK =  [iI][sS][bB][lL][aA][nN][kK] 
ISLITERAL =  [iI][sS][lL][iI][tT][eE][rR][aA][lL] 
REGEX =  [rR][eE][gG][eE][xX] 

TRUE =  [tT][rR][uU][eE] 
FALSE = [fF][aA][lL][sS][eE] 





//////////
NOT  =  [nN][oO][tT]
EXISTS  =  [eE][xX][iI][sS][tT][sS]
REPLACE  =  [rR][eE][pP][lL][aA][cC][eE]
AS  =  [aA][sS]
SUBSTR  =  [sS][uU][bB][sS][tT][rR]
BNODE  =  [bB][nN][oO][dD][eE]
//IRI  =  [iI][rR][iI]
//URI  =  [uU][rR][iI]
RAND  =  [rR][aA][nN][dD]
ABS  =  [aA][bB][sS]
CEIL  =  [cC][eE][iI][lL]
FLOOR  =  [fF][lL][oO][oO][rR]
ROUND  =  [rR][oO][uU][nN][dD]
CONCAT  =  [cC][oO][nN][cC][aA][tT]
STRLEN  =  [sS][tT][rR][lL][eE][nN]
UCASE  =  [uU][cC][aA][sS][eE]
LCASE  =  [lL][cC][aA][sS][eE]
ENCODE_FOR_URI  =  [eE][nN][cC][oO][dD][eE]_[fF][oO][rR]_[uU][rR][iI]
CONTAINS  =  [cC][oO][nN][tT][aA][iI][nN][sS]
STRSTARTS  =  [sS][tT][rR][sS][tT][aA][rR][tT][sS]
STRENDS  =  [sS][tT][rR][eE][nN][dD][sS]
STRBEFORE  =  [sS][tT][rR][bB][eE][fF][oO][rR][eE]
STRAFTER  =  [sS][tT][rR][aA][fF][tT][eE][rR]
YEAR  =  [yY][eE][aA][rR]
MONTH  =  [mM][oO][nN][tT][hH]
DAY  =  [dD][aA][yY]
HOURS  =  [hH][oO][uU][rR][sS]
MINUTES  =  [mM][iI][nN][uU][tT][eE][sS]
SECONDS  =  [sS][eE][cC][oO][nN][dD][sS]
TIMEZONE  =  [tT][iI][mM][eE][zZ][oO][nN][eE]
TZ  =  [tT][zZ]
UUID  =  [uU][uU][iI][dD]
STRUUID  =  [sS][tT][rR][uU][uU][iI][dD]
MD5  =  [mM][dD]5
SHA1  =  [sS][hH][aA]1
SHA256  =  [sS][hH][aA]256
SHA384  =  [sS][hH][aA]384
SHA512  =  [sS][hH][aA]512
COALESCE  =  [cC][oO][aA][lL][eE][sS][cC][eE]
IF  =  [iI][fF]
STRLANG  =  [sS][tT][rR][lL][aA][nN][gG]
//SAMETERM  =  [sS][aA][mM][eE][tT][eE][rR][mM]
//ISIRI  =  [iI][sS][iI][rR][iI]
//ISURI  =  [iI][sS][uU][rR][iI]
//ISBLANK  =  [iI][sS][bB][lL][aA][nN]K
//ISLITERAL  =  [iI][sS][lL][iI][tT][eE][rR][aA][lL]
ISNUMERIC  =  [iI][sS][nN][uU][mM][eE][rR][iI][cC]
HAVING  =  [hH][aA][vV][iI][nN][gG]
VALUES  =  [vV][aA][lL][uU][eE][sS]
LOAD  =  [lL][oO][aA][dD]
SILENT  =  [sS][iI][lL][eE][nN][tT]
INTO  =  [iI][nN][tT][oO]
CLEAR  =  [cC][lL][eE][aA][rR]
DROP  =  [dD][rR][oO][pP]
CREATE  =  [cC][rR][eE][aA][tT][eE]
ADD  =  [aA][dD][dD]
MOVE  =  [mM][oO][vV][eE]
COPY  =  [cC][oO][pP][yY]
TO  =  [tT][oO]
INSERT  =  [iI][nN][sS][eE][rR][tT]
DATA  =  [dD][aA][tT][aA]
DELETE  =  [dD][eE][lL][eE][tT][eE]
WITH  =  [wW][iI][tT][hH]
USING  =  [uU][sS][iI][nN][gG]
ALL  =  [aA][lL][lL]
//OPTIONAL  =  [oO][pP][tT][iI][oO][nN][aA][lL]
SERVICE  =  [sS][eE][rR][vV][iI][cC][eE]
BIND  =  [bB][iI][nN][dD]
UNDEF  =  [uU][nN][dD][eE][fF]
MINUS  =  [mM][iI][nN][uU][sS]
//UNION  =  [uU][nN][iI][oO][nN]
//FILTER  =  [fF][iI][lL][tT][eE][rR]
GROUP_CONCAT  =  [gG][rR][oO][uU][pP]_[cC][oO][nN][cC][aA][tT]
SUM  =  [sS][uU][mM]
MIN  =  [mM][iI][nN]
MAX  =  [mM][aA][xX]
COUNT  =  [cC][oO][uU][nN][tT]
SAMPLE  =  [sS][aA][mM][pP][lL][eE]
AVG  =  [aA][vV][gG]
SEPARATOR  =  [sS][eE][pP][aA][rR][aA][tT][oO][rR]
GROUP = [gG][rR][oO][uP]
IN = [iI][nN]
NOW = [nN][oO][wW]
STRDT = [sS][tT][rR][dD][tT]
//////////

COMMENT = "#"[^*\n]+
%%

/* ------------------------Lexical Rules Section---------------------- */

/*
   This section contains regular expressions and actions, i.e. Java
   code, that will be executed when the scanner matches the associated
   regular expression. */

   /* YYINITIAL is the state at which the lexer begins scanning.  So
   these regular expressions will only be matched if the scanner is in
   the start state YYINITIAL. */

<YYINITIAL> {



 {PLX}                 	{ return new Symbol(SparqlSym.PLX, yytext()); }
 
  /* keywords */
  {BASE}                     { return new Symbol(SparqlSym.BASE, yytext()); }
  {PREFIX}                     { return new Symbol(SparqlSym.PREFIX, yytext()); }
  {SELECT}                     { return new Symbol(SparqlSym.SELECT, yytext()); }
  {CONSTRUCT}                     { return new Symbol(SparqlSym.CONSTRUCT, yytext()); }
  {DESCRIBE}                     { return new Symbol(SparqlSym.DESCRIBE, yytext()); }
  {ASK}                     { return new Symbol(SparqlSym.ASK, yytext()); }
  {ORDER}                     { return new Symbol(SparqlSym.ORDER, yytext()); }
  {BY}                     { return new Symbol(SparqlSym.BY, yytext()); }
  {DESC}                     { return new Symbol(SparqlSym.DESC, yytext()); }
  {LIMIT}                     { return new Symbol(SparqlSym.LIMIT, yytext()); }
  {OFFSET}                     { return new Symbol(SparqlSym.OFFSET, yytext()); }
  {DISTINCT}                     { return new Symbol(SparqlSym.DISTINCT, yytext()); }
  {REDUCED}                     { return new Symbol(SparqlSym.REDUCED, yytext()); }
  {FROM}                     { return new Symbol(SparqlSym.FROM, yytext()); }
  {NAMED}                     { return new Symbol(SparqlSym.NAMED, yytext()); }
  {WHERE}                     { return new Symbol(SparqlSym.WHERE, yytext()); }
  {GRAPH}                     { return new Symbol(SparqlSym.GRAPH, yytext()); }
  {OPTIONAL}                     { return new Symbol(SparqlSym.OPTIONAL, yytext()); }
  {UNION}                     { /*return new Symbol(SparqlSym.UNION); */}
  {FILTER}                     { return new Symbol(SparqlSym.FILTER, yytext()); }
  {ISA}                     { return new Symbol(SparqlSym.ISA, yytext()); }
  {STR}                     { return new Symbol(SparqlSym.STR, yytext()); }
  {LANG}                     { return new Symbol(SparqlSym.LANG, yytext()); }
  {LANGMATCHES}                     { return new Symbol(SparqlSym.LANGMATCHES, yytext()); }
  {DATATYPE}                     { return new Symbol(SparqlSym.DATATYPE, yytext()); }
  {BOUND}                     { return new Symbol(SparqlSym.BOUND, yytext()); }
  {SAMETERM}                     { return new Symbol(SparqlSym.SAMETERM, yytext()); }
  {ISURI}                     { return new Symbol(SparqlSym.ISURI, yytext()); }
  {ISIRI}                     { return new Symbol(SparqlSym.ISIRI, yytext()); }
  {ISBLANK}                     { return new Symbol(SparqlSym.ISBLANK, yytext()); }
  {ISLITERAL}                     { return new Symbol(SparqlSym.ISLITERAL, yytext()); }
  {REGEX}                     { return new Symbol(SparqlSym.REGEX, yytext()); }
  {TRUE}                     { return new Symbol(SparqlSym.TRUE, yytext()); }
  {FALSE}                     { return new Symbol(SparqlSym.FALSE, yytext()); }

  /* more terminals  */

{NOT}                     { return new Symbol(SparqlSym.NOT, yytext()); }
{EXISTS}                     { return new Symbol(SparqlSym.EXISTS, yytext()); }
{REPLACE}                     { return new Symbol(SparqlSym.REPLACE, yytext()); }
{AS}                     { return new Symbol(SparqlSym.AS, yytext()); }
{SUBSTR}                     { return new Symbol(SparqlSym.SUBSTR, yytext()); }
{BNODE}                     { return new Symbol(SparqlSym.BNODE, yytext()); }
//{IRI}                     { return new Symbol(SparqlSym.IRI, yytext()); }
//{URI}                     { return new Symbol(SparqlSym.URI, yytext()); }
{RAND}                     { return new Symbol(SparqlSym.RAND, yytext()); }
{ABS}                     { return new Symbol(SparqlSym.ABS, yytext()); }
{CEIL}                     { return new Symbol(SparqlSym.CEIL, yytext()); }
{FLOOR}                     { return new Symbol(SparqlSym.FLOOR, yytext()); }
{ROUND}                     { return new Symbol(SparqlSym.ROUND, yytext()); }
{CONCAT}                     { return new Symbol(SparqlSym.CONCAT, yytext()); }
{STRLEN}                     { return new Symbol(SparqlSym.STRLEN, yytext()); }
{UCASE}                     { return new Symbol(SparqlSym.UCASE, yytext()); }
{LCASE}                     { return new Symbol(SparqlSym.LCASE, yytext()); }
{ENCODE_FOR_URI}                     { return new Symbol(SparqlSym.ENCODE_FOR_URI, yytext()); }
{CONTAINS}                     { return new Symbol(SparqlSym.CONTAINS, yytext()); }
{STRSTARTS}                     { return new Symbol(SparqlSym.STRSTARTS, yytext()); }
{STRENDS}                     { return new Symbol(SparqlSym.STRENDS, yytext()); }
{STRBEFORE}                     { return new Symbol(SparqlSym.STRBEFORE, yytext()); }
{STRAFTER}                     { return new Symbol(SparqlSym.STRAFTER, yytext()); }
{YEAR}                     { return new Symbol(SparqlSym.YEAR, yytext()); }
{MONTH}                     { return new Symbol(SparqlSym.MONTH, yytext()); }
{DAY}                     { return new Symbol(SparqlSym.DAY, yytext()); }
{HOURS}                     { return new Symbol(SparqlSym.HOURS, yytext()); }
{MINUTES}                     { return new Symbol(SparqlSym.MINUTES, yytext()); }
{SECONDS}                     { return new Symbol(SparqlSym.SECONDS, yytext()); }
{TIMEZONE}                     { return new Symbol(SparqlSym.TIMEZONE, yytext()); }
{TZ}                     { return new Symbol(SparqlSym.TZ, yytext()); }
{UUID}                     { return new Symbol(SparqlSym.UUID, yytext()); }
{STRUUID}                     { return new Symbol(SparqlSym.STRUUID, yytext()); }
{MD5}                     { return new Symbol(SparqlSym.MD5, yytext()); }
{SHA1}                     { return new Symbol(SparqlSym.SHA1, yytext()); }
{SHA256}                     { return new Symbol(SparqlSym.SHA256, yytext()); }
{SHA384}                     { return new Symbol(SparqlSym.SHA384, yytext()); }
{SHA512}                     { return new Symbol(SparqlSym.SHA512, yytext()); }
{COALESCE}                     { return new Symbol(SparqlSym.COALESCE, yytext()); }
{IF}                     { return new Symbol(SparqlSym.IF, yytext()); }
{STRLANG}                     { return new Symbol(SparqlSym.STRLANG, yytext()); }
//{SAMETERM}                     { return new Symbol(SparqlSym.SAMETERM, yytext()); }
//{ISIRI}                     { return new Symbol(SparqlSym.ISIRI, yytext()); }
//{ISURI}                     { return new Symbol(SparqlSym.ISURI, yytext()); }
//{ISBLANK}                     { return new Symbol(SparqlSym.ISBLANK, yytext()); }
//{ISLITERAL}                     { return new Symbol(SparqlSym.ISLITERAL, yytext()); }
{ISNUMERIC}                     { return new Symbol(SparqlSym.ISNUMERIC, yytext()); }
{HAVING}                     { return new Symbol(SparqlSym.HAVING, yytext()); }
{VALUES}                     { return new Symbol(SparqlSym.VALUES, yytext()); }
{LOAD}                     { return new Symbol(SparqlSym.LOAD, yytext()); }
{SILENT}                     { return new Symbol(SparqlSym.SILENT, yytext()); }
{INTO}                     { return new Symbol(SparqlSym.INTO, yytext()); }
{CLEAR}                     { return new Symbol(SparqlSym.CLEAR, yytext()); }
{DROP}                     { return new Symbol(SparqlSym.DROP, yytext()); }
{CREATE}                     { return new Symbol(SparqlSym.CREATE, yytext()); }
{ADD}                     { return new Symbol(SparqlSym.ADD, yytext()); }
{MOVE}                     { return new Symbol(SparqlSym.MOVE, yytext()); }
{COPY}                     { return new Symbol(SparqlSym.COPY, yytext()); }
{TO}                     { return new Symbol(SparqlSym.TO, yytext()); }
{INSERT}                     { return new Symbol(SparqlSym.INSERT, yytext()); }
{DATA}                     { return new Symbol(SparqlSym.DATA, yytext()); }
{DELETE}                     { return new Symbol(SparqlSym.DELETE, yytext()); }
{WITH}                     { return new Symbol(SparqlSym.WITH, yytext()); }
{USING}                     { return new Symbol(SparqlSym.USING, yytext()); }
{ALL}                     { return new Symbol(SparqlSym.ALL, yytext()); }
//{OPTIONAL}                     { return new Symbol(SparqlSym.OPTIONAL, yytext()); }
{SERVICE}                     { return new Symbol(SparqlSym.SERVICE, yytext()); }
{BIND}                     { return new Symbol(SparqlSym.BIND, yytext()); }
{UNDEF}                     { return new Symbol(SparqlSym.UNDEF, yytext()); }
{MINUS}                     { return new Symbol(SparqlSym.MINUS, yytext()); }
//{UNION}                     { return new Symbol(SparqlSym.UNION, yytext()); }
//{FILTER}                     { return new Symbol(SparqlSym.FILTER, yytext()); }
{GROUP_CONCAT}                     { return new Symbol(SparqlSym.GROUP_CONCAT, yytext()); }
{SUM}                     { return new Symbol(SparqlSym.SUM, yytext()); }
{MIN}                     { return new Symbol(SparqlSym.MIN, yytext()); }
{MAX}                     { return new Symbol(SparqlSym.MAX, yytext()); }
{COUNT}                     { return new Symbol(SparqlSym.COUNT, yytext()); }
{SAMPLE}                     { return new Symbol(SparqlSym.SAMPLE, yytext()); }
{AVG}                     { return new Symbol(SparqlSym.AVG, yytext()); }
{SEPARATOR}                     { return new Symbol(SparqlSym.SEPARATOR, yytext()); }
{GROUP}                     { return new Symbol(SparqlSym.GROUP, yytext()); }
{IN}                     { return new Symbol(SparqlSym.IN, yytext()); }
{NOW}                     { return new Symbol(SparqlSym.NOW, yytext()); }



 
  /* various stuff */
  
  {IRIREF}                 	{ return new Symbol(SparqlSym.IRIREF, yytext()); }
  {PNAME_NS}                 	{ return new Symbol(SparqlSym.PNAME_NS, yytext()); }
  {PNAME_LN}                 	{ return new Symbol(SparqlSym.PNAME_LN, yytext()); }
  {BLANK_NODE_LABEL}                 	{ return new Symbol(SparqlSym.BLANK_NODE_LABEL, yytext()); }
  {VAR1}                 	{ return new Symbol(SparqlSym.VAR1, yytext()); }
  {VAR2}                 	{ return new Symbol(SparqlSym.VAR2, yytext()); }
  {LANGTAG}                 	{ return new Symbol(SparqlSym.LANGTAG, yytext()); }
  
  
  /* numeric literals */

  {INTEGER}                 	{ return new Symbol(SparqlSym.INTEGER, yytext()); }
  {DECIMAL}                 	{ return new Symbol(SparqlSym.DECIMAL, yytext()); }
  {DOUBLE}                 	{ return new Symbol(SparqlSym.DOUBLE, yytext()); }
  {INTEGER_POSITIVE}                 	{ return new Symbol(SparqlSym.INTEGER_POSITIVE, yytext()); }
  {DECIMAL_POSITIVE}                 	{ return new Symbol(SparqlSym.DECIMAL_POSITIVE, yytext()); }
  {DOUBLE_POSITIVE}                 	{ return new Symbol(SparqlSym.DOUBLE_POSITIVE, yytext()); }
  {INTEGER_NEGATIVE}                 	{ return new Symbol(SparqlSym.INTEGER_NEGATIVE, yytext()); }
  {DECIMAL_NEGATIVE}                 	{ return new Symbol(SparqlSym.DECIMAL_NEGATIVE, yytext()); }
  {DOUBLE_NEGATIVE}                 	{ return new Symbol(SparqlSym.DOUBLE_NEGATIVE, yytext()); }
  {EXPONENT}                 	{ return new Symbol(SparqlSym.EXPONENT, yytext()); }
  
  
   /* string literal */
  
  {STRING_LITERAL1}                 	{ return new Symbol(SparqlSym.STRING_LITERAL1, yytext()); }
  {STRING_LITERAL2}                 	{ return new Symbol(SparqlSym.STRING_LITERAL2, yytext()); }
  {STRING_LITERAL_LONG1}                 	{ return new Symbol(SparqlSym.STRING_LITERAL_LONG1, yytext()); }
  {STRING_LITERAL_LONG2}                 	{ return new Symbol(SparqlSym.STRING_LITERAL_LONG2, yytext()); }
  
  {COMMENT}                     {  }
  
  {ECHAR}                 	{ return new Symbol(SparqlSym.ECHAR, yytext()); }
  
  {NIL}                 	{ return new Symbol(SparqlSym.NIL, yytext()); }
  {WS}                 	{ /*return new Symbol(SparqlSym.WS);*/ }
  {ANON}                 	{ return new Symbol(SparqlSym.ANON, yytext()); }
  {PN_CHARS_BASE}                 	{ return new Symbol(SparqlSym.PN_CHARS_BASE, yytext()); }
  {PN_CHARS_U}                 	{ return new Symbol(SparqlSym.PN_CHARS_U, yytext()); }
  {VARNAME}                 	{ return new Symbol(SparqlSym.VARNAME, yytext()); }
  {PN_CHARS}                 	{ return new Symbol(SparqlSym.PN_CHARS, yytext()); }
  {PN_PREFIX}                 	{ return new Symbol(SparqlSym.PN_PREFIX, yytext()); }
  {PN_LOCAL}                 	{ return new Symbol(SparqlSym.PN_LOCAL, yytext()); }
 



  /* operators OK */
//{PN_LOCAL_ESC}  { return new Symbol(SparqlSym.OPERATOR, yytext()); }


  "(" 		                { return new Symbol(SparqlSym.PARENTHO, yytext()); }
  ")" 		                { return new Symbol(SparqlSym.PARENTHC, yytext()); }
  "{" 		                { return new Symbol(SparqlSym.CBRACKETO, yytext()); }
  "}" 		                { return new Symbol(SparqlSym.CBRACKETC, yytext()); }
  "[" 		                { return new Symbol(SparqlSym.SBRACKETO, yytext()); }
  "]" 		                { return new Symbol(SparqlSym.SBRACKETC, yytext()); }
  "." 		                { return new Symbol(SparqlSym.DOT, yytext()); }
  ";" 		                { return new Symbol(SparqlSym.SEMICOLON, yytext()); }
  "," 		                { return new Symbol(SparqlSym.COMMA, yytext()); }
  "||" 		                { return new Symbol(SparqlSym.DOUBLEPIPE, yytext()); }
  "&&" 		                { return new Symbol(SparqlSym.DOUBLEAMPERSAND, yytext()); }
  "=" 		                { return new Symbol(SparqlSym.EQUALMARK, yytext()); }
  "!=" 		                { return new Symbol(SparqlSym.DIFFERENT, yytext()); }
  "<" 		                { return new Symbol(SparqlSym.LT, yytext()); }
  ">" 		                { return new Symbol(SparqlSym.GT, yytext()); }
  "<=" 		                { return new Symbol(SparqlSym.LEQUAL, yytext()); }
  ">=" 		                { return new Symbol(SparqlSym.GEQUAL, yytext()); }
  "+" 		                { return new Symbol(SparqlSym.PLUSMARK, yytext()); }
  "-" 		                { return new Symbol(SparqlSym.MINUSMARK, yytext()); }
  "*" 		                { return new Symbol(SparqlSym.STAR, yytext()); }
  "/" 		                { return new Symbol(SparqlSym.SLASH, yytext()); }
  "!" 		                { return new Symbol(SparqlSym.EMARK, yytext()); }
  "|"                          { return new Symbol(SparqlSym.PIPE, yytext()); }
  ","                          { return new Symbol(SparqlSym.COMMA, yytext()); }
  "?"                          { return new Symbol(SparqlSym.QMARK, yytext()); }
  "^^"                          { return new Symbol(SparqlSym.CARETCARET, yytext()); }
  "^"                          { return new Symbol(SparqlSym.CARET, yytext()); }



}


/* error fallback */
.|\n              { 
		    error("Illegal character <"+ yytext()+">");
                  }
<<EOF>>                          { return new Symbol(SparqlSym.EOF, null); }
