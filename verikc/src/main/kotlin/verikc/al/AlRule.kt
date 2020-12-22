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

import verikc.antlr.KotlinParser

object AlRule {

    val PACKAGE_HEADER: Int
    val IMPORT_LIST: Int
    val IMPORT_HEADER: Int
    val TOP_LEVEL_OBJECT: Int
    val DECLARATION: Int
    val CLASS_DECLARATION: Int
    val PRIMARY_CONSTRUCTOR: Int
    val CLASS_BODY: Int
    val CLASS_PARAMETERS: Int
    val CLASS_PARAMETER: Int
    val DELEGATION_SPECIFIERS: Int
    val DELEGATION_SPECIFIER: Int
    val CONSTRUCTOR_INVOCATION: Int
    val ANNOTATED_DELEGATION_SPECIFIER: Int
    val TYPE_PARAMETERS: Int
    val TYPE_PARAMETER: Int
    val CLASS_MEMBER_DECLARATIONS: Int
    val CLASS_MEMBER_DECLARATION: Int
    val COMPANION_OBJECT: Int
    val FUNCTION_VALUE_PARAMETERS: Int
    val FUNCTION_VALUE_PARAMETER: Int
    val FUNCTION_DECLARATION: Int
    val FUNCTION_BODY: Int
    val VARIABLE_DECLARATION: Int
    val PROPERTY_DECLARATION: Int
    val PARAMETER: Int
    val OBJECT_DECLARATION: Int
    val ENUM_CLASS_BODY: Int
    val ENUM_ENTRIES: Int
    val ENUM_ENTRY: Int
    val TYPE: Int
    val TYPE_REFERENCE: Int
    val USER_TYPE: Int
    val SIMPLE_USER_TYPE: Int
    val TYPE_PROJECTION: Int
    val PARENTHESIZED_TYPE: Int
    val STATEMENTS: Int
    val STATEMENT: Int
    val CONTROL_STRUCTURE_BODY: Int
    val BLOCK: Int
    val LOOP_STATEMENT: Int
    val FOR_STATEMENT: Int
    val WHILE_STATEMENT: Int
    val DO_WHILE_STATEMENT: Int
    val ASSIGNMENT: Int
    val EXPRESSION: Int
    val DISJUNCTION: Int
    val CONJUNCTION: Int
    val EQUALITY: Int
    val COMPARISON: Int
    val INFIX_OPERATION: Int
    val ELVIS_EXPRESSION: Int
    val INFIX_FUNCTION_CALL: Int
    val RANGE_EXPRESSION: Int
    val ADDITIVE_EXPRESSION: Int
    val MULTIPLICATIVE_EXPRESSION: Int
    val AS_EXPRESSION: Int
    val COMPARISON_WITH_LITERAL_RIGHT_SIDE: Int
    val PREFIX_UNARY_EXPRESSION: Int
    val UNARY_PREFIX: Int
    val POSTFIX_UNARY_EXPRESSION: Int
    val POSTFIX_UNARY_SUFFIX: Int
    val DIRECTLY_ASSIGNABLE_EXPRESSION: Int
    val PARENTHESIZED_DIRECTLY_ASSIGNABLE_EXPRESSION: Int
    val ASSIGNABLE_EXPRESSION: Int
    val PARENTHESIZED_ASSIGNABLE_EXPRESSION: Int
    val ASSIGNABLE_SUFFIX: Int
    val INDEXING_SUFFIX: Int
    val NAVIGATION_SUFFIX: Int
    val CALL_SUFFIX: Int
    val ANNOTATED_LAMBDA: Int
    val TYPE_ARGUMENTS: Int
    val VALUE_ARGUMENTS: Int
    val VALUE_ARGUMENT: Int
    val PRIMARY_EXPRESSION: Int
    val PARENTHESIZED_EXPRESSION: Int
    val LITERAL_CONSTANT: Int
    val STRING_LITERAL: Int
    val LINE_STRING_LITERAL: Int
    val LINE_STRING_CONTENT: Int
    val LINE_STRING_EXPRESSION: Int
    val LAMBDA_LITERAL: Int
    val LAMBDA_PARAMETERS: Int
    val LAMBDA_PARAMETER: Int
    val FUNCTION_LITERAL: Int
    val THIS_EXPRESSION: Int
    val SUPER_EXPRESSION: Int
    val IF_EXPRESSION: Int
    val WHEN_SUBJECT: Int
    val WHEN_EXPRESSION: Int
    val WHEN_ENTRY: Int
    val WHEN_CONDITION: Int
    val RANGE_TEST: Int
    val TYPE_TEST: Int
    val JUMP_EXPRESSION: Int
    val ASSIGNMENT_AND_OPERATOR: Int
    val EQUALITY_OPERATOR: Int
    val COMPARISON_OPERATOR: Int
    val IN_OPERATOR: Int
    val IS_OPERATOR: Int
    val ADDITIVE_OPERATOR: Int
    val MULTIPLICATIVE_OPERATOR: Int
    val AS_OPERATOR: Int
    val PREFIX_UNARY_OPERATOR: Int
    val POSTFIX_UNARY_OPERATOR: Int
    val EXCL: Int
    val MODIFIERS: Int
    val ANNOTATION: Int
    val SINGLE_ANNOTATION: Int
    val UNESCAPED_ANNOTATION: Int
    val SIMPLE_IDENTIFIER: Int
    val IDENTIFIER: Int

