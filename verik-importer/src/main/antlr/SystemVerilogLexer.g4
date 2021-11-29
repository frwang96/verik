lexer grammar SystemVerilogLexer;

COMMA     : ',' ;
SEMICOLON : ';' ;
LPAREN    : '(' ;
RPAREN    : ')' ;
LPAREN_STAR : '(*' ;
RPAREN_STAR : '*)' ;

ENDMODULE : 'endmodule' ;
INPUT     : 'input' ;
MODULE    : 'module' ;
OUTPUT    : 'output' ;

SimpleIdentifier
    : [a-zA-Z_][a-zA-Z0-9_$]*
    ;

WS
    : [ \t\r\n]+ -> channel(HIDDEN)
    ;
