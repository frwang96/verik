/**
 * Kotlin lexical grammar in ANTLR4 notation
 */

lexer grammar KotlinLexer;

// SECTION: lexicalGeneral

DelimitedComment
    : '/*' ( DelimitedComment | . )*? '*/' -> channel(HIDDEN)
    ;

LineComment
    : '//' ~[\r\n]* -> channel(HIDDEN)
    ;

WS
    : [\u0020\u0009\u000C] -> channel(HIDDEN)
    ;

NL: '\n' | '\r' '\n'?;

fragment Hidden: DelimitedComment | LineComment | WS;

// SECTION: separatorsAndOperations

DOT: '.';
COMMA: ',';
LPAREN: '(' -> pushMode(Inside);
RPAREN: ')';
LSQUARE: '[' -> pushMode(Inside);
RSQUARE: ']';
LCURL: '{' -> pushMode(DEFAULT_MODE);
RCURL: '}' { if (!_modeStack.isEmpty()) { popMode(); } };
MULT: '*';
MOD: '%';
DIV: '/';
ADD: '+';
SUB: '-';
INCR: '++';
DECR: '--';
CONJ: '&&';
DISJ: '||';
EXCL_WS: '!' Hidden;
EXCL_NO_WS: '!';
COLON: ':';
SEMICOLON: ';';
ASSIGNMENT: '=';
ADD_ASSIGNMENT: '+=';
SUB_ASSIGNMENT: '-=';
MULT_ASSIGNMENT: '*=';
DIV_ASSIGNMENT: '/=';
MOD_ASSIGNMENT: '%=';
ARROW: '->';
RANGE: '..';
AT_NO_WS: '@';
AT_PRE_WS: (Hidden | NL) '@' ;
LANGLE: '<';
RANGLE: '>';
LE: '<=';
GE: '>=';
EXCL_EQ: '!=';
EQEQ: '==';

// SECTION: keywords

PACKAGE: 'package';
IMPORT: 'import';
CLASS: 'class';
FUN: 'fun';
OBJECT: 'object';
VAL: 'val';
VAR: 'var';
COMPANION: 'companion';
THIS: 'this';
SUPER: 'super';
IF: 'if';
ELSE: 'else';
WHEN: 'when';
FOR: 'for';
DO: 'do';
WHILE: 'while';
RETURN: 'return';
CONTINUE: 'continue';
BREAK: 'break';
AS: 'as';
IS: 'is';
IN: 'in';
NOT_IS: '!is' (Hidden | NL);
NOT_IN: '!in' (Hidden | NL);

// SECTION: lexicalModifiers

PUBLIC: 'public';
PRIVATE: 'private';
PROTECTED: 'protected';
INTERNAL: 'internal';
ENUM: 'enum';
OVERRIDE: 'override';
FINAL: 'final';
OPEN: 'open';

// SECTION: literals

fragment DecDigit: '0'..'9';
fragment DecDigitNoZero: '1'..'9';
fragment DecDigitOrSeparator: DecDigit | '_';

fragment DecDigits
    : DecDigit DecDigitOrSeparator* DecDigit
    | DecDigit
    ;

IntegerLiteral
    : DecDigitNoZero DecDigitOrSeparator* DecDigit
    | DecDigit
    ;

fragment HexDigit: [0-9a-fA-F];
fragment HexDigitOrSeparator: HexDigit | '_';

HexLiteral
    : '0' [xX] HexDigit HexDigitOrSeparator* HexDigit
    | '0' [xX] HexDigit
    ;

fragment BinDigit: [01];
fragment BinDigitOrSeparator: BinDigit | '_';

BinLiteral
    : '0' [bB] BinDigit BinDigitOrSeparator* BinDigit
    | '0' [bB] BinDigit
    ;

BooleanLiteral: 'true'| 'false';

// SECTION: lexicalIdentifiers

Identifier
    : [_a-zA-Z] [_a-zA-Z0-9]*
    ;

IdentifierOrSoftKey
    : Identifier
    /* Soft keywords */
    | COMPANION
    | ENUM
    | FINAL
    | IMPORT
    | INTERNAL
    | OPEN
    | OVERRIDE
    | PRIVATE
    | PROTECTED
    | PUBLIC
    ;

FieldIdentifier
    : '$' IdentifierOrSoftKey
    ;

fragment EscapedIdentifier
    : '\\' ('t' | 'b' | 'r' | 'n' | '\'' | '"' | '\\' | '$')
    ;

fragment EscapeSeq
    : EscapedIdentifier
    ;

// SECTION: strings

QUOTE_OPEN
    : '"' -> pushMode(LineString)
    ;

mode LineString;

QUOTE_CLOSE
    : '"' -> popMode
    ;

LineStrRef
    : FieldIdentifier
    ;

LineStrText
    : ~('\\' | '"' | '$')+ | '$'
    ;

LineStrEscapedChar
    : EscapedIdentifier
    ;

LineStrExprStart
    : '${' -> pushMode(DEFAULT_MODE)
    ;

// SECTION: inside

mode Inside;