    fun index(index: Int): Int {
        return index
    }

    init {
        val ruleMap = RuleMap()

        PACKAGE_HEADER = ruleMap.index("packageHeader")
        IMPORT_LIST = ruleMap.index("importList")
        IMPORT_HEADER = ruleMap.index("importHeader")
        TOP_LEVEL_OBJECT = ruleMap.index("topLevelObject")
        DECLARATION = ruleMap.index("declaration")
        CLASS_DECLARATION = ruleMap.index("classDeclaration")
        PRIMARY_CONSTRUCTOR = ruleMap.index("primaryConstructor")
        CLASS_BODY = ruleMap.index("classBody")
        CLASS_PARAMETERS = ruleMap.index("classParameters")
        CLASS_PARAMETER = ruleMap.index("classParameter")
        DELEGATION_SPECIFIERS = ruleMap.index("delegationSpecifiers")
        DELEGATION_SPECIFIER = ruleMap.index("delegationSpecifier")
        CONSTRUCTOR_INVOCATION = ruleMap.index("constructorInvocation")
        ANNOTATED_DELEGATION_SPECIFIER = ruleMap.index("annotatedDelegationSpecifier")
        TYPE_PARAMETERS = ruleMap.index("typeParameters")
        TYPE_PARAMETER = ruleMap.index("typeParameter")
        CLASS_MEMBER_DECLARATIONS = ruleMap.index("classMemberDeclarations")
        CLASS_MEMBER_DECLARATION = ruleMap.index("classMemberDeclaration")
        COMPANION_OBJECT = ruleMap.index("companionObject")
        FUNCTION_VALUE_PARAMETERS = ruleMap.index("functionValueParameters")
        FUNCTION_VALUE_PARAMETER = ruleMap.index("functionValueParameter")
        FUNCTION_DECLARATION = ruleMap.index("functionDeclaration")
        FUNCTION_BODY = ruleMap.index("functionBody")
        VARIABLE_DECLARATION = ruleMap.index("variableDeclaration")
        PROPERTY_DECLARATION = ruleMap.index("propertyDeclaration")
        PARAMETER = ruleMap.index("parameter")
        OBJECT_DECLARATION = ruleMap.index("objectDeclaration")
        ENUM_CLASS_BODY = ruleMap.index("enumClassBody")
        ENUM_ENTRIES = ruleMap.index("enumEntries")
        ENUM_ENTRY = ruleMap.index("enumEntry")
        TYPE = ruleMap.index("type")
        TYPE_REFERENCE = ruleMap.index("typeReference")
        USER_TYPE = ruleMap.index("userType")
        SIMPLE_USER_TYPE = ruleMap.index("simpleUserType")
        TYPE_PROJECTION = ruleMap.index("typeProjection")
        PARENTHESIZED_TYPE = ruleMap.index("parenthesizedType")
        STATEMENTS = ruleMap.index("statements")
        STATEMENT = ruleMap.index("statement")
        CONTROL_STRUCTURE_BODY = ruleMap.index("controlStructureBody")
        BLOCK = ruleMap.index("block")
        LOOP_STATEMENT = ruleMap.index("loopStatement")
        FOR_STATEMENT = ruleMap.index("forStatement")
        WHILE_STATEMENT = ruleMap.index("whileStatement")
        DO_WHILE_STATEMENT = ruleMap.index("doWhileStatement")
        ASSIGNMENT = ruleMap.index("assignment")
        EXPRESSION = ruleMap.index("expression")
        DISJUNCTION = ruleMap.index("disjunction")
        CONJUNCTION = ruleMap.index("conjunction")
        EQUALITY = ruleMap.index("equality")
        COMPARISON = ruleMap.index("comparison")
        INFIX_OPERATION = ruleMap.index("infixOperation")
        ELVIS_EXPRESSION = ruleMap.index("elvisExpression")
        INFIX_FUNCTION_CALL = ruleMap.index("infixFunctionCall")
        RANGE_EXPRESSION = ruleMap.index("rangeExpression")
        ADDITIVE_EXPRESSION = ruleMap.index("additiveExpression")
        MULTIPLICATIVE_EXPRESSION = ruleMap.index("multiplicativeExpression")
        AS_EXPRESSION = ruleMap.index("asExpression")
        COMPARISON_WITH_LITERAL_RIGHT_SIDE = ruleMap.index("comparisonWithLiteralRightSide")
        PREFIX_UNARY_EXPRESSION = ruleMap.index("prefixUnaryExpression")
        UNARY_PREFIX = ruleMap.index("unaryPrefix")
        POSTFIX_UNARY_EXPRESSION = ruleMap.index("postfixUnaryExpression")
        POSTFIX_UNARY_SUFFIX = ruleMap.index("postfixUnarySuffix")
        DIRECTLY_ASSIGNABLE_EXPRESSION = ruleMap.index("directlyAssignableExpression")
        PARENTHESIZED_DIRECTLY_ASSIGNABLE_EXPRESSION = ruleMap.index("parenthesizedDirectlyAssignableExpression")
        ASSIGNABLE_EXPRESSION = ruleMap.index("assignableExpression")
        PARENTHESIZED_ASSIGNABLE_EXPRESSION = ruleMap.index("parenthesizedAssignableExpression")
        ASSIGNABLE_SUFFIX = ruleMap.index("assignableSuffix")
        INDEXING_SUFFIX = ruleMap.index("indexingSuffix")
        NAVIGATION_SUFFIX = ruleMap.index("navigationSuffix")
        CALL_SUFFIX = ruleMap.index("callSuffix")
        ANNOTATED_LAMBDA = ruleMap.index("annotatedLambda")
        TYPE_ARGUMENTS = ruleMap.index("typeArguments")
        VALUE_ARGUMENTS = ruleMap.index("valueArguments")
        VALUE_ARGUMENT = ruleMap.index("valueArgument")
        PRIMARY_EXPRESSION = ruleMap.index("primaryExpression")
        PARENTHESIZED_EXPRESSION = ruleMap.index("parenthesizedExpression")
        LITERAL_CONSTANT = ruleMap.index("literalConstant")
        STRING_LITERAL = ruleMap.index("stringLiteral")
        LINE_STRING_LITERAL = ruleMap.index("lineStringLiteral")
        LINE_STRING_CONTENT = ruleMap.index("lineStringContent")
        LINE_STRING_EXPRESSION = ruleMap.index("lineStringExpression")
        LAMBDA_LITERAL = ruleMap.index("lambdaLiteral")
        LAMBDA_PARAMETERS = ruleMap.index("lambdaParameters")
        LAMBDA_PARAMETER = ruleMap.index("lambdaParameter")
        FUNCTION_LITERAL = ruleMap.index("functionLiteral")
        THIS_EXPRESSION = ruleMap.index("thisExpression")
        SUPER_EXPRESSION = ruleMap.index("superExpression")
        IF_EXPRESSION = ruleMap.index("ifExpression")
        WHEN_SUBJECT = ruleMap.index("whenSubject")
        WHEN_EXPRESSION = ruleMap.index("whenExpression")
        WHEN_ENTRY = ruleMap.index("whenEntry")
        WHEN_CONDITION = ruleMap.index("whenCondition")
        RANGE_TEST = ruleMap.index("rangeTest")
        TYPE_TEST = ruleMap.index("typeTest")
        JUMP_EXPRESSION = ruleMap.index("jumpExpression")
        ASSIGNMENT_AND_OPERATOR = ruleMap.index("assignmentAndOperator")
        EQUALITY_OPERATOR = ruleMap.index("equalityOperator")
        COMPARISON_OPERATOR = ruleMap.index("comparisonOperator")
        IN_OPERATOR = ruleMap.index("inOperator")
        IS_OPERATOR = ruleMap.index("isOperator")
        ADDITIVE_OPERATOR = ruleMap.index("additiveOperator")
        MULTIPLICATIVE_OPERATOR = ruleMap.index("multiplicativeOperator")
        AS_OPERATOR = ruleMap.index("asOperator")
        PREFIX_UNARY_OPERATOR = ruleMap.index("prefixUnaryOperator")
        POSTFIX_UNARY_OPERATOR = ruleMap.index("postfixUnaryOperator")
        EXCL = ruleMap.index("excl")
        MODIFIERS = ruleMap.index("modifiers")
        ANNOTATION = ruleMap.index("annotation")
        SINGLE_ANNOTATION = ruleMap.index("singleAnnotation")
        UNESCAPED_ANNOTATION = ruleMap.index("unescapedAnnotation")
        SIMPLE_IDENTIFIER = ruleMap.index("simpleIdentifier")
        IDENTIFIER = ruleMap.index("identifier")
    }

    private class RuleMap {

        val parser = KotlinParser(null)

        fun index(string: String): Int {
            return parser.ruleIndexMap[string]!!
        }
    }
}