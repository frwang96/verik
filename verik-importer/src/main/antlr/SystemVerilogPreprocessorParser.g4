parser grammar SystemVerilogPreprocessorParser;

options { tokenVocab = SystemVerilogPreprocessorLexer; }

source_text
    : text* EOF
    ;

text
    : code
    ;

code
    : CODE+
    ;
