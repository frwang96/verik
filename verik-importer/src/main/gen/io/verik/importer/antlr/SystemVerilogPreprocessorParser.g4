parser grammar SystemVerilogPreprocessorParser;

options { tokenVocab = SystemVerilogPreprocessorLexer; }

file
    : text* EOF
    ;

text
    : code
    | BACKTICK directive
    ;

directive
    : IFNDEF
    | IFDEF
    | ENDIF
    | TIMESCALE
    ;

code
    : CODE
    ;
