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

parser grammar PreprocessorParser;

options { tokenVocab = PreprocessorLexer; }

file
    : text* EOF
    ;

text
    : code
    | directive
    | directiveDefine
    | directiveDefineArg
    | directiveMacro
    | directiveMacroArg
    ;

directive
    : BACKTICK DIRECTIVE_IFDEF       # directiveIfdef
    | BACKTICK DIRECTIVE_IFNDEF      # directiveIfndef
    | BACKTICK DIRECTIVE_ENDIF       # directiveEndif
    | BACKTICK DIRECTIVE_LINE        # directiveIgnored
    | BACKTICK DIRECTIVE_TIMESCALE   # directiveIgnored
    | BACKTICK DIRECTIVE_UNDEFINEALL # directiveUndefineAll
    | BACKTICK DIRECTIVE_UNDEF       # directiveUndef
    ;

directiveDefine
    : BACKTICK DIRECTIVE_DEFINE DEFINE_MACRO (TEXT | TEXT_LINE_CONTINUATION)*
    ;

directiveDefineArg
    : BACKTICK DIRECTIVE_DEFINE DEFINE_MACRO_ARG arguments? DEFINE_ARG_RP (TEXT | TEXT_LINE_CONTINUATION)*
    ;

arguments
    : argument (DEFINE_ARG_COMMA argument)*
    ;

argument
    : DEFINE_ARG_IDENTIFIER
    ;

directiveMacro
    : BACKTICK DIRECTIVE_MACRO
    ;

directiveMacroArg
    : BACKTICK DIRECTIVE_MACRO_ARG runArguments RUN_RP
    ;

runArguments
    : runArgument (RUN_COMMA runArgument)*
    ;

runArgument
    : RUN_TEXT*
    ;

code
    : CODE
    ;
