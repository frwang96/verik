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

AND                 : '&' ;
AND2                : '&&' ;
AND3                : '&&&' ;
AT                  : '@' ;
AT_STAR             : '@*' ;
BANG                : '!' ;
BANG_EQ             : '!=' ;
CARET               : '^' ;
CARET_EQ            : '^=' ;
CARET_NOT           : '^~' ;
COLON               : ':' ;
COLON2              : '::' ;
COMMA               : ',' ;
DOLLAR              : '$' ;
DOT                 : '.' ;
EQ                  : '=' ;
EQ2                 : '==' ;
EQ2_QUEST           : '==?' ;
EQ3                 : '===' ;
EQ_GT               : '=>' ;
GT                  : '>' ;
LT                  : '<' ;
LT2                 : '<<' ;
LT2_EQ              : '<<=' ;
LT3                 : '<<<' ;
LT3_EQ              : '<<<=' ;
LT_EQ               : '<=' ;
LT_MINUS_GT         : '<->' ;
MINUS               : '-' ;
MINUS2              : '--' ;
MOD                 : '%';
MOD_EQ              : '%=';
NOT                 : '~' ;
NOT_AND             : '~&' ;
NOT_CARET           : '~^' ;
NOT_OR              : '~|' ;
OR                  : '|' ;
OR2                 : '||' ;
PLUS                : '+' ;
PLUS2               : '++' ;
QUEST               : '?' ;
SEMICOLON           : ';' ;
SHARP               : '#' ;
SHARP2              : '##' ;
SLASH               : '/' ;
STAR                : '*' ;
TICK                : '\'' ;

LBRACK              : '[' ;
RBRACK              : ']' ;
LPAREN              : '(' ;
RPAREN              : ')' ;
LBRACE              : '{' ;
RBRACE              : '}' ;
LPAREN_STAR         : '(*' ;
RPAREN_STAR         : '*)' ;

DPI_C               : '"DPI-C"' ;
DPI                 : '"DPI"' ;

