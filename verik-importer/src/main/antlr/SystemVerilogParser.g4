parser grammar SystemVerilogParser;

options { tokenVocab = SystemVerilogLexer; }

// A.1.2 SystemVerilog Source Text /////////////////////////////////////////////////////////////////////////////////////

project
    : description* EOF
    ;

description
    : moduleDeclaration
    ;

moduleNonAnsiHeader
    : MODULE identifier listOfPorts SEMICOLON
    ;

moduleAnsiHeader
    : MODULE identifier SEMICOLON
    ;

moduleDeclaration
    : moduleNonAnsiHeader moduleItem* ENDMODULE
    | moduleAnsiHeader ENDMODULE
    ;

// A.1.3 Module Parameters and Ports ///////////////////////////////////////////////////////////////////////////////////

listOfPorts
    : LPAREN port (COMMA port)* RPAREN
    ;

portDeclaration
    : inputDeclaration
    | outputDeclaration
    ;

port
    : portExpression
    ;

portExpression
    : portReference
    ;

portReference
    : identifier
    ;

// A.1.4 Module Items //////////////////////////////////////////////////////////////////////////////////////////////////

moduleItem
    : portDeclaration SEMICOLON
    ;

// A.2.1.2 Port Declarations ///////////////////////////////////////////////////////////////////////////////////////////

inputDeclaration
    : INPUT identifier
    ;

outputDeclaration
    : OUTPUT identifier
    ;

// A.8.7 Numbers ///////////////////////////////////////////////////////////////////////////////////////////////////////

number
    : integralNumber
    ;

integralNumber
    : decimalNumber
    ;

decimalNumber
    : UnsignedNumber
    ;

// A.9.3 Identifiers ///////////////////////////////////////////////////////////////////////////////////////////////////

identifier
    : SimpleIdentifier
    ;
