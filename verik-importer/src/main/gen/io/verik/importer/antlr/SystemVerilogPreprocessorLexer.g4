lexer grammar SystemVerilogPreprocessorLexer;

BACKTICK
    : '`' -> mode(DIRECTIVE_MODE)
    ;

CODE
    : ~[`"/\r\n]+
    ;

STRING_LITERAL
    : STRING -> type(CODE)
    ;

BLOCK_COMMENT
    :'/*' .*? '*/' -> type(CODE)
    ;

LINE_COMMENT
    : '//' ~[\r\n]* -> type(CODE)
    ;

WS
    : [ \t\r\n]+ -> type(CODE)
    ;

mode DIRECTIVE_MODE;

DIRECTIVE_WS
    : [ \t]+ -> channel(HIDDEN)
    ;

TIMESCALE
    : 'timescale' ~[\r\n]+ [\r\n]? -> mode(DEFAULT_MODE)
    ;

fragment STRING
    : '"' ('\\"' | '\\\\' | .)*? '"'
    ;
