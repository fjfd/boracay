/*
    针对IQ模块的元数据提供自定义SQL语法解析
*/

grammar DSLSQL;

@header{
    package com.hex.bigdata.udsp.dsl.parser;
}

AS: A S;
SELECT: S E L E C T;
FROM: F R O M;
MAX: M A X;
SUM: S U M;
AVG: A V G;
MIN: M I N;
COUNT: C O U N T;
DISTINCT: D I S T I N C T;
WHERE: W H E R E;
GROUP: G R O U P;
BY: B Y;
ORDER: O R D E R;
NOT: N O T;
IS:  I S;
BETWEEN: B E T W E E N;
AND:  A N D;
IN:   I N;
NULL : N U L L;
OR: O R;
ASC: A S C;
DESC: D E S C;
LIMIT: L I M I T;
OFFSET: O F F S E T;
LIKE: L I K E;
DESCRIBE: D E S C R I B E;
SHOW: S H O W;
SERVICES: S E R V I C E S;

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
    : SELECT selectElements FROM serviceName (whereClause)? (groupByCaluse)? (orderByClause)? (limitClause)?
    ;

selectElements
    : star='*'
    | selectElement (',' selectElement)*
    ;

whereClause
    : WHERE logicExpressions
    ;

logicExpressions
     : logicExpression (logicalOperator logicExpression)*
     ;

logicExpression
    : fullColumnName comparisonOperator value
    | fullColumnName BETWEEN value AND value
    | fullColumnName NOT? IN '(' value (',' value)*  ')'
    | '(' logicExpressions ')'
    ;

groupByCaluse
    :   GROUP BY groupByItem (',' groupByItem)*
    ;

orderByClause
    : ORDER BY orderByExpression (',' orderByExpression)*
    ;

limitClause
    : LIMIT (
        (offset=decimalLiteral ',')? limit=decimalLiteral
        | limit=decimalLiteral OFFSET offset=decimalLiteral
    )
    ;

orderByExpression
    : fullColumnName order=(ASC | DESC)?
    ;

groupByItem
    : fullColumnName
    ;

logicalOperator
    : AND
    | OR
    ;

comparisonOperator
    : '='
    | '>'
    | '<'
    | '<' '='
    | '>' '='
    | '!' '='
    | LIKE
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
    : fullColumnName (AS? uid)?
    | functionCall (AS? uid)?
    ;

fullColumnName
    : columnName
    ;

functionCall
    :  aggregateWindowedFunction
    ;

aggregateWindowedFunction
    : (AVG | MAX | MIN | SUM) '(' functionArg ')'
    | COUNT '(' (starArg='*' |  functionArg?) ')'
    | COUNT '(' aggregator=DISTINCT functionArgs ')'
    ;

functionArg
    :  columnName
    ;

functionArgs
    : columnName (',' columnName)*
    ;

serviceName: tmpName=ID;

columnName: ID;

uid
    : ID
    ;

WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines
