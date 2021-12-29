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

WHITESPACE
    : [ \t\r\n]+ -> type(CODE)
    ;

//  DIRECTIVE MODE  ////////////////////////////////////////////////////////////////////////////////////////////////////

mode DIRECTIVE_MODE;

DIRECTIVE_WHITESPACE
    : [ \t]+ -> channel(HIDDEN)
    ;

DIRECTIVE_BLOCK_COMMENT
    : '/*' .*? '*/' -> channel(HIDDEN)
    ;

DIRECTIVE_LINE_COMMENT
    : '//' ~[\r\n]* -> channel(HIDDEN)
    ;

DIRECTIVE_LINE_CONTINUATION
    : '\\' '\r'? '\n' -> channel(HIDDEN)
    ;

DIRECTIVE_NEW_LINE
    : '\r'? '\n' -> channel(HIDDEN), mode(DEFAULT_MODE)
    ;

DEFINE
    : 'define' [ \t]+ -> mode(DEFINE_MODE)
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

DEFINED_MACRO
    : IDENTIFIER -> mode(DEFAULT_MODE)
    ;

//  DEFINE MODE  ///////////////////////////////////////////////////////////////////////////////////////////////////////

mode DEFINE_MODE;

DEFINE_WHITESPACE
    : [ \t]+ -> channel(HIDDEN)
    ;

DEFINE_LINE_CONTINUATION
    : '\\' '\r'? '\n' -> channel(HIDDEN)
    ;

DEFINE_NEW_LINE
    : '\r'? '\n' -> channel(HIDDEN)
    ;

DEFINE_MACRO
    : IDENTIFIER -> mode(TEXT_MODE)
    ;

//  TEXT MODE  /////////////////////////////////////////////////////////////////////////////////////////////////////////

mode TEXT_MODE;

TEXT_LINE_CONTINUATION
    : '\\' '\r'? '\n' -> channel(HIDDEN)
    ;

TEXT_LINE_BACK_SLASH
    : '\\' -> type(TEXT)
    ;

TEXT_NEW_LINE
    : '\r'? '\n' -> channel(HIDDEN), mode(DEFAULT_MODE)
    ;

TEXT_BLOCK_COMMENT
    : '/*' .*? '*/' -> type(TEXT)
    ;

TEXT_LINE_COMMENT
    : '//' ~[\r\n]* -> type(TEXT)
    ;

TEXT_SLASH
    : '/' -> type(TEXT)
    ;

TEXT_WHITESPACE
    : [ \t]+ -> type(TEXT)
    ;

TEXT
    : ~[\\/\r\n]+
    ;

fragment STRING
    : '"' ('\\"' | '\\\\' | .)*? '"'
    ;

fragment IDENTIFIER
    : [a-zA-Z_][a-zA-Z0-9_$]*
    ;