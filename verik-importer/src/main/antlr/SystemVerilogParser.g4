parser grammar SystemVerilogParser;

options { tokenVocab = SystemVerilogLexer; }

// A.1.2 SystemVerilog Source Text /////////////////////////////////////////////////////////////////////////////////////

compilationUnit
    : description* EOF
    ;

description
    : moduleDeclaration
    | packageItem
    ;

moduleNonAnsiHeader
    : MODULE identifier listOfPorts SEMICOLON
    ;

moduleAnsiHeader
    : MODULE identifier listOfPortDeclarations? SEMICOLON
    ;

moduleDeclaration
    : moduleNonAnsiHeader moduleItem* ENDMODULE
    | moduleAnsiHeader ENDMODULE
    ;

// A.1.3 Module Parameters and Ports ///////////////////////////////////////////////////////////////////////////////////

listOfPorts
    : LPAREN port (COMMA port)* RPAREN
    ;

listOfPortDeclarations
    : LPAREN (ansiPortDeclaration (COMMA ansiPortDeclaration)*)? RPAREN
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

portDirection
    : INPUT
    | OUTPUT
    ;

netPortHeader
    : portDirection? netPortType
    ;

ansiPortDeclaration
    : netPortHeader identifier
    ;

// A.1.4 Module Items //////////////////////////////////////////////////////////////////////////////////////////////////

moduleItem
    : portDeclaration SEMICOLON
    ;

// A.1.11 Package Items ////////////////////////////////////////////////////////////////////////////////////////////////

packageItem
    : packageOrGenerateItemDeclaration
    ;

packageOrGenerateItemDeclaration
    : dataDeclaration
    ;

// A.2.1.2 Port Declarations ///////////////////////////////////////////////////////////////////////////////////////////

inputDeclaration
    : INPUT (netPortType identifier | variablePortType identifier)
    ;

outputDeclaration
    : OUTPUT (netPortType identifier | variablePortType identifier)
    ;

// A.2.1.3 Type Declarations ///////////////////////////////////////////////////////////////////////////////////////////

dataDeclaration
    : dataTypeOrImplicit listOfVariableDeclAssignments SEMICOLON
    ;

// A.2.2.1 Net and Variable Types //////////////////////////////////////////////////////////////////////////////////////

dataType
    : integerVectorType
    ;

dataTypeOrImplicit
    : dataType
    | implicitDataType
    ;

implicitDataType
    :signing? packedDimension*
    ;

integerType
    : integerVectorType
    ;

integerVectorType
    : LOGIC
    ;

netType
    : WIRE
    ;

netPortType
    : dataTypeOrImplicit
    | netType dataTypeOrImplicit
    ;

variablePortType
    : varDataType
    ;

varDataType
    : dataType
    ;

signing
    : SIGNED | UNSIGNED
    ;

simpleType
    : integerType
    ;

// A.2.3 Declaration Lists /////////////////////////////////////////////////////////////////////////////////////////////

listOfVariableDeclAssignments
    : variableDeclAssignment (COMMA variableDeclAssignment)*
    ;

// A.2.4 Declaration Assignments ///////////////////////////////////////////////////////////////////////////////////////

variableDeclAssignment
    : identifier
    ;

// A.2.5 Declaration Ranges ////////////////////////////////////////////////////////////////////////////////////////////

packedDimension
    : LBRACK (constantRange | constantExpression) RBRACK
    ;

// A.8.3 Expressions ///////////////////////////////////////////////////////////////////////////////////////////////////

constantExpression
    : constantPrimary
    ;

constantRange
    : constantExpression COLON constantExpression
    ;

// A.8.4 Primaries /////////////////////////////////////////////////////////////////////////////////////////////////////

constantPrimary
    : primaryLiteral
    ;

primaryLiteral
    : number
    ;

// A.8.7 Numbers ///////////////////////////////////////////////////////////////////////////////////////////////////////

number
    : integralNumber
    ;

integralNumber
    : decimalNumber
    ;

decimalNumber
    : UNSIGNED_NUMBER
    ;

// A.9.3 Identifiers ///////////////////////////////////////////////////////////////////////////////////////////////////

identifier
    : SIMPLE_IDENTIFIER
    ;
