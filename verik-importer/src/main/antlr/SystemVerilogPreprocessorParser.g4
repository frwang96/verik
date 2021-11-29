parser grammar SystemVerilogPreprocessorParser;

options { tokenVocab = SystemVerilogPreprocessorLexer; }


file
    : text* EOF
    ;

text
    : code
    | unescapedDirective
    ;

unescapedDirective
    : BACKTICK TIMESCALE
    ;

code
    : CODE
    ;
