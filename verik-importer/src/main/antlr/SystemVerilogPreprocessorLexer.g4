lexer grammar SystemVerilogPreprocessorLexer;


BACKTICK
    : '`' -> mode(DIRECTIVE_MODE)
    ;

CODE
    : ~[`"/\r\n]+
    ;

BLOCK_COMMENT
    :'/*' .*? '*/' -> channel(HIDDEN)
    ;

LINE_COMMENT
    : '//' ~[\r\n]* -> channel(HIDDEN)
    ;

WS
    : [ \t\r\n]+ -> channel(HIDDEN)
    ;


mode DIRECTIVE_MODE;

DIRECTIVE_WS
    : [ \t]+ -> channel(HIDDEN)
    ;

TIMESCALE
    : 'timescale' ~[\r\n]+ -> mode(DEFAULT_MODE)
    ;
