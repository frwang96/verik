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

import io.verik.compiler.ast.property.AnnotationEntry
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

    val UNANNOTATED_FILE = WarningMessageTemplate0(
        "File is not annotated as Verik"
    )

    val UNSUPPORTED_ELEMENT = ErrorMessageTemplate1<String>(
        "$0 not supported"
    )

    val UNSUPPORTED_MODIFIER = ErrorMessageTemplate1<KtToken>(
        "Modifier $0 not supported"
    )

    val MISSING_PACKAGE = ErrorMessageTemplate1<String>(
        "Package not found: $0"
    )

    val ILLEGAL_NAME = ErrorMessageTemplate1<String>(
        "Illegal name: $0"
    )

//  COMPILE  ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    val KOTLIN_COMPILE_WARNING = WarningMessageTemplate1<String>(
        "$0"
    )

    val KOTLIN_COMPILE_ERROR = ErrorMessageTemplate1<String>(
        "$0"
    )

//  CAST  //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    val ILLEGAL_LOCAL_DECLARATION = ErrorMessageTemplate1<String>(
        "Illegal local declaration: $0"
    )

    val UNSUPPORTED_ANNOTATION = ErrorMessageTemplate1<AnnotationEntry>(
        "Unsupported annotation: $0"
    )

    val UNSUPPORTED_DECLARATION = ErrorMessageTemplate1<String>(
        "Unsupported declaration: $0"
    )

//  PRE TRANSFORM  /////////////////////////////////////////////////////////////////////////////////////////////////////

    val ILLEGAL_BIT_CONSTANT = ErrorMessageTemplate0(
        "Constant literal expected for bit constant"
    )

    val BIT_CONSTANT_PARSE_ERROR = ErrorMessageTemplate1<String>(
        "Error parsing bit constant: $0"
    )

//  MID CHECK  /////////////////////////////////////////////////////////////////////////////////////////////////////////

    val TOP_NOT_MODULE = ErrorMessageTemplate0(
        "Top level declaration must be a module"
    )

    val TOP_PARAMETERIZED = ErrorMessageTemplate0(
        "Type parameters not permitted on top level declaration"
    )

    val CONFLICTING_ANNOTATIONS = ErrorMessageTemplate2<AnnotationEntry, AnnotationEntry>(
        "Conflicting annotations: @$0 and @$1"
    )

    val MISSING_MAKE_ANNOTATION = ErrorMessageTemplate0(
        "Make annotation required"
    )

    val ILLEGAL_MAKE_ANNOTATION = ErrorMessageTemplate0(
        "Make annotation only permitted on component instantiations"
    )

    val ILLEGAL_COMPONENT_INSTANTIATION = ErrorMessageTemplate0(
        "Component instantiation out of context"
    )

    val EXPECTED_CARDINAL_TYPE = ErrorMessageTemplate1<Type>(
        "Cardinal type expected but found: $0"
    )

//  RESOLVE  ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    val TYPE_NO_WIDTH = ErrorMessageTemplate1<Type>(
        "Could not get width of type: $0"
    )

    val UNRESOLVED_EXPRESSION = ErrorMessageTemplate0(
        "Type of expression could not be resolved"
    )

    val UNRESOLVED_TYPE_ARGUMENT = ErrorMessageTemplate0(
        "Type of type argument could not be resolved"
    )

    val CARDINAL_OUT_OF_RANGE = ErrorMessageTemplate0(
        "Cardinal type out of range"
    )

    val MISMATCHED_TYPE = ErrorMessageTemplate2<Type, Type>(
        "Type mismatch: Expected $0 actual $1"
    )

    val EXTENSION_ERROR = ErrorMessageTemplate2<Type, Type>(
        "Unable to extend from $0 to $1"
    )

    val TRUNCATION_ERROR = ErrorMessageTemplate2<Type, Type>(
        "Unable to truncate from $0 to $1"
    )

