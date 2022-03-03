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

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.reorder.Dependency
import org.jetbrains.kotlin.lexer.KtToken
import java.nio.file.Path
import kotlin.reflect.full.declaredMemberProperties

object Messages {

    val INTERNAL_ERROR = FatalMessageTemplate1<String>(
        "Internal error: $0"
    )

    val FILE_READ_ERROR = FatalMessageTemplate1<Path>(
        "Unable to read file: $0"
    )

    val FILE_WRITE_ERROR = FatalMessageTemplate1<Path>(
        "Unable to write file: $0"
    )

    val NORMALIZATION_ERROR = FatalMessageTemplate2<ProjectStage, String>(
        "Normalization error at $0: $1"
    )

//  PRE CHECK  /////////////////////////////////////////////////////////////////////////////////////////////////////////

    val INVALID_TIMESCALE = FatalMessageTemplate1<String>(
        "Invalid timescale: $0"
    )

    val UNANNOTATED_FILE = WarningMessageTemplate0(
        "File is not annotated as Verik"
    )

    val UNSUPPORTED_ELEMENT = ErrorMessageTemplate1<String>(
        "$0 not supported"
    )

    val UNSUPPORTED_MODIFIER = ErrorMessageTemplate1<KtToken>(
        "Modifier $0 not supported"
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

    val BIT_CONSTANT_INSUFFICIENT_WIDTH = ErrorMessageTemplate1<String>(
        "Bit constant is insufficiently wide: $0"
    )

//  MID CHECK  /////////////////////////////////////////////////////////////////////////////////////////////////////////

    val ENTRY_POINT_NOT_FOUND = ErrorMessageTemplate1<String>(
        "Entry point not found: $0"
    )

    val SUPER_TYPE_MISSING = ErrorMessageTemplate1<String>(
        "Supertype is required: $0"
    )

    val CONFLICTING_ANNOTATIONS = ErrorMessageTemplate2<AnnotationEntry, AnnotationEntry>(
        "Conflicting annotations: @$0 and @$1"
    )

    val INVALID_ENTRY_POINT = ErrorMessageTemplate0(
        "Invalid entry point"
    )

    val ENTRY_POINT_PARAMETERIZED = ErrorMessageTemplate0(
        "Type parameters not permitted on entry points"
    )

    val PROCEDURAL_ASSIGNMENT_NOT_MUTABLE = ErrorMessageTemplate2<String, String>(
        "$0 assignment must be declared as var: $1"
    )

    val FUNCTION_IS_TASK = ErrorMessageTemplate1<String>(
        "Function should be annotated with @Task: $0"
    )

    val FUNCTION_NOT_TASK = ErrorMessageTemplate1<String>(
        "Function should not be annotated with @Task: $0"
    )

    val MISSING_MAKE_ANNOTATION = ErrorMessageTemplate0(
        "Make annotation required"
    )

    val ILLEGAL_MAKE_ANNOTATION = ErrorMessageTemplate0(
        "Make annotation only permitted on component instantiations"
    )

    val MAKE_OUT_OF_CONTEXT = ErrorMessageTemplate0(
        "Make annotation out of context"
    )

    val PARAMETERIZED_FUNCTION_NOT_TOP = ErrorMessageTemplate1<String>(
        "Function that is not top level cannot be parameterized: $0"
    )

    val EXPECTED_CARDINAL_TYPE = ErrorMessageTemplate1<Type>(
        "Cardinal type expected but found: $0"
    )

    val MULTIPLE_CONSTRUCTORS = ErrorMessageTemplate0(
        "Multiple constructors are not permitted"
    )

    val EXPECTED_NOT_OBJECT = ErrorMessageTemplate2<String, String>(
        "$0 must not be declared as object: $1"
    )

    val PORT_NOT_MUTABLE = ErrorMessageTemplate2<String, String>(
        "$0 port must be declared as var: $1"
    )

    val PORT_MUTABLE = ErrorMessageTemplate2<String, String>(
        "$0 port must be declared as val: $1"
    )

    val PORT_NO_DIRECTIONALITY = ErrorMessageTemplate1<String>(
        "Could not determine directionality of port: $0"
    )

    val INPUT_PORT_NOT_CONNECTED = ErrorMessageTemplate1<String>(
        "Input port not connected: $0"
    )

    val INPUT_PORT_NOT_CONSTANT = ErrorMessageTemplate1<String>(
        "Constant expression expected for input port declared as val: $0"
    )

    val OUTPUT_PORT_NOT_MUTABLE = ErrorMessageTemplate1<String>(
        "Property assigned by output port must be declared as var: $0"
    )

    val ILLEGAL_OUTPUT_PORT_EXPRESSION = ErrorMessageTemplate0(
        "Illegal expression for output port"
    )

    val PROCEDURAL_BLOCK_ILLEGAL_REFERENCE = ErrorMessageTemplate1<String>(
        "Illegal reference to procedural block: $0"
    )

    val VAL_REASSIGNED = ErrorMessageTemplate1<String>(
        "Property declared val cannot be reassigned: $0"
    )

//  SPECIALIZE  ////////////////////////////////////////////////////////////////////////////////////////////////////////

    val TYPE_NO_WIDTH = ErrorMessageTemplate1<Type>(
        "Could not get width of type: $0"
    )

    val CARDINAL_NOT_BOOLEAN = ErrorMessageTemplate1<Type>(
        "Could not interpret cardinal as either true or false: $0"
    )

    val CARDINAL_OUT_OF_RANGE = ErrorMessageTemplate0(
        "Cardinal type out of range"
    )

    val OPTIONAL_NOT_DIRECT_ASSIGNMENT = ErrorMessageTemplate0(
        "Optional must be directly assigned to a property"
    )

    val OPTIONAL_NOT_VAL = ErrorMessageTemplate0(
        "Property assigned as optional must be declared as val"
    )

//  RESOLVE  ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    val INDETERMINATE_SLICE_WIDTH = ErrorMessageTemplate0(
        "Unable to determine width of slice"
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

    val UNRESOLVED_DECLARATION = ErrorMessageTemplate1<String>(
        "Type of declaration could not be resolved: $0"
    )

    val UNRESOLVED_EXPRESSION = ErrorMessageTemplate0(
        "Type of expression could not be resolved"
    )

    val UNRESOLVED_TYPE_ARGUMENT = ErrorMessageTemplate0(
        "Type argument should be specified explicitly"
    )

    val CARDINAL_NEGATIVE = ErrorMessageTemplate1<Type>(
        "Cardinal type is negative: $0"
    )

//  INTERPRET  /////////////////////////////////////////////////////////////////////////////////////////////////////////

    val ENUM_PROPERTY_ILLEGAL = ErrorMessageTemplate1<String>(
        "Illegal enum property: $0"
    )

    val ENUM_PROPERTY_ILLEGAL_TYPE = ErrorMessageTemplate1<Type>(
        "Illegal type for enum property: $0"
    )

    val EXPECTED_ON_EXPRESSION = ErrorMessageTemplate0(
        "Expected on expression"
    )

    val COVER_CROSS_INSUFFICIENT_ARGUMENTS = ErrorMessageTemplate0(
        "Cover cross should cross at least two cover points"
    )

    val COVER_POINT_EXPECTED = ErrorMessageTemplate0(
        "Cover point expected"
    )

    val MISMATCHED_PORT_NAME = ErrorMessageTemplate1<String>(
        "Port instantiation must match port name: $0"
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

    val NOT_INJECTED_PROPERTY = ErrorMessageTemplate0(
        "Only injected properties are permitted here"
    )

    val INJECTED_PROPERTY_NOT_LITERAL = ErrorMessageTemplate0(
        "Expression not permitted in injected property"
    )

//  UPPER TRANSFORM  ///////////////////////////////////////////////////////////////////////////////////////////////////

    val ILLEGAL_INJECTED_EXPRESSION = ErrorMessageTemplate0(
        "String literal expected for injected expression"
    )

    val COM_ASSIGNMENT_NO_INITIALIZER = ErrorMessageTemplate0(
        "Expected initializer for combinational assignment"
    )

    val SEQ_ASSIGNMENT_NO_ONR_EXPRESSION = ErrorMessageTemplate0(
        "Expected onr expression with return value for sequential assignment"
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

    val DUPLICATED_FILE_NAME = ErrorMessageTemplate1<Path>(
        "File name is duplicated: $0"
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
