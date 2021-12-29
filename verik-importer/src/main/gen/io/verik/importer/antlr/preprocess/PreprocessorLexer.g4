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

lexer grammar PreprocessorLexer;

@members {
   private int runLevel;
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

DIRECTIVE_IFDEF
    : 'ifdef' [ \t]+ IDENTIFIER [ \t]* [\r\n]? -> mode(DEFAULT_MODE)
    ;

DIRECTIVE_IFNDEF
    : 'ifndef' [ \t]+ IDENTIFIER [ \t]* [\r\n]? -> mode(DEFAULT_MODE)
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
    : IDENTIFIER [ \t]* '(' { runLevel++; } -> mode(RUN_MODE)
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

DEFINE_MACRO_ARG
    : IDENTIFIER [ \t]* '(' -> mode(DEFINE_ARG_MODE)
    ;

DEFINE_MACRO
    : IDENTIFIER [ \t]* -> mode(TEXT_MODE)
    ;

//  DEFINE ARG MODE  ///////////////////////////////////////////////////////////////////////////////////////////////////

mode DEFINE_ARG_MODE;

DEFINE_ARG_WHITESPACE
    : [ \t]+ -> channel(HIDDEN)
    ;

DEFINE_ARG_LINE_CONTINUATION
    : '\\' '\r'? '\n' -> channel(HIDDEN)
    ;

DEFINE_ARG_NEW_LINE
    : '\r'? '\n' -> channel(HIDDEN)
    ;

DEFINE_ARG_COMMA
    : ','
    ;

DEFINE_ARG_RP
    : ')' [ \t]* -> mode(TEXT_MODE)
    ;

DEFINE_ARG_IDENTIFIER
    : IDENTIFIER
    ;

//  TEXT MODE  /////////////////////////////////////////////////////////////////////////////////////////////////////////

mode TEXT_MODE;

TEXT_LINE_CONTINUATION
    : '\\' '\r'? '\n'
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
    : '//' ~[\r\n]* -> channel(HIDDEN)
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

//  RUN MODE  //////////////////////////////////////////////////////////////////////////////////////////////////////////

mode RUN_MODE;

RUN_WHITESPACE
    : [ \t]+ -> type(RUN_TEXT)
    ;

RUN_COMMA
    : ',' { if (runLevel != 1) setType(RUN_TEXT); }
    ;

RUN_PUSH
    : [([{] { runLevel++; } -> type(RUN_TEXT)
    ;

RUN_POP
    : [\]}] { runLevel--; } -> type(RUN_TEXT)
    ;

RUN_RP
    : ')' { runLevel--; if (runLevel == 0) mode(DEFAULT_MODE); else setType(RUN_TEXT); }
    ;

RUN_TEXT
    : ~[",(){}[\]\r\n\\/]+
    ;

fragment IDENTIFIER
    : [a-zA-Z_][a-zA-Z0-9_$]*
    ;