parser grammar SystemVerilogPreprocessorParser;

options { tokenVocab = SystemVerilogPreprocessorLexer; }

file
    : text* EOF
    ;

text
    : code
    | BACKTICK directive
    | BACKTICK defineDirective
    | BACKTICK macroDirective
    ;

directive
    : IFNDEF    # ifndef
    | IFDEF     # ifdef
    | ENDIF     # endif
    | TIMESCALE # timescale
    ;

defineDirective
    : DEFINE DEFINE_MACRO (TEXT | TEXT_LINE_CONTINUATION)*
    ;

macroDirective
    : DEFINED_MACRO
    ;

code
    : CODE
    ;