//  INTERPRET  /////////////////////////////////////////////////////////////////////////////////////////////////////////

    val SYNTHESIS_TOP_IS_OBJECT = ErrorMessageTemplate0(
        "Synthesis top must not be declared as object"
    )

    val SIMULATION_TOP_NOT_OBJECT = ErrorMessageTemplate0(
        "Simulation top must be declared as object"
    )

    val MODULE_IS_OBJECT = ErrorMessageTemplate0(
        "Module must not be declared as object"
    )

    val PORT_NOT_MUTABLE = ErrorMessageTemplate1<String>(
        "Port must be declared as var: $0"
    )

    val PORT_NO_DIRECTIONALITY = ErrorMessageTemplate1<String>(
        "Could not determine directionality of port: $0"
    )

    val EXPECTED_ON_EXPRESSION = ErrorMessageTemplate0(
        "On expression expected"
    )

    val MISMATCHED_PORT_NAME = ErrorMessageTemplate1<String>(
        "Port instantiation must match port name: $0"
    )

    val UNCONNECTED_INPUT_PORT = ErrorMessageTemplate1<String>(
        "Input port not connected: $0"
    )

    val ILLEGAL_MODULE_PORT_INSTANTIATION = ErrorMessageTemplate0(
        "Module port instantiation out of context"
    )

    val MODULE_PORT_MULTIPLE_PARENTS = ErrorMessageTemplate1<String>(
        "Module port has multiple parent module interfaces: $0"
    )

    val ILLEGAL_INJECTED_PROPERTY = ErrorMessageTemplate1<String>(
        "String literal expected for injected property: $0"
    )

    val ILLEGAL_OUTPUT_PORT_EXPRESSION = ErrorMessageTemplate1<String>(
        "Illegal expression for output port: $0"
    )

    val OUTPUT_PORT_NOT_MUTABLE = ErrorMessageTemplate1<String>(
        "Property assigned by output port must be declared as var: $0"
    )

//  MID TRANSFORM  /////////////////////////////////////////////////////////////////////////////////////////////////////

    val ILLEGAL_INJECTED_STATEMENT = ErrorMessageTemplate0(
        "String literal expected for injected statement"
    )

    val COM_ASSIGNMENT_NOT_MUTABLE = ErrorMessageTemplate0(
        "Combinational assignment must be declared as var"
    )

    val COM_ASSIGNMENT_NO_INITIALIZER = ErrorMessageTemplate0(
        "Initializer expected for combinational assignment"
    )

    val UNABLE_TO_EXTRACT = ErrorMessageTemplate0(
        "Unable to extract expression"
    )

//  REORDER  ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    val ILLEGAL_PACKAGE_DEPENDENCY = ErrorMessageTemplate1<Dependency>(
        "Illegal package dependency: $0"
    )

    val CIRCULAR_PACKAGE_DEPENDENCY = ErrorMessageTemplate1<Dependency>(
        "Circular dependency between packages: $0"
    )

    val CIRCULAR_FILE_DEPENDENCY = ErrorMessageTemplate1<Dependency>(
        "Circular dependency between files: $0"
    )

    val CIRCULAR_DECLARATION_DEPENDENCY = ErrorMessageTemplate1<Dependency>(
        "Circular dependency between declarations: $0"
    )

//  POST TRANSFORM  ////////////////////////////////////////////////////////////////////////////////////////////////////

    val EXPRESSION_OUT_OF_CONTEXT = ErrorMessageTemplate1<String>(
        "Expression used out of context: $0"
    )

//  POST CHECK  ////////////////////////////////////////////////////////////////////////////////////////////////////////

    val RESERVED_PACKAGE_NAME = ErrorMessageTemplate1<String>(
        "Package name is reserved: $0"
    )

    val RESERVED_FILE_NAME = ErrorMessageTemplate1<Path>(
        "File name is reserved: $0"
    )

    val DUPLICATED_FILE_NAME = ErrorMessageTemplate1<Path>(
        "File name is duplicated: $0"
    )

    val NEGATIVE_CARDINAL = ErrorMessageTemplate1<Type>(
        "Cardinal type is negative: $0"
    )

    val KEYWORD_CONFLICT = ErrorMessageTemplate1<String>(
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