ACCEPT_ON           : 'accept_on' ;
ALIAS               : 'alias' ;
ALWAYS              : 'always' ;
ALWAYS_COMB         : 'always_comb' ;
ALWAYS_FF           : 'always_ff' ;
ALWAYS_LATCH        : 'always_latch' ;
ASSERT              : 'assert' ;
ASSUME              : 'assume' ;
AUTOMATIC           : 'automatic' ;
BEFORE              : 'before' ;
BEGIN               : 'begin' ;
BIND                : 'bind' ;
BINS                : 'bins' ;
BINSOF              : 'binsof' ;
BIT                 : 'bit' ;
BREAK               : 'break' ;
BUF                 : 'buf' ;
BUFIF0              : 'bufif0' ;
BUFIF1              : 'bufif1' ;
BYTE                : 'byte' ;
CASE                : 'case' ;
CASEX               : 'casex' ;
CASEZ               : 'casez' ;
CELL                : 'cell' ;
CHANDLE             : 'chandle' ;
CHECKER             : 'checker' ;
CLASS               : 'class' ;
CLOCKING            : 'clocking' ;
CMOS                : 'cmos' ;
CONFIG              : 'config' ;
CONST               : 'const' ;
CONSTRAINT          : 'constraint' ;
CONTEXT             : 'context' ;
CONTINUE            : 'continue' ;
COVER               : 'cover' ;
COVERGROUP          : 'covergroup' ;
COVERPOINT          : 'coverpoint' ;
CROSS               : 'cross' ;
DEASSIGN            : 'deassign' ;
DEFPARAM            : 'defparam' ;
DESIGN              : 'design' ;
DISABLE             : 'disable' ;
DIST                : 'dist' ;
DO                  : 'do' ;
EDGE                : 'edge' ;
ELSE                : 'else' ;
END                 : 'end' ;
ENDCASE             : 'endcase' ;
ENDCHECKER          : 'endchecker' ;
ENDCLASS            : 'endclass' ;
ENDCLOCKING         : 'endclocking' ;
ENDCONFIG           : 'endconfig' ;
ENDFUNCTION         : 'endfunction' ;
ENDGENERATE         : 'endgenerate' ;
ENDGROUP            : 'endgroup' ;
ENDINTERFACE        : 'endinterface' ;
ENDMODULE           : 'endmodule' ;
ENDPACKAGE          : 'endpackage' ;
ENDPRIMITIVE        : 'endprimitive' ;
ENDPROGRAM          : 'endprogram' ;
ENDPROPERTY         : 'endproperty' ;
ENDSEQUENCE         : 'endsequence' ;
ENDSPECIFY          : 'endspecify' ;
ENDTABLE            : 'endtable' ;
ENDTASK             : 'endtask' ;
ENUM                : 'enum' ;
EVENT               : 'event' ;
EVENTUALLY          : 'eventually' ;
EXPECT              : 'expect' ;
EXPORT              : 'export' ;
EXTENDS             : 'extends' ;
EXTERN              : 'extern' ;
FINAL               : 'final' ;
FIRST_MATCH         : 'first_match' ;
FOR                 : 'for' ;
FORCE               : 'force' ;
FOREACH             : 'foreach' ;
FOREVER             : 'forever' ;
FORK                : 'fork' ;
FORKJOIN            : 'forkjoin' ;
FUNCTION            : 'function' ;
FUNCTION_SAMPLE     : 'function sample' ;
GENERATE            : 'generate' ;
GENVAR              : 'genvar' ;
GLOBAL              : 'global' ;
HIGHZ0              : 'highz0' ;
HIGHZ1              : 'highz1' ;
IF                  : 'if' ;
IFF                 : 'iff' ;
IFNONE              : 'ifnone' ;
IGNORE_BINS         : 'ignore_bins' ;
ILLEGAL_BINS        : 'illegal_bins' ;
IMPLEMENTS          : 'implements' ;
IMPLIES             : 'implies' ;
IMPORT              : 'import' ;
INITIAL             : 'initial' ;
INOUT               : 'inout' ;
INPUT               : 'input' ;
INSIDE              : 'inside' ;
INSTANCE            : 'instance' ;
INT                 : 'int' ;
INTEGER             : 'integer' ;
INTERCONNECT        : 'interconnect' ;
INTERFACE           : 'interface' ;
INTERSECT           : 'intersect' ;
JOIN                : 'join' ;
JOIN_ANY            : 'join_any' ;
JOIN_NONE           : 'join_none' ;
LAND                : 'and' ;
LARGE               : 'large' ;
LASSIGN             : 'assign' ;
LDEFAULT            : 'default' ;
LET                 : 'let' ;
LIBLIST             : 'liblist' ;
LNOT                : 'not' ;
LOCAL               : 'local' ;
LOCALPARAM          : 'localparam' ;
LOGIC               : 'logic' ;
LONGINT             : 'longint' ;
LOR                 : 'or' ;
MACROMODULE         : 'macromodule' ;
MATCHES             : 'matches' ;
MEDIUM              : 'medium' ;
MODPORT             : 'modport' ;
MODULE              : 'module' ;
NAND                : 'nand' ;
NEGEDGE             : 'negedge' ;
NETTYPE             : 'nettype' ;
NEW                 : 'new' ;
NEXTTIME            : 'nexttime' ;
NMOS                : 'nmos' ;
NOR                 : 'nor' ;
NOSHOWCANCELLED     : 'noshowcancelled' ;
NOTIF0              : 'notif0' ;
NOTIF1              : 'notif1' ;
NULL                : 'null' ;
OPTION              : 'option.' ;
OUTPUT              : 'output' ;
PACKAGE             : 'package' ;
PACKED              : 'packed' ;
PARAMETER           : 'parameter' ;
PMOS                : 'pmos' ;
POSEDGE             : 'posedge' ;
PRIMITIVE           : 'primitive' ;
PRIORITY            : 'priority' ;
PROGRAM             : 'program' ;
PROPERTY            : 'property' ;
PROTECTED           : 'protected' ;
PULL0               : 'pull0' ;
PULL1               : 'pull1' ;
PULLDOWN            : 'pulldown' ;
PULLUP              : 'pullup' ;
PULSESTYLE_ONDETECT : 'pulsestyle_ondetect' ;
PULSESTYLE_ONEVENT  : 'pulsestyle_onevent' ;
PURE                : 'pure' ;
RAND                : 'rand' ;
RANDC               : 'randc' ;
RANDCASE            : 'randcase' ;
RANDOMIZE           : 'randomize' ;
RANDSEQUENCE        : 'randsequence' ;
RCMOS               : 'rcmos' ;
REAL                : 'real' ;
REALTIME            : 'realtime' ;
REF                 : 'ref' ;
REG                 : 'reg' ;
REJECT_ON           : 'reject_on' ;
RELEASE             : 'release' ;
REPEAT              : 'repeat' ;
RESTRICT            : 'restrict' ;
RETURN              : 'return' ;
RNMOS               : 'rnmos' ;
RPMOS               : 'rpmos' ;
RTRAN               : 'rtran' ;
RTRANIF0            : 'rtranif0' ;
RTRANIF1            : 'rtranif1' ;
SCALARED            : 'scalared' ;
SEQUENCE            : 'sequence' ;
SHORTINT            : 'shortint' ;
SHORTREAL           : 'shortreal' ;
SHOWCANCELLED       : 'showcancelled' ;
SIGNED              : 'signed' ;
SMALL               : 'small' ;
SOFT                : 'soft' ;
SOLVE               : 'solve' ;
SPECIFY             : 'specify' ;
SPECPARAM           : 'specparam' ;
STATIC              : 'static' ;
STD                 : 'std' ;
STRING              : 'string' ;
STRONG              : 'strong' ;
STRONG0             : 'strong0' ;
STRONG1             : 'strong1' ;
STRUCT              : 'struct' ;
SUPER               : 'super' ;
SUPPLY0             : 'supply0' ;
SUPPLY1             : 'supply1' ;
SYNC_ACCEPT_ON      : 'sync_accept_on' ;
SYNC_REJECT_ON      : 'sync_reject_on' ;
S_ALWAYS            : 's_always' ;
S_EVENTUALLY        : 's_eventually' ;
S_NEXTTIME          : 's_nexttime' ;
S_UNTIL             : 's_until' ;
S_UNTIL_WITH        : 's_until_with' ;
TABLE               : 'table' ;
TAGGED              : 'tagged' ;
TASK                : 'task' ;
THIS                : 'this' ;
THROUGHOUT          : 'throughout' ;
TIME                : 'time' ;
TIMEPRECISION       : 'timeprecision' ;
TIMEUNIT            : 'timeunit' ;
TRAN                : 'tran' ;
TRANIF0             : 'tranif0' ;
TRANIF1             : 'tranif1' ;
TRI                 : 'tri' ;
TRI0                : 'tri0' ;
TRI1                : 'tri1' ;
TRIAND              : 'triand' ;
TRIOR               : 'trior' ;
TRIREG              : 'trireg' ;
TYPE                : 'type' ;
TYPEDEF             : 'typedef' ;
UNION               : 'union' ;
UNSIGNED            : 'unsigned' ;
UNTIL               : 'until' ;
UNTIL_WITH          : 'until_with' ;
UNTYPED             : 'untyped' ;
USE                 : 'use' ;
UWIRE               : 'uwire' ;
VAR                 : 'var' ;
VECTORED            : 'vectored' ;
VIRTUAL             : 'virtual' ;
VOID                : 'void' ;
WAIT                : 'wait' ;
WAIT_ORDER          : 'wait_order' ;
WAND                : 'wand' ;
WEAK                : 'weak' ;
WEAK0               : 'weak0' ;
WEAK1               : 'weak1' ;
WHILE               : 'while' ;
WILDCARD            : 'wildcard' ;
WIRE                : 'wire' ;
WITH                : 'with' ;
WITHIN              : 'within' ;
WOR                 : 'wor' ;
XNOR                : 'xnor' ;
XOR                 : 'xor' ;

UNIT                : '$unit' ;

UNSIGNED_NUMBER
    : DECIMAL_DIGIT ('_' | DECIMAL_DIGIT)*
    ;

fragment NON_ZERO_DECIMAL_DIGIT
    : [1-9]
    ;

fragment DECIMAL_DIGIT
    : [0-9]
    ;

STRING_LITERAL
    : '"' ('\\"' | '\\\\' | .)*? '"'
    ;

SIMPLE_IDENTIFIER
    : [a-zA-Z_][a-zA-Z0-9_$]*
    ;

WS
    : [ \t\r\n]+ -> channel(HIDDEN)
    ;
