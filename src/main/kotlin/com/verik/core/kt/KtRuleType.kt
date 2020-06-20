package com.verik.core.kt

// Copyright (c) 2020 Francis Wang

enum class KtRuleType {
    KOTLIN_FILE,                            // [PACKAGE_HEADER] [IMPORT_LIST] [TOP_LEVEL_OBJECT]* EOF
    PACKAGE_HEADER,                         // (PACKAGE [IDENTIFIER] [SEMI]?)?
    IMPORT_LIST,                            // [IMPORT_HEADER]*
    IMPORT_HEADER,                          // IMPORT [IDENTIFIER] (DOT MULT)? [SEMI]?
    TOP_LEVEL_OBJECT,                       // [DECLARATION] [SEMIS]?
    DECLARATION,                            // [CLASS_DECLARATION] | [FUNCTION_DECLARATION] | [PROPERTY_DECLARATION]
    CLASS_DECLARATION,
    PRIMARY_CONSTRUCTOR,                    // ([MODIFIERS]? CONSTRUCTOR NL*)? [CLASS_PARAMETERS]
    CLASS_BODY,                             // LCURL NL* [CLASS_MEMBER_DECLARATIONS] NL* RCURL
    CLASS_PARAMETERS,
    CLASS_PARAMETER,
    DELEGATION_SPECIFIERS,
    DELEGATION_SPECIFIER,
    CONSTRUCTOR_INVOCATION,
    ANNOTATED_DELEGATION_SPECIFIER,
    CLASS_MEMBER_DECLARATIONS,              // ([CLASS_MEMBER_DECLARATION] [SEMIS]?)*
    CLASS_MEMBER_DECLARATION,
    COMPANION_OBJECT,
    FUNCTION_VALUE_PARAMETERS,
    FUNCTION_VALUE_PARAMETER,
    FUNCTION_DECLARATION,
    FUNCTION_BODY,                          // [BLOCK] | ASSIGNMENT NL* [EXPRESSION]
    VARIABLE_DECLARATION,
    PROPERTY_DECLARATION,
    PARAMETER,                              // [SIMPLE_IDENTIFIER] NL* COLON NL* [TYPE]
    ENUM_CLASS_BODY,                        // LCURL NL* [ENUM_ENTRIES]? (NL* SEMICOLON NL* [CLASS_MEMBER_DECLARATIONS])? NL* RCURL
    ENUM_ENTRIES,                           // [ENUM_ENTRY] (NL* COMMA NL* [ENUM_ENTRY])* NL* COMMA?
    ENUM_ENTRY,                             // ([MODIFIERS] NL*)? [SIMPLE_IDENTIFIER] (NL* [VALUE_ARGUMENTS])? (NL* [CLASS_BODY])?
    TYPE,
    TYPE_REFERENCE,
    USER_TYPE,
    SIMPLE_USER_TYPE,
    STATEMENTS,                             // ([STATEMENT] ([SEMIS] [STATEMENT])*)? [SEMIS]?
    STATEMENT,
    LABEL,                                  // [SIMPLE_IDENTIFIER] (AT_NO_WS | AT_POST_WS) NL*
    BLOCK,                                  // LCURL NL* [STATEMENTS] NL* RCURL
    LOOP_STATEMENT,                         // [FOR_STATEMENT] | [WHILE_STATEMENT] | [DO_WHILE_STATEMENT]
    FOR_STATEMENT,
    WHILE_STATEMENT,
    DO_WHILE_STATEMENT,
    SEMI,                                   // (SEMICOLON | NL) NL* | EOF
    SEMIS,                                  // (SEMICOLON | NL)+ | EOF
    EXPRESSION,                             // [DISJUNCTION]
    DISJUNCTION,                            // [CONJUNCTION] (NL* DISJ NL* [CONJUNCTION])*
    CONJUNCTION,                            // [EQUALITY] (NL* CONJ NL* [EQUALITY])*
    EQUALITY,
    COMPARISON,
    INFIX_OPERATION,
    ELVIS_EXPRESSION,                       // [INFIX_FUNCTION_CALL] (NL* [ELVIS] NL* [INFIX_FUNCTION_CALL])*
    ELVIS,                                  // QUEST_NO_WS COLON
    INFIX_FUNCTION_CALL,                    // [RANGE_EXPRESSION] ([SIMPLE_IDENTIFIER] NL* [RANGE_EXPRESSION])*
    RANGE_EXPRESSION,                       // [ADDITIVE_EXPRESSION] (RANGE NL* [ADDITIVE_EXPRESSION])*
    ADDITIVE_EXPRESSION,                    // [MULTIPLICATIVE_EXPRESSION] ([ADDITIVE_OPERATOR] NL* [MULTIPLICATIVE_EXPRESSION])*
    MULTIPLICATIVE_EXPRESSION,              // [AS_EXPRESSION] ([MULTIPLICATIVE_OPERATOR] NL* [AS_EXPRESSION])*
    AS_EXPRESSION,
    COMPARISON_WITH_LITERAL_RIGHT_SIDE,
    PREFIX_UNARY_EXPRESSION,
    POSTFIX_UNARY_EXPRESSION,
    POSTFIX_UNARY_SUFFIX,
    INDEXING_SUFFIX,
    NAVIGATION_SUFFIX,
    CALL_SUFFIX,
    VALUE_ARGUMENTS,
    PRIMARY_EXPRESSION,
    LITERAL_CONSTANT,
    STRING_LITERAL,
    LINE_STRING_LITERAL,
    LINE_STRING_CONTENT,
    JUMP_EXPRESSION,
    EQUALITY_OPERATOR,                      // EXCL_EQ | EQEQ
    COMPARISON_OPERATOR,                    // LANGLE | RANGLE | LE | GE
    ADDITIVE_OPERATOR,                      // ADD | SUB
    MULTIPLICATIVE_OPERATOR,                // MULT
    PREFIX_UNARY_OPERATOR,                  // INCR | DECR | SUB | ADD | [EXCL]
    POSTFIX_UNARY_OPERATOR,
    MEMBER_ACCESS_OPERATOR,                 // DOT
    MODIFIERS,                              // ([ANNOTATION] | [MODIFIER])+
    MODIFIER,
    CLASS_MODIFIER,
    FUNCTION_MODIFIER,                      // OPERATOR
    PROPERTY_MODIFIER,
    ANNOTATION,                             // [SINGLE_ANNOTATION] NL*
    SINGLE_ANNOTATION,                      // (AT_NO_WS | AT_PRE_WS) [UNESCAPED_ANNOTATION]
    UNESCAPED_ANNOTATION,                   // [CONSTRUCTOR_INVOCATION] | [USER_TYPE]
    SIMPLE_IDENTIFIER,
    IDENTIFIER;                             // [SIMPLE_IDENTIFIER] (NL* DOT [SIMPLE_IDENTIFIER])*

