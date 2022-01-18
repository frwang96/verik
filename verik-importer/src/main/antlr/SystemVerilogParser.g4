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

parser grammar SystemVerilogParser;

options { tokenVocab = SystemVerilogLexer; }

// A.1.2 SystemVerilog Source Text /////////////////////////////////////////////////////////////////////////////////////

sourceText
    : description* EOF
    ;

description
    : moduleDeclaration
    | packageItem
    ;

moduleNonAnsiHeader
    : attributeInstance* MODULE identifier listOfPorts SEMICOLON
    ;

moduleAnsiHeader
    : attributeInstance* MODULE identifier listOfPortDeclarations? SEMICOLON
    ;

moduleDeclaration
    : moduleNonAnsiHeader moduleItem* ENDMODULE (COLON moduleIdentifier)?     # moduleDeclarationNonAnsi
    | moduleAnsiHeader nonPortModuleItem* ENDMODULE (COLON moduleIdentifier)? # moduleDeclarationAnsi
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
    : portIdentifier
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

moduleCommonItem
    : moduleOrGenerateItemDeclaration
    ;

moduleItem
    : portDeclaration SEMICOLON # moduleItemPortDeclaration
    | nonPortModuleItem         # moduleItemNonPortItem
    ;

moduleOrGenerateItem
    : attributeInstance* moduleCommonItem
    ;

moduleOrGenerateItemDeclaration
    : packageOrGenerateItemDeclaration
    ;

nonPortModuleItem
    : moduleOrGenerateItem
    ;

// A.1.11 Package Items ////////////////////////////////////////////////////////////////////////////////////////////////

packageItem
    : packageOrGenerateItemDeclaration
    ;

packageOrGenerateItemDeclaration
    : dataDeclaration
    | functionDeclaration
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

lifetime
    : STATIC
    | AUTOMATIC
    ;

// A.2.2.1 Net and Variable Types //////////////////////////////////////////////////////////////////////////////////////

dataType
    : integerVectorType signing? packedDimension* # dataTypeVector
    | classType                                   # dataTypeClassType
    ;

dataTypeOrImplicit
    : dataType
    | implicitDataType
    ;

implicitDataType
    : signing? packedDimension*
    ;

classType
    : psClassIdentifier
    ;

integerVectorType
    : BIT
    | LOGIC
    | REG
    ;

netType
    : WIRE
    ;

netPortType
    : dataTypeOrImplicit
    | netType dataTypeOrImplicit
    | netTypeIdentifier
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

dataTypeOrVoid
    : dataType
    | VOID
    ;

// A.2.3 Declaration Lists /////////////////////////////////////////////////////////////////////////////////////////////

listOfVariableDeclAssignments
    : variableDeclAssignment (COMMA variableDeclAssignment)*
    ;

// A.2.4 Declaration Assignments ///////////////////////////////////////////////////////////////////////////////////////

variableDeclAssignment
    : variableIdentifier variableDimension*
    ;

// A.2.5 Declaration Ranges ////////////////////////////////////////////////////////////////////////////////////////////

unpackedDimension
    : LBRACK constantRange RBRACK      # unpackedDimensionRange
    | LBRACK constantExpression RBRACK # unpackedDimensionExpression
    ;

packedDimension
    : LBRACK constantRange RBRACK # packedDimensionRange
    | unsizedDimension            # packedDimensionUnsized
    ;

associativeDimension
    : LBRACK dataType RBRACK # associativeDimensionDataType
    | LBRACK STAR RBRACK     # associativeDimensionStar
    ;

variableDimension
    : unsizedDimension
    | unpackedDimension
    | associativeDimension
    | queueDimension
    ;

queueDimension
    : LBRACK DOLLAR RBRACK
    ;

unsizedDimension
    : LBRACK RBRACK
    ;

// A.2.6 Function Declarations /////////////////////////////////////////////////////////////////////////////////////////

functionDataTypeOrImplicit
    : dataTypeOrVoid
    | implicitDataType
    ;

functionDeclaration
    : FUNCTION lifetime? functionBodyDeclaration
    ;

functionBodyDeclaration
    : functionDataTypeOrImplicit functionIdentifier SEMICOLON functionStatementOrNull* ENDFUNCTION
      (COLON functionIdentifier)?
      # functionBodyDeclarationNoPortList
    | functionDataTypeOrImplicit functionIdentifier LPAREN tfPortList? RPAREN blockItemDeclaration*
      functionStatementOrNull* ENDFUNCTION (COLON functionIdentifier)?
      # functionBodyDeclarationPortList
    ;

// A.2.7 Task Declarations /////////////////////////////////////////////////////////////////////////////////////////////

tfPortList
    : tfPortItem (COMMA tfPortItem)*
    ;

tfPortItem
    : attributeInstance* tfPortDirection? VAR? dataTypeOrImplicit portIdentifier variableDimension*
    ;

tfPortDirection
    : portDirection
    | CONST REF
    ;

// A.2.8 Block Item Declarations ///////////////////////////////////////////////////////////////////////////////////////

blockItemDeclaration
    : attributeInstance* dataDeclaration # blockItemDeclarationData
    ;

// A.6.4 Statements ////////////////////////////////////////////////////////////////////////////////////////////////////

statement
    : (blockIdentifier COLON)? attributeInstance* statementItem
    ;

statementItem
    : jumpStatement # statementItemJump
    ;

functionStatement
    : statement
    ;

functionStatementOrNull
    : functionStatement              # functionStatementOrNullFunction
    | attributeInstance* SEMICOLON # functionStatementOrNullNull
    ;

// A.6.5 Timing Control Statements /////////////////////////////////////////////////////////////////////////////////////

jumpStatement
    : BREAK SEMICOLON    # jumpStatementBreak
    | CONTINUE SEMICOLON # jumpStatementContinue
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
    | STRING_LITERAL
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

// A.9.1 Attributes ////////////////////////////////////////////////////////////////////////////////////////////////////

attributeInstance
    : LPAREN_STAR attrSpec (COMMA attrSpec)* RPAREN_STAR
    ;

attrSpec
    : attrName (EQ constantExpression)?
    ;

attrName
    : identifier
    ;

// A.9.3 Identifiers ///////////////////////////////////////////////////////////////////////////////////////////////////

blockIdentifier
    : identifier
    ;

classIdentifier
    : identifier
    ;

functionIdentifier
    : identifier
    ;

identifier
    : SIMPLE_IDENTIFIER
    ;

moduleIdentifier
    : identifier
    ;

netTypeIdentifier
    : identifier
    ;

packageIdentifier
    : identifier
    ;

packageScope
    : packageIdentifier COLON2  # packageScopeIdentifier
    | UNIT COLON2               # packageScopeUnit
    ;

portIdentifier
    : identifier
    ;

psClassIdentifier
    : packageScope? classIdentifier
    ;

variableIdentifier
    : identifier
    ;