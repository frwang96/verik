lexer grammar SystemVerilogPreprocessorLexer;

CODE:
    ~[`"/\r\n]+
    ;

WS
    : (' ' | '\t' | '\r'| '\n') -> channel(HIDDEN)
    ;