    companion object {
        fun getType(type: String): KtRuleType? {
            return when (type) {
                "kotlinFile" -> KOTLIN_FILE
                "packageHeader" -> PACKAGE_HEADER
                "importList" -> IMPORT_LIST
                "importHeader" -> IMPORT_HEADER
                "topLevelObject" -> TOP_LEVEL_OBJECT
                "declaration" -> DECLARATION
                "classDeclaration" -> CLASS_DECLARATION
                "primaryConstructor" -> PRIMARY_CONSTRUCTOR
                "classBody" -> CLASS_BODY
                "classParameters" -> CLASS_PARAMETERS
                "classParameter" -> CLASS_PARAMETER
                "delegationSpecifiers" -> DELEGATION_SPECIFIERS
                "delegationSpecifier" -> DELEGATION_SPECIFIER
                "constructorInvocation" -> CONSTRUCTOR_INVOCATION
                "annotatedDelegationSpecifier" -> ANNOTATED_DELEGATION_SPECIFIER
                "classMemberDeclarations" -> CLASS_MEMBER_DECLARATIONS
                "classMemberDeclaration" -> CLASS_MEMBER_DECLARATION
                "companionObject" -> COMPANION_OBJECT
                "functionValueParameters" -> FUNCTION_VALUE_PARAMETERS
                "functionValueParameter" -> FUNCTION_VALUE_PARAMETER
                "functionDeclaration" -> FUNCTION_DECLARATION
                "functionBody" -> FUNCTION_BODY
                "variableDeclaration" -> VARIABLE_DECLARATION
                "propertyDeclaration" -> PROPERTY_DECLARATION
                "parameter" -> PARAMETER
                "enumClassBody" -> ENUM_CLASS_BODY
                "enumEntries" -> ENUM_ENTRIES
                "enumEntry" -> ENUM_ENTRY
                "type" -> TYPE
                "typeReference" -> TYPE_REFERENCE
                "userType" -> USER_TYPE
                "simpleUserType" -> SIMPLE_USER_TYPE
                "statements" -> STATEMENTS
                "statement" -> STATEMENT
                "label" -> LABEL
                "block" -> BLOCK
                "loopStatement" -> LOOP_STATEMENT
                "forStatement" -> FOR_STATEMENT
                "whileStatement" -> WHILE_STATEMENT
                "doWhileStatement" -> DO_WHILE_STATEMENT
                "semi" -> SEMI
                "semis" -> SEMIS
                "expression" -> EXPRESSION
                "disjunction" -> DISJUNCTION
                "conjunction" -> CONJUNCTION
                "equality" -> EQUALITY
                "comparison" -> COMPARISON
                "infixOperation" -> INFIX_OPERATION
                "elvisExpression" -> ELVIS_EXPRESSION
                "elvis" -> ELVIS
                "infixFunctionCall" -> INFIX_FUNCTION_CALL
                "rangeExpression" -> RANGE_EXPRESSION
                "additiveExpression" -> ADDITIVE_EXPRESSION
                "multiplicativeExpression" -> MULTIPLICATIVE_EXPRESSION
                "asExpression" -> AS_EXPRESSION
                "comparisonWithLiteralRightSide" -> COMPARISON_WITH_LITERAL_RIGHT_SIDE
                "prefixUnaryExpression" -> PREFIX_UNARY_EXPRESSION
                "postfixUnaryExpression" -> POSTFIX_UNARY_EXPRESSION
                "postfixUnarySuffix" -> POSTFIX_UNARY_SUFFIX
                "indexingSuffix" -> INDEXING_SUFFIX
                "navigationSuffix" -> NAVIGATION_SUFFIX
                "callSuffix" -> CALL_SUFFIX
                "valueArguments" -> VALUE_ARGUMENTS
                "primaryExpression" -> PRIMARY_EXPRESSION
                "literalConstant" -> LITERAL_CONSTANT
                "stringLiteral" -> STRING_LITERAL
                "lineStringLiteral" -> LINE_STRING_LITERAL
                "lineStringContent" -> LINE_STRING_CONTENT
                "jumpExpression" -> JUMP_EXPRESSION
                "equalityOperator" -> EQUALITY_OPERATOR
                "comparisonOperator" -> COMPARISON_OPERATOR
                "additiveOperator" -> ADDITIVE_OPERATOR
                "multiplicativeOperator" -> MULTIPLICATIVE_OPERATOR
                "prefixUnaryOperator" -> PREFIX_UNARY_OPERATOR
                "postfixUnaryOperator" -> POSTFIX_UNARY_OPERATOR
                "memberAccessOperator" -> MEMBER_ACCESS_OPERATOR
                "modifiers" -> MODIFIERS
                "modifier" -> MODIFIER
                "classModifier" -> CLASS_MODIFIER
                "functionModifier" -> FUNCTION_MODIFIER
                "propertyModifier" -> PROPERTY_MODIFIER
                "annotation" -> ANNOTATION
                "singleAnnotation" -> SINGLE_ANNOTATION
                "unescapedAnnotation" -> UNESCAPED_ANNOTATION
                "simpleIdentifier" -> SIMPLE_IDENTIFIER
                "identifier" -> IDENTIFIER
                else -> null
            }
        }
    }
}