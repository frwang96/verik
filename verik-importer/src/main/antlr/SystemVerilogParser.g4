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
    | initialConstruct
    | alwaysConstruct
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
    | integerAtomType signing?                    # dataTypeInteger
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

integerAtomType
    : BYTE
    | SHORTINT
    | INT
    | LONGINT
    | INTEGER
    | TIME
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

// A.2.2.3 Delays //////////////////////////////////////////////////////////////////////////////////////////////////////

delayValue
    : UNSIGNED_NUMBER
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

// A.6.1 Continuous Assignment and Net Alias Statements ////////////////////////////////////////////////////////////////

listOfVariableAssignments
    : variableAssignment (COMMA variableAssignment)*
    ;

// A.6.2 Procedural Blocks and Assignmemts /////////////////////////////////////////////////////////////////////////////

initialConstruct
    : INITIAL statementOrNull
    ;

alwaysConstruct
    : alwaysKeyword statement
    ;

alwaysKeyword
    : ALWAYS
    | ALWAYS_COMB
    | ALWAYS_LATCH
    | ALWAYS_FF
    ;

blockingAssignment
    : variableLeftValue EQ delayOrEventControl? expression
    ;

nonBlockingAssignment
    : variableLeftValue LT_EQ delayOrEventControl? expression
    ;

variableAssignment
    : variableLeftValue EQ expression
    ;

// A.6.3 Parallel and Sequential Blocks ////////////////////////////////////////////////////////////////////////////////

seqBlock
    : BEGIN (COLON blockIdentifier)? blockItemDeclaration* statementOrNull* END (COLON blockIdentifier)?
    ;

// A.6.4 Statements ////////////////////////////////////////////////////////////////////////////////////////////////////

statementOrNull
    : statement
    | attributeInstance* SEMICOLON
    ;

statement
    : (blockIdentifier COLON)? attributeInstance* statementItem
    ;

statementItem
    : blockingAssignment SEMICOLON     # statementItemBlocking
    | nonBlockingAssignment SEMICOLON  # statementItemNonBlocking
    | conditionalStatement             # statementItemConditional
    | loopStatement                    # statementItemLoop
    | jumpStatement                    # statementItemJump
    | seqBlock                         # statementItemSeqBlock
    | proceduralTimingControlStatement # statementItemTiming
    ;

functionStatement
    : statement
    ;

functionStatementOrNull
    : functionStatement              # functionStatementOrNullFunction
    | attributeInstance* SEMICOLON # functionStatementOrNullNull
    ;

// A.6.5 Timing Control Statements /////////////////////////////////////////////////////////////////////////////////////

proceduralTimingControlStatement
    : proceduralTimingControl statementOrNull
    ;

delayOrEventControl
    : delayControl                                 # delayOrEventControlDelay
    | eventControl                                 # delayOrEventControlEvent
    | REPEAT LPAREN expression RPAREN eventControl # delayOrEventControlRepeat
    ;

delayControl
    : SHARP delayValue                        # delayControlValue
    | SHARP LPAREN minTypMaxExpression RPAREN # delayControlExpression
    ;

eventControl
    : AT hierarchicalEventIdentifier   # eventControlAtIdentifier
    | AT LPAREN eventExpression RPAREN # eventControlAtParen
    | AT_STAR                          # eventControlAtStar
    ;

eventExpression
    : edgeIdentifier? expression (IFF expression)? # eventExpressionExpression
    | LPAREN eventExpression RPAREN                # eventExpressionParen
    ;

proceduralTimingControl
    : eventControl
    ;

jumpStatement
    : RETURN expression? SEMICOLON # jumpStatementReturn
    | BREAK SEMICOLON              # jumpStatementBreak
    | CONTINUE SEMICOLON           # jumpStatementContinue
    ;

// A.6.6 Conditional Statements ////////////////////////////////////////////////////////////////////////////////////////

conditionalStatement
    : uniquePriority? IF LPAREN condPredicate RPAREN statementOrNull
      (ELSE IF LPAREN condPredicate RPAREN statementOrNull)* (ELSE statementOrNull)
    ;

uniquePriority
    : UNIQUE
    | UNIQUE0
    | PRIORITY
    ;

condPredicate
    : expressionOrCondPattern (AND3 expressionOrCondPattern)*
    ;

expressionOrCondPattern
    : expression
    ;

