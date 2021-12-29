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
    : IFDEF     # ifdef
    | IFNDEF    # ifndef
    | ENDIF     # endif
    | TIMESCALE # timescale
    | UNDEF_ALL # undefAll
    | UNDEF     # undef
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
