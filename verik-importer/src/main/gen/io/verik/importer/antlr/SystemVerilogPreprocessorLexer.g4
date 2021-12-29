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

IFNDEF
    : 'ifndef' [ \t]+ IDENTIFIER [ \t]* [\r\n]? -> mode(DEFAULT_MODE)
    ;

IFDEF
    : 'ifdef' [ \t]+ IDENTIFIER [ \t]* [\r\n]? -> mode(DEFAULT_MODE)
    ;

ENDIF
    : 'endif' [ \t]* [\r\n]? -> mode(DEFAULT_MODE)
    ;

TIMESCALE
    : 'timescale' ~[\r\n]+ [\r\n]? -> mode(DEFAULT_MODE)
    ;

fragment STRING
    : '"' ('\\"' | '\\\\' | .)*? '"'
    ;

fragment IDENTIFIER
    : [a-zA-Z_][a-zA-Z0-9_$]*
    ;