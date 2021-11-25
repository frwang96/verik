lexer grammar SystemVerilogPreprocessorLexer;


BACKTICK
    : '`' -> mode(DIRECTIVE_MODE)
    ;

CODE
    : ~[`"/\r\n]+
    ;

WS
    : [ \t\r\n]+ -> channel(HIDDEN)
    ;


mode DIRECTIVE_MODE;

DIRECTIVE_WS
    : [ \t]+ -> channel(HIDDEN)
    ;
