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

lexer grammar SystemVerilogLexer;

EQ          : '=' ;
COMMA       : ',' ;
COLON       : ':' ;
SEMICOLON   : ';' ;
UNDERSCORE  : '_' ;

LBRACK      : '[' ;
RBRACK      : ']' ;
LPAREN      : '(' ;
RPAREN      : ')' ;
LPAREN_STAR : '(*' ;
RPAREN_STAR : '*)' ;

ENDMODULE : 'endmodule' ;
INPUT     : 'input' ;
LOGIC     : 'logic' ;
MODULE    : 'module' ;
OUTPUT    : 'output' ;
SIGNED    : 'signed' ;
UNSIGNED  : 'unsigned' ;
WIRE      : 'wire' ;

UNSIGNED_NUMBER
    : DECIMAL_DIGIT (UNDERSCORE | DECIMAL_DIGIT)*
    ;

fragment NON_ZERO_DECIMAL_DIGIT
    : [1-9]
    ;

fragment DECIMAL_DIGIT
    : [0-9]
    ;

STRING_LITERAL
	: '"' ~["\n\r]* '"'
	;

SIMPLE_IDENTIFIER
    : [a-zA-Z_][a-zA-Z0-9_$]*
    ;

WS
    : [ \t\r\n]+ -> channel(HIDDEN)
    ;
