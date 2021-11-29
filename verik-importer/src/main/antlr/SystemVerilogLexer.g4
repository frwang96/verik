lexer grammar SystemVerilogLexer;

COMMA       : ',' ;
SEMICOLON   : ';' ;
UNDERSCORE  : '_' ;
LPAREN      : '(' ;
RPAREN      : ')' ;
LPAREN_STAR : '(*' ;
RPAREN_STAR : '*)' ;

ENDMODULE : 'endmodule' ;
INPUT     : 'input' ;
LOGIC     : 'logic' ;
MODULE    : 'module' ;
OUTPUT    : 'output' ;

UnsignedNumber
    : DecimalDigit (UNDERSCORE | DecimalDigit)*
    ;

fragment NonZeroDecimalDigit
    : [1-9]
    ;

fragment DecimalDigit
    : [0-9]
    ;

SimpleIdentifier
    : [a-zA-Z_][a-zA-Z0-9_$]*
    ;

WS
    : [ \t\r\n]+ -> channel(HIDDEN)
    ;
