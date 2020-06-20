package com.verik.core.kt

// Copyright (c) 2020 Francis Wang

enum class KtRuleType {
    KOTLIN_FILE,                            // [PACKAGE_HEADER] [IMPORT_LIST] [TOP_LEVEL_OBJECT]* EOF
    PACKAGE_HEADER,                         // (PACKAGE [IDENTIFIER] [SEMI]?)?
    IMPORT_LIST,                            // [IMPORT_HEADER]*
    IMPORT_HEADER,
    TOP_LEVEL_OBJECT,
    DECLARATION,
    CLASS_DECLARATION,
    PRIMARY_CONSTRUCTOR,
    CLASS_BODY,
    CLASS_PARAMETERS,
    CLASS_PARAMETER,
    DELEGATION_SPECIFIERS,
    DELEGATION_SPECIFIER,
    CONSTRUCTOR_INVOCATION,
    ANNOTATED_DELEGATION_SPECIFIER,
    CLASS_MEMBER_DECLARATIONS,
    CLASS_MEMBER_DECLARATION,
    FUNCTION_VALUE_PARAMETERS,
    FUNCTION_VALUE_PARAMETER,
    FUNCTION_DECLARATION,
    FUNCTION_BODY,
    VARIABLE_DECLARATION,
    PROPERTY_DECLARATION,
    PARAMETER,
    TYPE,
    TYPE_REFERENCE,
    USER_TYPE,
    SIMPLE_USER_TYPE,
    STATEMENTS,
    STATEMENT,
    BLOCK,
    SEMI,                                   // (SEMICOLON | NL) NL* | EOF
    SEMIS,                                  // (SEMICOLON | NL)+ | EOF
    EXPRESSION,
    DISJUNCTION,
    CONJUNCTION,
    EQUALITY,
    COMPARISON,
    INFIX_OPERATION,
    ELVIS_EXPRESSION,
    INFIX_FUNCTION_CALL,
    RANGE_EXPRESSION,
    ADDITIVE_EXPRESSION,
    MULTIPLICATIVE_EXPRESSION,
    AS_EXPRESSION,
    COMPARISON_WITH_LITERAL_RIGHT_SIDE,
    PREFIX_UNARY_EXPRESSION,
    POSTFIX_UNARY_EXPRESSION,
    POSTFIX_UNARY_SUFFIX,
    VALUE_ARGUMENTS,
    PRIMARY_EXPRESSION,
    LITERAL_CONSTANT,
    STRING_LITERAL,
    LINE_STRING_LITERAL,
    LINE_STRING_CONTENT,
    JUMP_EXPRESSION,
    ADDITIVE_OPERATOR,
    MULTIPLICATIVE_OPERATOR,
    POSTFIX_UNARY_OPERATOR,
    MODIFIERS,
    MODIFIER,
    PROPERTY_MODIFIER,
    SIMPLE_IDENTIFIER,
    IDENTIFIER;

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
                "functionValueParameters" -> FUNCTION_VALUE_PARAMETERS
                "functionValueParameter" -> FUNCTION_VALUE_PARAMETER
                "functionDeclaration" -> FUNCTION_DECLARATION
                "functionBody" -> FUNCTION_BODY
                "variableDeclaration" -> VARIABLE_DECLARATION
                "propertyDeclaration" -> PROPERTY_DECLARATION
                "parameter" -> PARAMETER
                "type" -> TYPE
                "typeReference" -> TYPE_REFERENCE
                "userType" -> USER_TYPE
                "simpleUserType" -> SIMPLE_USER_TYPE
                "statements" -> STATEMENTS
                "statement" -> STATEMENT
                "block" -> BLOCK
                "semi" -> SEMI
                "semis" -> SEMIS
                "expression" -> EXPRESSION
                "disjunction" -> DISJUNCTION
                "conjunction" -> CONJUNCTION
                "equality" -> EQUALITY
                "comparison" -> COMPARISON
                "infixOperation" -> INFIX_OPERATION
                "elvisExpression" -> ELVIS_EXPRESSION
                "infixFunctionCall" -> INFIX_FUNCTION_CALL
                "rangeExpression" -> RANGE_EXPRESSION
                "additiveExpression" -> ADDITIVE_EXPRESSION
                "multiplicativeExpression" -> MULTIPLICATIVE_EXPRESSION
                "asExpression" -> AS_EXPRESSION
                "comparisonWithLiteralRightSide" -> COMPARISON_WITH_LITERAL_RIGHT_SIDE
                "prefixUnaryExpression" -> PREFIX_UNARY_EXPRESSION
                "postfixUnaryExpression" -> POSTFIX_UNARY_EXPRESSION
                "postfixUnarySuffix" -> POSTFIX_UNARY_SUFFIX
                "valueArguments" -> VALUE_ARGUMENTS
                "primaryExpression" -> PRIMARY_EXPRESSION
                "literalConstant" -> LITERAL_CONSTANT
                "stringLiteral" -> STRING_LITERAL
                "lineStringLiteral" -> LINE_STRING_LITERAL
                "lineStringContent" -> LINE_STRING_CONTENT
                "jumpExpression" -> JUMP_EXPRESSION
                "additiveOperator" -> ADDITIVE_OPERATOR
                "multiplicativeOperator" -> MULTIPLICATIVE_OPERATOR
                "postfixUnaryOperator" -> POSTFIX_UNARY_OPERATOR
                "modifiers" -> MODIFIERS
                "modifier" -> MODIFIER
                "propertyModifier" -> PROPERTY_MODIFIER
                "simpleIdentifier" -> SIMPLE_IDENTIFIER
                "identifier" -> IDENTIFIER
                else -> null
            }
        }
    }
}