// A.6.8 Looping Statements ////////////////////////////////////////////////////////////////////////////////////////////

loopStatement
    : FOREVER statementOrNull
      # loopStatementForever
    | FOR LPAREN forInitialization? SEMICOLON expression? SEMICOLON forStep? RPAREN statementOrNull
      # loopStatementFor
    ;

forInitialization
    : listOfVariableAssignments                              # forInitializationAssignment
    | forVariableDeclaration (COMMA forVariableDeclaration)* # forInitializationDeclaration
    ;

forVariableDeclaration
    : VAR? dataType variableIdentifier EQ expression (COMMA variableIdentifier EQ expression)*
    ;

forStep
    : forStepAssignment (COMMA forStepAssignment)*
    ;

forStepAssignment
    : incOrDecExpression
    ;

// A.7.4 Specify Path Delays ///////////////////////////////////////////////////////////////////////////////////////////

edgeIdentifier
    : POSEDGE
    | NEGEDGE
    | EDGE
    ;

// A.8.3 Expressions ///////////////////////////////////////////////////////////////////////////////////////////////////

incOrDecExpression
    : incOrDecOperator attributeInstance* variableLeftValue # incOrDecExpressionPrefix
    | variableLeftValue attributeInstance* incOrDecOperator # incOrDecExpressionPostfix
    ;

constantExpression
    : constantPrimary                                                         # constantExpressionPrimary
    | constantExpression binaryOperator attributeInstance* constantExpression # constantExpressionBinary
    ;

constantRange
    : constantExpression COLON constantExpression
    ;

expression
    : primary                                                 # expressionPrimary
    | expression binaryOperator attributeInstance* expression # expressionBinary
    ;

minTypMaxExpression
    : expression                                   # minTypMaxExpressionNoColon
    | expression COLON expression COLON expression # minTypMaxExpressionColon
    ;

partSelectRange
    : constantRange
    | indexedRange
    ;

indexedRange
    : expression (PLUS_COLON | MINUS_COLON) constantExpression
    ;

// A.8.4 Primaries /////////////////////////////////////////////////////////////////////////////////////////////////////

constantPrimary
    : primaryLiteral
    ;

primary
    : primaryLiteral                # primaryPrimaryLiteral
    | hierarchicalIdentifier select # primaryHierarchical
    | THIS                          # primaryThis
    ;

primaryLiteral
    : number
    | STRING_LITERAL
    ;

bitSelect
    : (LBRACK expression RBRACK)*
    ;

select
    : ((DOT memberIdentifier bitSelect)* DOT memberIdentifier)? bitSelect (LBRACK partSelectRange RBRACK)?
    ;

constantBitSelect
    : (LBRACK constantExpression RBRACK)*
    ;

// A.8.5 Expression Left-Side Values ///////////////////////////////////////////////////////////////////////////////////

variableLeftValue
    : hierarchicalVariableIdentifier select
    ;

// A.8.6 Operators /////////////////////////////////////////////////////////////////////////////////////////////////////

binaryOperator
    : PLUS
    | MINUS
    | STAR
    | SLASH
    | MOD
    | EQ2
    | BANG_EQ
    | EQ3
    | BANG_EQ2
    | EQ2_QUEST
    | BANG_EQ_QUEST
    | AND2
    | OR2
    | STAR2
    | LT
    | LT_EQ
    | GT
    | GT_EQ
    | AND
    | OR
    | CARET
    | CARET_NOT
    | NOT_CARET
    | GT2
    | LT2
    | GT3
    | LT3
    | MINUS_GT
    | LT_MINUS_GT
    ;

incOrDecOperator
    : PLUS2
    | MINUS2
    ;

// A.8.7 Numbers ///////////////////////////////////////////////////////////////////////////////////////////////////////

number
    : integralNumber
    ;

integralNumber
    : UNSIGNED_NUMBER
    | DECIMAL_NUMBER
    | BINARY_NUMBER
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

hierarchicalEventIdentifier
    : hierarchicalIdentifier
    ;

hierarchicalIdentifier
    : (ROOT DOT)? (identifier constantBitSelect DOT)* identifier
    ;

hierarchicalVariableIdentifier
    : hierarchicalIdentifier
    ;

identifier
    : SIMPLE_IDENTIFIER
    ;

memberIdentifier
    : identifier
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