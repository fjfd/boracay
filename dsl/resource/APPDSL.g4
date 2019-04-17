/*
    针对IQ模块的应用提供自定义SQL语法解析
*/

grammar APPDSL;

@header{
    package com.hex.bigdata.udsp.dsl.parser;
}

AS: A S;
COUNT: C O U N T;
SELECT: S E L E C T;
FROM: F R O M;
WHERE: W H E R E;
AND:  A N D;
LIMIT: L I M I T;
OFFSET: O F F S E T;
DESCRIBE: D E S C R I B E;
SHOW: S H O W;
SERVICES: S E R V I C E S;
LIKE: L I K E;

fragment A: [aA];
fragment B: [bB];
fragment C: [cC];
fragment D: [dD];
fragment E: [eE];
fragment F: [fF];
fragment G: [gG];
fragment H: [hH];
fragment I: [iI];
fragment J: [jJ];
fragment K: [kK];
fragment L: [lL];
fragment M: [mM];
fragment N: [nN];
fragment O: [oO];
fragment P: [pP];
fragment Q: [qQ];
fragment R: [rR];
fragment S: [sS];
fragment T: [tT];
fragment U: [uU];
fragment V: [vV];
fragment W: [wW];
fragment X: [xX];
fragment Y: [yY];
fragment Z: [zZ];
fragment HEX_DIGIT: [0-9A-F];
fragment DEC_DIGIT: [0-9];
fragment LETTER: [a-zA-Z];


ID: ('A'..'Z' | 'a'..'z') ('A'..'Z' | 'a'..'z' | '_' | '0'..'9')*;
TEXT_STRING: ( '\'' ( ('\\' '\\') | ('\'' '\'') | ('\\' '\'') | ~('\'') )* '\'');
DECIMAL_LITERAL: DEC_DIGIT+;


statement
    : selectStatement
    | describeServiceStatement
    | showServicesStatement
    ;

describeServiceStatement
    : DESCRIBE serviceName
    ;

showServicesStatement
    : SHOW SERVICES (LIKE textLiteral)?
    ;

selectStatement
    : SELECT selectElements FROM serviceName (whereClause)? (limitClause)?
    ;

selectElements
    : star='*'
    | selectElement
    ;

whereClause
    : WHERE logicExpressions
    ;

logicExpressions
     : logicExpression (logicalOperator logicExpression)*
     ;

logicExpression
    : fullColumnName comparisonOperator value
    ;

limitClause
    : LIMIT (
        (offset=decimalLiteral ',')? limit=decimalLiteral
        | limit=decimalLiteral OFFSET offset=decimalLiteral
    )
    ;

logicalOperator
    : AND
    ;

comparisonOperator
    : '='
    ;

value
    : textLiteral
    | decimalLiteral
    ;

decimalLiteral
    : DECIMAL_LITERAL
    ;

textLiteral
    : TEXT_STRING
    ;

selectElement
    : functionCall (AS? uid)?
    ;

aggregateWindowedFunction
    : COUNT '(' (starArg='*' |  functionArg?) ')'
    ;

functionArg
    :  columnName
    ;

fullColumnName
    : columnName
    ;

functionCall
    :  aggregateWindowedFunction
    ;

serviceName: tmpName=ID;

columnName: ID;

uid
    : ID
    ;

WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines
