/*
 * Copyright (c) 2021 Francis Wang
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

package io.verik.compiler.message

import io.verik.compiler.ast.property.Type
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.reorder.Dependency
import org.jetbrains.kotlin.lexer.KtToken
import java.nio.file.Path
import kotlin.reflect.full.declaredMemberProperties

object Messages {

    val INTERNAL_ERROR = FatalMessageTemplate1<String>(
        "Internal error: $0"
    )

    val NORMALIZATION_ERROR = FatalMessageTemplate2<ProjectStage, String>(
        "Normalization error at $0: $1"
    )

//  PRE CHECK  /////////////////////////////////////////////////////////////////////////////////////////////////////////

    val PACKAGE_NOT_FOUND = ErrorMessageTemplate1<String>(
        "Package not found: $0"
    )

    val ELEMENT_NOT_SUPPORTED = ErrorMessageTemplate1<String>(
        "$0 not supported"
    )

    val MODIFIER_NOT_SUPPORTED = ErrorMessageTemplate1<KtToken>(
        "Modifier $0 not supported"
    )

//  COMPILE  ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    val KOTLIN_COMPILE_WARNING = WarningMessageTemplate1<String>(
        "$0"
    )

    val KOTLIN_COMPILE_ERROR = ErrorMessageTemplate1<String>(
        "$0"
    )

//  CAST  //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    val NAME_ILLEGAL = ErrorMessageTemplate1<String>(
        "Illegal name: $0"
    )

    val CARDINAL_TYPE_EXPECTED = ErrorMessageTemplate1<Type>(
        "Cardinal type expected but found: $0"
    )

    val ILLEGAL_LOCAL_DECLARATION = ErrorMessageTemplate1<String>(
        "Illegal local declaration: $0"
    )

    val UNSUPPORTED_ANNOTATION = ErrorMessageTemplate1<String>(
        "Unsupported annotation: $0"
    )

    val UNSUPPORTED_DECLARATION = ErrorMessageTemplate1<String>(
        "Unsupported declaration: $0"
    )

//  PRE TRANSFORM  /////////////////////////////////////////////////////////////////////////////////////////////////////

    val BIT_CONSTANT_NOT_LITERAL = ErrorMessageTemplate0(
        "Constant literal expected for bit constant"
    )

    val BIT_CONSTANT_ERROR = ErrorMessageTemplate1<String>(
        "Error parsing bit constant: $0"
    )

//  SPECIALIZE  ////////////////////////////////////////////////////////////////////////////////////////////////////////

    val TYPE_NO_WIDTH = ErrorMessageTemplate1<Type>(
        "Could not get width of type: $0"
    )

    val EXPRESSION_UNRESOLVED = ErrorMessageTemplate0(
        "Type of expression could not be resolved"
    )

    val TYPE_ARGUMENT_UNRESOLVED = ErrorMessageTemplate0(
        "Type of type argument could not be resolved"
    )

    val TYPE_PARAMETERS_ON_TOP = ErrorMessageTemplate0(
        "Type parameters not permitted on top level declaration"
    )

    val CARDINAL_OUT_OF_RANGE = ErrorMessageTemplate0(
        "Cardinal type out of range"
    )

    val TYPE_MISMATCH = ErrorMessageTemplate2<Type, Type>(
        "Type mismatch: Expected $0 actual $1"
    )

    val EXTENSION_ERROR = ErrorMessageTemplate2<Type, Type>(
        "Unable to extend from $0 to $1"
    )

    val TRUNCATION_ERROR = ErrorMessageTemplate2<Type, Type>(
        "Unable to truncate from $0 to $1"
    )

//  INTERPRET  /////////////////////////////////////////////////////////////////////////////////////////////////////////

    val TOP_NOT_MODULE = ErrorMessageTemplate0(
        "Top level declaration must be a module"
    )

    val CONFLICTING_ANNOTATION = ErrorMessageTemplate1<String>(
        "Conflicts with annotation: $0"
    )

    val MAKE_ANNOTATION_REQUIRED = ErrorMessageTemplate0(
        "Make annotation required"
    )

    val MAKE_ANNOTATION_ILLEGAL = ErrorMessageTemplate0(
        "Make annotation only permitted on component instantiations"
    )

    val COMPONENT_INSTANTIATION_OUT_OF_CONTEXT = ErrorMessageTemplate0(
        "Component instantiation out of context"
    )

    val FUNCTION_MISSING_BODY = ErrorMessageTemplate1<String>(
        "Function missing body: $0"
    )

    val ON_EXPRESSION_EXPECTED = ErrorMessageTemplate0(
        "On expression expected"
    )

    val PORT_NOT_MUTABLE = ErrorMessageTemplate1<String>(
        "Port must be declared as var: $0"
    )

    val PORT_NO_DIRECTIONALITY = ErrorMessageTemplate1<String>(
        "Could not determine directionality of port: $0"
    )

    val PORT_INSTANTIATION_NAME_MISMATCH = ErrorMessageTemplate1<String>(
        "Port instantiation must match port name: $0"
    )

    val INPUT_PORT_NOT_CONNECTED = ErrorMessageTemplate1<String>(
        "Input port not connected: $0"
    )

    val MODULE_PORT_INSTANTIATION_OUT_OF_CONTEXT = ErrorMessageTemplate0(
        "Module port instantiation out of context"
    )

    val MODULE_PORT_MULTIPLE_PARENTS = ErrorMessageTemplate1<String>(
        "Module port has multiple parent module interfaces: $0"
    )

    val INJECTED_PROPERTY_NOT_LITERAL = ErrorMessageTemplate1<String>(
        "String literal expected for injected property: $0"
    )

    val OUTPUT_PORT_ILLEGAL_EXPRESSION = ErrorMessageTemplate1<String>(
        "Illegal expression for output port: $0"
    )

    val OUTPUT_PORT_IMMUTABLE_PROPERTY = ErrorMessageTemplate1<String>(
        "Property assigned by output port must be declared as var: $0"
    )

//  MID TRANSFORM  /////////////////////////////////////////////////////////////////////////////////////////////////////

    val INJECTED_STATEMENT_NOT_LITERAL = ErrorMessageTemplate0(
        "String literal expected for injected statement"
    )

    val COM_ASSIGNMENT_NOT_MUTABLE = ErrorMessageTemplate0(
        "Combinational assignment must be declared as var"
    )

    val COM_ASSIGNMENT_NO_INITIALIZER = ErrorMessageTemplate0(
        "Initializer expected for combinational assignment"
    )

    val SUBEXPRESSION_UNABLE_TO_EXTRACT = ErrorMessageTemplate0(
        "Unable to extract subexpression"
    )

//  REORDER  ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    val PACKAGE_DEPENDENCY_ILLEGAL = ErrorMessageTemplate1<Dependency>(
        "Illegal package dependency: $0"
    )

    val DECLARATION_CIRCULAR_DEPENDENCY = ErrorMessageTemplate1<Dependency>(
        "Circular dependency between declarations: $0"
    )

//  POST TRANSFORM  ////////////////////////////////////////////////////////////////////////////////////////////////////

    val EXPRESSION_OUT_OF_CONTEXT = ErrorMessageTemplate1<String>(
        "Expression used out of context: $0"
    )

//  POST CHECK  ////////////////////////////////////////////////////////////////////////////////////////////////////////

    val PACKAGE_NAME_RESERVED = ErrorMessageTemplate1<String>(
        "Package name is reserved: $0"
    )

    val FILE_NAME_RESERVED = ErrorMessageTemplate1<Path>(
        "File name is reserved: $0"
    )

    val FILE_NAME_DUPLICATED = ErrorMessageTemplate1<Path>(
        "File name is duplicated: $0"
    )

    val CARDINAL_NOT_POSITIVE = ErrorMessageTemplate1<Type>(
        "Cardinal type not positive: $0"
    )

    val KEYWORD_CONFLICT_SYSTEM_VERILOG = ErrorMessageTemplate1<String>(
        "Conflict with SystemVerilog reserved keyword: $0"
    )

    val NAME_REDECLARATION = ErrorMessageTemplate1<String>(
        "Name has already been declared: $0"
    )

    val INVALID_STATEMENT = ErrorMessageTemplate0(
        "Could not interpret expression as statement"
    )

    init {
        Messages::class.declaredMemberProperties.forEach {
            val messageTemplate = it.get(Messages)
            if (messageTemplate is AbstractMessageTemplate)
                messageTemplate.name = it.name
        }
    }
}
