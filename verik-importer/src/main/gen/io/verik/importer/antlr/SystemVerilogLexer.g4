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
