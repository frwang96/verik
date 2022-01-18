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
    : attributeInstance* moduleInstantiation # moduleOrGenerateItemInstantiation
    | attributeInstance* moduleCommonItem    # moduleOrGenerateItemCommon
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

castingType
    : simpleType
    | constantPrimary
    | signing
    | STRING
    | CONST
    ;

dataType
    : integerVectorType signing? packedDimension* # dataTypeVector
    | integerAtomType signing?                    # dataTypeInteger
    | STRING                                      # dataTypeString
    | classType                                   # dataTypeClassType
    | EVENT                                       # dataTypeEvent
    ;

dataTypeOrImplicit
    : dataType
    | implicitDataType
    ;

implicitDataType
    : signing? packedDimension*
    ;

classScope
    : classType COLON2
    ;

classType
    : psClassIdentifier
    ;

integerType
    : integerVectorType
    | integerAtomType
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

simpleType
    : integerType
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
    : variableIdentifier variableDimension* (EQ expression)?
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

// A.4.1.1 Module Instantiations ///////////////////////////////////////////////////////////////////////////////////////

moduleInstantiation
    : moduleIdentifier parameterValueAssignment? hierarchicalInstance (COMMA hierarchicalInstance)* SEMICOLON
    ;

parameterValueAssignment
    : SHARP LPAREN listOfParameterAssignments? RPAREN
    ;

listOfParameterAssignments
    : orderedParameterAssignment (COMMA orderedParameterAssignment)* # listOfParameterAssignmentsOrdered
    | namedParameterAssignment (COMMA namedParameterAssignment)*     # listOfParameterAssignmentsNamed
    ;

orderedParameterAssignment
    : paramExpression
    ;

namedParameterAssignment
    : DOT parameterIdentifier LPAREN paramExpression? RPAREN
    ;

hierarchicalInstance
    : nameOfInstance LPAREN listOfPortConnections RPAREN
    ;

nameOfInstance
    : instanceIdentifier unpackedDimension*
    ;

listOfPortConnections
    : orderedPortConnection (COMMA orderedPortConnection)* # listOfPortConnectionsOrdered
    | namedPortConnection (COMMA namedPortConnection)*     # listOfPortConnectionsNamed
    ;

orderedPortConnection
    : attributeInstance* expression?
    ;

namedPortConnection
    : attributeInstance* DOT portIdentifier (LPAREN expression? RPAREN)? # namedPortConnectionIdentifier
    | attributeInstance* DOT_STAR                                        # namedPortConnectionStar
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

actionBlock
    : statementOrNull                 # actionBlockNoElse
    | statement? ELSE statementOrNull # actionBlockElse
    ;

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
    | caseStatement                    # statementItemCase
    | conditionalStatement             # statementItemConditional
    | incOrDecExpression               # statementItemIncOrDec
    | subroutineCallStatement          # statementItemSubroutine
    | loopStatement                    # statementItemLoop
    | jumpStatement                    # statementItemJump
    | proceduralTimingControlStatement # statementItemTiming
    | seqBlock                         # statementItemSeqBlock
    | proceduralAssertionStatement     # statementItemAssert
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
      (ELSE IF LPAREN condPredicate RPAREN statementOrNull)* (ELSE statementOrNull)?
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

// A.6.7 Case Statements ///////////////////////////////////////////////////////////////////////////////////////////////

caseStatement
    : uniquePriority? caseKeyword LPAREN caseExpression RPAREN caseItem+ ENDCASE                # caseStatementRegular
    | uniquePriority? caseKeyword LPAREN caseExpression RPAREN MATCHES casePatternItem+ ENDCASE # caseStatementMatches
    ;

caseKeyword
    : CASE
    | CASEZ
    | CASEX
    ;

caseExpression
    : expression
    ;

caseItem
    : caseItemExpression (COMMA caseItemExpression)* COLON statementOrNull # caseItemRegular
    | DEFAULT COLON? statementOrNull                                       # caseItemDefault
    ;

casePatternItem
    : pattern (AND3 expression)? COLON statementOrNull # casePatternRegular
    | DEFAULT COLON? statementOrNull                   # casePatternDefault
    ;

