lexer grammar SystemVerilogLexer;

SimpleIdentifier
    : [a-zA-Z_][a-zA-Z0-9_$]*
    ;

ENDMODULE : 'endmodule' ;
MODULE    : 'module' ;
