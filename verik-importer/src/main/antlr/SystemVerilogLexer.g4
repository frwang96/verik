lexer grammar SystemVerilogLexer;

ENDMODULE : 'endmodule' ;
MODULE    : 'module' ;

SimpleIdentifier
    : [a-zA-Z_][a-zA-Z0-9_$]*
    ;

WS
    : [ \t\r\n]+ -> channel(HIDDEN)
    ;