caseItemExpression
    : expression
    ;

// A.6.7.1 Patterns ////////////////////////////////////////////////////////////////////////////////////////////////////

pattern
    : DOT variableIdentifier # patternVariable
    | DOT_STAR               # patternStar
    ;

assignmentPattern
    : TICK_LBRACE structurePatternKey COLON expression (COMMA structurePatternKey COLON expression)* RBRACE
      # assignmentPatternStructure
    | TICK_LBRACE constantExpression LBRACE expression (COMMA expression)* RBRACE RBRACE
      # assignmentPatternConstant
    ;

structurePatternKey
    : memberIdentifier
    | assignmentPatternKey
    ;

assignmentPatternKey
    : simpleType
    | DEFAULT
    ;

assignmentPatternExpression
    : assignmentPatternExpressionType? assignmentPattern
    ;

assignmentPatternExpressionType
    : integerAtomType
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

// A.6.9 Subroutine Call Statements ////////////////////////////////////////////////////////////////////////////////////

subroutineCallStatement
    : subroutineCall SEMICOLON                                 # subroutineCallStatementSubroutine
    | VOID TICK LPAREN functionSubroutineCall RPAREN SEMICOLON # subroutineCallStatementFunction
    ;

// A.6.10 Assertion Statements /////////////////////////////////////////////////////////////////////////////////////////

proceduralAssertionStatement
    : immediateAssertionStatement
    ;

immediateAssertionStatement
    : simpleImmediateAssertionStatement
    ;

simpleImmediateAssertionStatement
    : simpleImmediateAssertStatement
    | simpleImmediateAssumeStatement
    ;

simpleImmediateAssertStatement
    : ASSERT LPAREN expression RPAREN actionBlock
    ;

simpleImmediateAssumeStatement
    : ASSUME LPAREN expression RPAREN actionBlock
    ;

// A.7.4 Specify Path Delays ///////////////////////////////////////////////////////////////////////////////////////////

edgeIdentifier
    : POSEDGE
    | NEGEDGE
    | EDGE
    ;

// A.8.1 Concatenations ////////////////////////////////////////////////////////////////////////////////////////////////

concatenation
    : LBRACE expression (COMMA expression)* RBRACE
    ;

multipleConcatenation
    : LBRACE expression concatenation RBRACE
    ;

// A.8.2 Subroutine Calls //////////////////////////////////////////////////////////////////////////////////////////////

tfCall
    : psOrHierarchicalTfIdentifier attributeInstance* (LPAREN listOfArguments RPAREN)?
    ;

systemTfCall
    : systemTfIdentifier (LPAREN listOfArguments RPAREN)?           # systemIfCallArguments
    | systemTfIdentifier LPAREN dataType (COMMA expression)? RPAREN # systemTfCallDataType
    ;

subroutineCall
    : tfCall       # subroutineCallTf
    | systemTfCall # subroutineCallSystemTf
    | methodCall   # subroutineCallMethod
    ;

functionSubroutineCall
    : subroutineCall
    ;

listOfArguments
    : expression? (COMMA expression?)* (COMMA DOT identifier LPAREN expression? RPAREN)*
    | DOT identifier LPAREN expression? RPAREN (COMMA DOT identifier LPAREN expression? RPAREN)*
    ;

methodCall
    : methodCallRoot DOT methodCallBody
    | methodCallBody
    ;

methodCallBody
    : methodIdentifier attributeInstance* (LPAREN listOfArguments RPAREN)*
    ;

methodCallRoot
    : primary
    ;

// A.8.3 Expressions ///////////////////////////////////////////////////////////////////////////////////////////////////

incOrDecExpression
    : incOrDecOperator attributeInstance* variableLeftValue # incOrDecExpressionPrefix
    | variableLeftValue attributeInstance* incOrDecOperator # incOrDecExpressionPostfix
    ;

constantExpression
    : constantPrimary
      # constantExpressionPrimary
    | unaryOperator attributeInstance* constantPrimary
      # constantExpressionUnary
    | constantExpression binaryOperator attributeInstance* constantExpression
      # constantExpressionBinary
    | constantExpression QUEST attributeInstance* constantExpression COLON constantExpression
      # constantExpressionConditional
    ;

