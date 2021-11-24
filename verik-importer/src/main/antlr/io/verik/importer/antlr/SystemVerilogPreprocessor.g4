grammar SystemVerilogPreprocessor;

@header {
package io.verik.importer.antlr;
}

eval
    : additionExp
    ;

additionExp
    : atomExp (ADD atomExp | SUB atomExp)*
    ;

atomExp
    : Number
    | LPAREN additionExp RPAREN
    ;

Number
    : ('0'..'9')+ ('.' ('0'..'9')+)?
    ;

ADD
    : '+'
    ;

SUB
    : '-'
    ;

LPAREN
    : '('
    ;

RPAREN
    : ')'
    ;

WS
    : (' ' | '\t' | '\r'| '\n') -> channel(HIDDEN)
    ;