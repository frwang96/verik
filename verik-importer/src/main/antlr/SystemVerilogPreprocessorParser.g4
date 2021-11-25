parser grammar SystemVerilogPreprocessorParser;

options { tokenVocab = SystemVerilogPreprocessorLexer; }


file
    : text* EOF
    ;

text
    : code
    ;

code
    : CODE
    ;