paramExpression
    : minTypMaxExpression
    | dataType
    | DOLLAR
    ;

constantPartSelectRange
    : constantRange
    | constantIndexedRange
    ;

constantRange
    : constantExpression COLON constantExpression
    ;

constantIndexedRange
    : constantExpression (PLUS_COLON | MINUS_COLON) constantExpression
    ;

expression
    : primary
      # expressionPrimary
    | unaryOperator attributeInstance* primary
      # expressionUnary
    | expression binaryOperator attributeInstance* expression
      # expressionBinary
    | expression (AND3 expressionOrCondPattern)* QUEST attributeInstance* expression COLON expression
      # expressionConditional // conditionalExpression
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
    : primaryLiteral                       # constantPrimaryLiteral
    | psParameterIdentifier constantSelect # constantPrimaryParameter
    ;

primary
    : primaryLiteral
      # primaryPrimaryLiteral
    | hierarchicalIdentifier select
      # primaryHierarchical
    | concatenation (LBRACK rangeExpression RBRACK)?
      # primaryConcatenation
    | multipleConcatenation (LBRACK rangeExpression RBRACK)?
      # primaryMultipleConcatenation
    | tfCall
      # primarySubroutineCallTf       // functionSubroutineCall
    | systemTfCall
      # primarySubroutineCallSystemTf // functionSubroutineCall
    | methodCallBody
      # primaryMethodCallNoPrimary    // methodCall
    | primary DOT? methodCallBody
      # primaryMethodCallPrimary      // methodCall
    | LPAREN minTypMaxExpression RPAREN
      # primaryMinTypMaxExpression
    | cast
      # primaryCast
    | assignmentPatternExpression
      # primaryAssignmentPatternExpression
    | THIS
      # primaryThis
    ;

rangeExpression
    : expression
    | partSelectRange
    ;

primaryLiteral
    : number
    | STRING_LITERAL
    ;

implicitClassHandle
    : THIS
    | SUPER
    | THIS DOT SUPER
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

constantSelect
    : ((DOT memberIdentifier constantBitSelect)* DOT memberIdentifier)? constantBitSelect
      (LBRACK constantPartSelectRange RBRACK)?
    ;

cast
    : castingType TICK LPAREN expression RPAREN
    ;

// A.8.5 Expression Left-Side Values ///////////////////////////////////////////////////////////////////////////////////

variableLeftValue
    : (implicitClassHandle DOT | packageScope)? hierarchicalVariableIdentifier select # variableLeftValueHierarchical
    | LBRACE variableLeftValue (COMMA variableLeftValue)* RBRACE                      # variableLeftValueConcatenate
    ;

// A.8.6 Operators /////////////////////////////////////////////////////////////////////////////////////////////////////

unaryOperator
    : PLUS
    | MINUS
    | BANG
    | NOT
    | AND
    | NOT_AND
    | OR
    | NOT_OR
    | CARET
    | NOT_CARET
    | CARET_NOT
    ;

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
    | OCTAL_NUMBER
    | BINARY_NUMBER
    | HEX_NUMBER
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

hierarchicalTfIdentifier
    : hierarchicalIdentifier
    ;

hierarchicalVariableIdentifier
    : hierarchicalIdentifier
    ;

identifier
    : SIMPLE_IDENTIFIER
    ;

instanceIdentifier
    : identifier
    ;

memberIdentifier
    : identifier
    ;

methodIdentifier
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

parameterIdentifier
    : identifier
    ;

portIdentifier
    : identifier
    ;

psClassIdentifier
    : packageScope? classIdentifier
    ;

psOrHierarchicalTfIdentifier
    : packageScope? tfIdentifier
    | hierarchicalTfIdentifier
    ;

psParameterIdentifier
    : (packageScope | classScope)? parameterIdentifier
    ;

systemTfIdentifier
    : SYSTEM_TF_IDENTIFIER
    ;

tfIdentifier
    : identifier
    ;

variableIdentifier
    : identifier
    ;