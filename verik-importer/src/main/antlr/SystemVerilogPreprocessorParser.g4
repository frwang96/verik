// SPDX-License-Identifier: Apache-2.0

parser grammar SystemVerilogPreprocessorParser;

options { tokenVocab = SystemVerilogPreprocessorLexer; }

file
    : text* EOF
    ;

text
    : code
    | directive
    ;

directive
    : BACKTICK DIRECTIVE_IFDEF                                    # directiveIfdef
    | BACKTICK DIRECTIVE_IFNDEF                                   # directiveIfndef
    | BACKTICK DIRECTIVE_ELSE                                     # directiveElse
    | BACKTICK DIRECTIVE_ENDIF                                    # directiveEndif
    | BACKTICK DIRECTIVE_LINE                                     # directiveIgnored
    | BACKTICK DIRECTIVE_TIMESCALE                                # directiveIgnored
    | BACKTICK DIRECTIVE_INCLUDE contents                         # directiveInclude
    | BACKTICK DIRECTIVE_UNDEFINEALL                              # directiveUndefineAll
    | BACKTICK DIRECTIVE_UNDEF                                    # directiveUndef
    | BACKTICK DIRECTIVE_DEFINE DEFINE_MACRO parameters? contents # directiveDefine
    | BACKTICK DIRECTIVE_MACRO                                    # directiveMacro
    | BACKTICK DIRECTIVE_MACRO_ARG arguments ARG_RP               # directiveMacroArg
    ;

parameters
    : CONTENT_LP  parameter (CONTENT_COMMA parameter)* CONTENT_RP
    ;

parameter
    : CONTENT_WHITESPACE* CONTENT_IDENTIFIER CONTENT_WHITESPACE*
    ;

contents
    : content*
    ;

content
    : CONTENT_WHITESPACE
    | CONTENT_LINE_CONTINUATION
    | CONTENT_CONCAT
    | CONTENT_ESCAPE_DQ
    | CONTENT_ESCAPE_BACK_SLASH_DQ
    | CONTENT_LP
    | CONTENT_RP
    | CONTENT_COMMA
    | CONTENT_IDENTIFIER
    | CONTENT_TEXT
    ;

arguments
    : argument (ARG_COMMA argument)*
    ;

argument
    : ARG_TEXT*
    ;

code
    : CODE
    ;
