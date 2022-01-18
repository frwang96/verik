//  Copyright (c) 2021 Francis Wang
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      https://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

lexer grammar SystemVerilogPreprocessorLexer;

@members {
   private int argLevel;
}

BACKTICK
    : '`' -> mode(DIRECTIVE_MODE)
    ;

CODE
    : ~[`"/\r\n]+
    ;

STRING_LITERAL
    : '"' ('\\"' | '\\\\' | .)*? '"' -> type(CODE)
    ;

BLOCK_COMMENT
    :'/*' .*? '*/' -> type(CODE)
    ;

LINE_COMMENT
    : '//' ~[\r\n]* -> type(CODE)
    ;

SLASH
    : '/' -> type(CODE)
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

DIRECTIVE_DEFINE
    : 'define' [ \t]+ -> mode(DEFINE_MODE)
    ;

DIRECTIVE_INCLUDE
    : 'include' [ \t]+ -> mode(CONTENT_MODE)
    ;

DIRECTIVE_IFDEF
    : 'ifdef' [ \t]+ IDENTIFIER [ \t]* [\r\n]? -> mode(DEFAULT_MODE)
    ;

DIRECTIVE_IFNDEF
    : 'ifndef' [ \t]+ IDENTIFIER [ \t]* [\r\n]? -> mode(DEFAULT_MODE)
    ;

DIRECTIVE_ELSE
    : 'else' [ \t]* [\r\n]? -> mode(DEFAULT_MODE)
    ;

DIRECTIVE_ENDIF
    : 'endif' [ \t]* [\r\n]? -> mode(DEFAULT_MODE)
    ;

DIRECTIVE_LINE
    : 'line' ~[\r\n]+ [\r\n]? -> mode(DEFAULT_MODE)
    ;

DIRECTIVE_TIMESCALE
    : 'timescale' ~[\r\n]+ [\r\n]? -> mode(DEFAULT_MODE)
    ;

DIRECTIVE_UNDEFINEALL
    : 'undefineall' [ \t]* [\r\n]? -> mode(DEFAULT_MODE)
    ;

DIRECTIVE_UNDEF
    : 'undef' [ \t]+ IDENTIFIER [ \t]* [\r\n]? -> mode(DEFAULT_MODE)
    ;

DIRECTIVE_MACRO_ARG
    : IDENTIFIER [ \t]* '(' { argLevel++; } -> mode(ARG_MODE)
    ;

DIRECTIVE_MACRO
    : IDENTIFIER [ \t]* -> mode(DEFAULT_MODE)
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
    : IDENTIFIER [ \t]* -> mode(CONTENT_MODE)
    ;

//  CONTENT MODE  //////////////////////////////////////////////////////////////////////////////////////////////////////

mode CONTENT_MODE;

CONTENT_WHITESPACE
    : [ \t]+
    ;

CONTENT_LINE_CONTINUATION
    : '\\' '\r'? '\n'
    ;

CONTENT_LINE_BACK_SLASH
    : '\\' -> type(CONTENT_TEXT)
    ;

CONTENT_NEW_LINE
    : '\r'? '\n' -> channel(HIDDEN), mode(DEFAULT_MODE)
    ;

CONTENT_BLOCK_COMMENT
    : '/*' .*? '*/' -> type(CONTENT_TEXT)
    ;

CONTENT_LINE_COMMENT
    : '//' (~[\r\n\\] | '\\' ~[\r\n])* -> channel(HIDDEN)
    ;

CONTENT_SLASH
    : '/' -> type(CONTENT_TEXT)
    ;

CONTENT_STRING_LITERAL
    : '"' ('\\"' | '\\\\' | .)*? '"' -> type(CONTENT_TEXT)
    ;

CONTENT_CONCAT
    : '``'
    ;

CONTENT_ESCAPE_DQ
    : '`"'
    ;

CONTENT_ESCAPE_BACK_SLASH_DQ
    : '`\\`"'
    ;

CONTENT_BACK_TICK
    : '`' -> type(CONTENT_TEXT)
    ;

CONTENT_LP
    : '('
    ;

CONTENT_RP
    : ')' [ \t]*
    ;

CONTENT_COMMA
    : ','
    ;

CONTENT_IDENTIFIER
    : IDENTIFIER
    ;

CONTENT_TEXT
    : ~[\\/"`(),a-zA-Z_ \t\r\n]+
    ;

//  ARG MODE  //////////////////////////////////////////////////////////////////////////////////////////////////////////

mode ARG_MODE;

ARG_WHITESPACE
    : [ \t]+ -> type(ARG_TEXT)
    ;

ARG_NEW_LINE
    : '\r'? '\n' -> type(ARG_TEXT)
    ;

ARG_COMMA
    : ',' [ \t]* [\r\n]? { if (argLevel != 1) setType(ARG_TEXT); }
    ;

ARG_PUSH
    : [([{] { argLevel++; } -> type(ARG_TEXT)
    ;

ARG_POP
    : [\]}] { argLevel--; } -> type(ARG_TEXT)
    ;

ARG_RP
    : ')' { argLevel--; if (argLevel == 0) mode(DEFAULT_MODE); else setType(ARG_TEXT); }
    ;

ARG_STRING_LITERAL
    : '"' ('\\"' | '\\\\' | .)*? '"' -> type(ARG_TEXT)
    ;

ARG_TEXT
    : ~[",(){}[\]\r\n\\/]+
    ;

fragment IDENTIFIER
    : [a-zA-Z_][a-zA-Z0-9_$]*
    ;