Inside_RPAREN: RPAREN -> popMode, type(RPAREN);
Inside_RSQUARE: RSQUARE -> popMode, type(RSQUARE);
Inside_LPAREN: LPAREN -> pushMode(Inside), type(LPAREN);
Inside_LSQUARE: LSQUARE -> pushMode(Inside), type(LSQUARE);
Inside_LCURL: LCURL -> pushMode(DEFAULT_MODE), type(LCURL);
Inside_RCURL: RCURL -> popMode, type(RCURL);

Inside_DOT: DOT -> type(DOT);
Inside_COMMA: COMMA  -> type(COMMA);
Inside_MULT: MULT -> type(MULT);
Inside_MOD: MOD  -> type(MOD);
Inside_DIV: DIV -> type(DIV);
Inside_ADD: ADD  -> type(ADD);
Inside_SUB: SUB  -> type(SUB);
Inside_INCR: INCR  -> type(INCR);
Inside_DECR: DECR  -> type(DECR);
Inside_CONJ: CONJ  -> type(CONJ);
Inside_DISJ: DISJ  -> type(DISJ);
Inside_EXCL_WS: '!' (Hidden|NL) -> type(EXCL_WS);
Inside_EXCL_NO_WS: EXCL_NO_WS  -> type(EXCL_NO_WS);
Inside_COLON: COLON  -> type(COLON);
Inside_SEMICOLON: SEMICOLON  -> type(SEMICOLON);
Inside_ASSIGNMENT: ASSIGNMENT  -> type(ASSIGNMENT);
Inside_ADD_ASSIGNMENT: ADD_ASSIGNMENT  -> type(ADD_ASSIGNMENT);
Inside_SUB_ASSIGNMENT: SUB_ASSIGNMENT  -> type(SUB_ASSIGNMENT);
Inside_MULT_ASSIGNMENT: MULT_ASSIGNMENT  -> type(MULT_ASSIGNMENT);
Inside_DIV_ASSIGNMENT: DIV_ASSIGNMENT  -> type(DIV_ASSIGNMENT);
Inside_MOD_ASSIGNMENT: MOD_ASSIGNMENT  -> type(MOD_ASSIGNMENT);
Inside_ARROW: ARROW  -> type(ARROW);
Inside_RANGE: RANGE  -> type(RANGE);
Inside_AT_NO_WS: AT_NO_WS  -> type(AT_NO_WS);
Inside_AT_PRE_WS: AT_PRE_WS  -> type(AT_PRE_WS);
Inside_LANGLE: LANGLE  -> type(LANGLE);
Inside_RANGLE: RANGLE  -> type(RANGLE);
Inside_LE: LE  -> type(LE);
Inside_GE: GE  -> type(GE);
Inside_EXCL_EQ: EXCL_EQ  -> type(EXCL_EQ);
Inside_IS: IS -> type(IS);
Inside_NOT_IS: NOT_IS -> type(NOT_IS);
Inside_NOT_IN: NOT_IN -> type(NOT_IN);
Inside_AS: AS  -> type(AS);
Inside_EQEQ: EQEQ  -> type(EQEQ);
Inside_QUOTE_OPEN: QUOTE_OPEN -> pushMode(LineString), type(QUOTE_OPEN);

Inside_VAL: VAL -> type(VAL);
Inside_VAR: VAR -> type(VAR);
Inside_FUN: FUN -> type(FUN);
Inside_OBJECT: OBJECT -> type(OBJECT);
Inside_SUPER: SUPER -> type(SUPER);
Inside_IN: IN -> type(IN);
Inside_RETURN: RETURN -> type(RETURN);
Inside_CONTINUE: CONTINUE -> type(CONTINUE);
Inside_BREAK: BREAK -> type(BREAK);
Inside_IF: IF -> type(IF);
Inside_ELSE: ELSE -> type(ELSE);
Inside_WHEN: WHEN -> type(WHEN);
Inside_FOR: FOR -> type(FOR);
Inside_DO: DO -> type(DO);
Inside_WHILE: WHILE -> type(WHILE);

Inside_PUBLIC: PUBLIC -> type(PUBLIC);
Inside_PRIVATE: PRIVATE -> type(PRIVATE);
Inside_PROTECTED: PROTECTED -> type(PROTECTED);
Inside_INTERNAL: INTERNAL -> type(INTERNAL);
Inside_ENUM: ENUM -> type(ENUM);
Inside_OVERRIDE: OVERRIDE -> type(OVERRIDE);
Inside_FINAL: FINAL -> type(FINAL);
Inside_OPEN: OPEN -> type(OPEN);

Inside_BooleanLiteral: BooleanLiteral -> type(BooleanLiteral);
Inside_IntegerLiteral: IntegerLiteral -> type(IntegerLiteral);
Inside_HexLiteral: HexLiteral -> type(HexLiteral);
Inside_BinLiteral: BinLiteral -> type(BinLiteral);

Inside_Identifier: Identifier -> type(Identifier);
Inside_Comment: (LineComment | DelimitedComment) -> channel(HIDDEN);
Inside_WS: WS -> channel(HIDDEN);
Inside_NL: NL -> channel(HIDDEN);

mode DEFAULT_MODE;

ErrorCharacter: .;
