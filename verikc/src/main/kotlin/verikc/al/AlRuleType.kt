/*
 * Copyright 2020 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package verikc.al

import verikc.base.ast.Line
import verikc.base.ast.LineException

enum class AlRuleType {
    KOTLIN_FILE,
    PACKAGE_HEADER,
    IMPORT_LIST,
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
    TYPE_PARAMETERS,
    TYPE_PARAMETER,
    CLASS_MEMBER_DECLARATIONS,
    CLASS_MEMBER_DECLARATION,
    FUNCTION_VALUE_PARAMETERS,
    FUNCTION_VALUE_PARAMETER,
    FUNCTION_DECLARATION,
    FUNCTION_BODY,
    VARIABLE_DECLARATION,
    PROPERTY_DECLARATION,
    PARAMETER,
    ENUM_CLASS_BODY,
    ENUM_ENTRIES,
    ENUM_ENTRY,
    TYPE,
    TYPE_REFERENCE,
    USER_TYPE,
    SIMPLE_USER_TYPE,
    TYPE_PROJECTION,
    PARENTHESIZED_TYPE,
    STATEMENTS,
    STATEMENT,
    CONTROL_STRUCTURE_BODY,
    BLOCK,
    LOOP_STATEMENT,
    FOR_STATEMENT,
    WHILE_STATEMENT,
    DO_WHILE_STATEMENT,
    ASSIGNMENT,
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
    UNARY_PREFIX,
    POSTFIX_UNARY_EXPRESSION,
    POSTFIX_UNARY_SUFFIX,
    DIRECTLY_ASSIGNABLE_EXPRESSION,
    PARENTHESIZED_DIRECTLY_ASSIGNABLE_EXPRESSION,
    ASSIGNABLE_EXPRESSION,
    PARENTHESIZED_ASSIGNABLE_EXPRESSION,
    ASSIGNABLE_SUFFIX,
    INDEXING_SUFFIX,
    NAVIGATION_SUFFIX,
    CALL_SUFFIX,
    ANNOTATED_LAMBDA,
    TYPE_ARGUMENTS,
    VALUE_ARGUMENTS,
    VALUE_ARGUMENT,
    PRIMARY_EXPRESSION,
    PARENTHESIZED_EXPRESSION,
    LITERAL_CONSTANT,
    STRING_LITERAL,
    LINE_STRING_LITERAL,
    LINE_STRING_CONTENT,
    LINE_STRING_EXPRESSION,
    LAMBDA_LITERAL,
    LAMBDA_PARAMETERS,
    LAMBDA_PARAMETER,
    FUNCTION_LITERAL,
    THIS_EXPRESSION,
    SUPER_EXPRESSION,
    IF_EXPRESSION,
    WHEN_SUBJECT,
    WHEN_EXPRESSION,
    WHEN_ENTRY,
    WHEN_CONDITION,
    RANGE_TEST,
    TYPE_TEST,
    JUMP_EXPRESSION,
    ASSIGNMENT_AND_OPERATOR,
    EQUALITY_OPERATOR,
    COMPARISON_OPERATOR,
    IN_OPERATOR,
    IS_OPERATOR,
    ADDITIVE_OPERATOR,
    MULTIPLICATIVE_OPERATOR,
    AS_OPERATOR,
    PREFIX_UNARY_OPERATOR,
    POSTFIX_UNARY_OPERATOR,
    EXCL,
    MEMBER_ACCESS_OPERATOR,
    MODIFIERS,
    MODIFIER,
    CLASS_MODIFIER,
    MEMBER_MODIFIER,
    VISIBILITY_MODIFIER,
    INHERITANCE_MODIFIER,
    ANNOTATION,
    SINGLE_ANNOTATION,
    UNESCAPED_ANNOTATION,
    SIMPLE_IDENTIFIER,
    IDENTIFIER;

    companion object {
        operator fun invoke(type: String, line: Line): AlRuleType {
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
                "typeParameters" -> TYPE_PARAMETERS
                "typeParameter" -> TYPE_PARAMETER
                "classMemberDeclarations" -> CLASS_MEMBER_DECLARATIONS
                "classMemberDeclaration" -> CLASS_MEMBER_DECLARATION
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
                "typeProjection" -> TYPE_PROJECTION
                "parenthesizedType" -> PARENTHESIZED_TYPE
                "statements" -> STATEMENTS
                "statement" -> STATEMENT
                "controlStructureBody" -> CONTROL_STRUCTURE_BODY
                "block" -> BLOCK
                "loopStatement" -> LOOP_STATEMENT
                "forStatement" -> FOR_STATEMENT
                "whileStatement" -> WHILE_STATEMENT
                "doWhileStatement" -> DO_WHILE_STATEMENT
                "assignment" -> ASSIGNMENT
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
                "unaryPrefix" -> UNARY_PREFIX
                "postfixUnaryExpression" -> POSTFIX_UNARY_EXPRESSION
                "postfixUnarySuffix" -> POSTFIX_UNARY_SUFFIX
                "directlyAssignableExpression" -> DIRECTLY_ASSIGNABLE_EXPRESSION
                "parenthesizedDirectlyAssignableExpression" -> PARENTHESIZED_DIRECTLY_ASSIGNABLE_EXPRESSION
                "assignableExpression" -> ASSIGNABLE_EXPRESSION
                "parenthesizedAssignableExpression" -> PARENTHESIZED_ASSIGNABLE_EXPRESSION
                "assignableSuffix" -> ASSIGNABLE_SUFFIX
                "indexingSuffix" -> INDEXING_SUFFIX
                "navigationSuffix" -> NAVIGATION_SUFFIX
                "callSuffix" -> CALL_SUFFIX
                "annotatedLambda" -> ANNOTATED_LAMBDA
                "typeArguments" -> TYPE_ARGUMENTS
                "valueArguments" -> VALUE_ARGUMENTS
                "valueArgument" -> VALUE_ARGUMENT
                "primaryExpression" -> PRIMARY_EXPRESSION
                "parenthesizedExpression" -> PARENTHESIZED_EXPRESSION
                "literalConstant" -> LITERAL_CONSTANT
                "stringLiteral" -> STRING_LITERAL
                "lineStringLiteral" -> LINE_STRING_LITERAL
                "lineStringContent" -> LINE_STRING_CONTENT
                "lineStringExpression" -> LINE_STRING_EXPRESSION
                "lambdaLiteral" -> LAMBDA_LITERAL
                "lambdaParameters" -> LAMBDA_PARAMETERS
                "lambdaParameter" -> LAMBDA_PARAMETER
                "functionLiteral" -> FUNCTION_LITERAL
                "thisExpression" -> THIS_EXPRESSION
                "superExpression" -> SUPER_EXPRESSION
                "ifExpression" -> IF_EXPRESSION
                "whenSubject" -> WHEN_SUBJECT
                "whenExpression" -> WHEN_EXPRESSION
                "whenEntry" -> WHEN_ENTRY
                "whenCondition" -> WHEN_CONDITION
                "rangeTest" -> RANGE_TEST
                "typeTest" -> TYPE_TEST
                "jumpExpression" -> JUMP_EXPRESSION
                "assignmentAndOperator" -> ASSIGNMENT_AND_OPERATOR
                "equalityOperator" -> EQUALITY_OPERATOR
                "comparisonOperator" -> COMPARISON_OPERATOR
                "inOperator" -> IN_OPERATOR
                "isOperator" -> IS_OPERATOR
                "additiveOperator" -> ADDITIVE_OPERATOR
                "multiplicativeOperator" -> MULTIPLICATIVE_OPERATOR
                "asOperator" -> AS_OPERATOR
                "prefixUnaryOperator" -> PREFIX_UNARY_OPERATOR
                "postfixUnaryOperator" -> POSTFIX_UNARY_OPERATOR
                "excl" -> EXCL
                "memberAccessOperator" -> MEMBER_ACCESS_OPERATOR
                "modifiers" -> MODIFIERS
                "modifier" -> MODIFIER
                "classModifier" -> CLASS_MODIFIER
                "memberModifier" -> MEMBER_MODIFIER
                "visibilityModifier" -> VISIBILITY_MODIFIER
                "inheritanceModifier" -> INHERITANCE_MODIFIER
                "annotation" -> ANNOTATION
                "singleAnnotation" -> SINGLE_ANNOTATION
                "unescapedAnnotation" -> UNESCAPED_ANNOTATION
                "simpleIdentifier" -> SIMPLE_IDENTIFIER
                "identifier" -> IDENTIFIER
                else -> throw LineException("parser rule type \"$type\" not supported", line)
            }
        }

        fun isIgnored(type: String): Boolean {
            return type in listOf("semi", "semis")
        }
    }
}
