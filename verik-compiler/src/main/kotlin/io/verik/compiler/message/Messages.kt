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
import org.jetbrains.kotlin.lexer.KtToken
import java.nio.file.Path
import kotlin.reflect.full.memberProperties

object Messages {

    val INTERNAL_ERROR = MessageTemplate1<String>(
        Severity.ERROR,
        "$0"
    )

//  PRE CHECK  /////////////////////////////////////////////////////////////////////////////////////////////////////////

    val PACKAGE_NAME_ROOT = MessageTemplate0(
        Severity.ERROR,
        "Use of the root package is prohibited"
    )

    val PACKAGE_NAME_RESERVED = MessageTemplate1<String>(
        Severity.ERROR,
        "Package name is reserved: $0"
    )

    val PACkAGE_NOT_FOUND = MessageTemplate1<String>(
        Severity.ERROR,
        "Package not found: $0"
    )

    val ELEMENT_NOT_SUPPORTED = MessageTemplate1<String>(
        Severity.ERROR,
        "$0 not supported"
    )

    val MODIFIER_NOT_SUPPORTED = MessageTemplate1<KtToken>(
        Severity.ERROR,
        "Modifier $0 not supported"
    )

//  COMPILE  ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    val KOTLIN_COMPILE_WARNING = MessageTemplate1<String>(
        Severity.WARNING,
        "$0"
    )

    val KOTLIN_COMPILE_ERROR = MessageTemplate1<String>(
        Severity.ERROR,
        "$0"
    )

//  CAST  //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    val NAME_ILLEGAL = MessageTemplate1<String>(
        Severity.ERROR,
        "Illegal name: $0"
    )

    val CARDINAL_TYPE_EXPECTED = MessageTemplate1<Type>(
        Severity.ERROR,
        "Cardinal type expected but found: $0"
    )

    val ANNOTATION_ARGUMENT_NOT_LITERAL = MessageTemplate0(
        Severity.ERROR,
        "String literal expected for annotation argument"
    )

    val ILLEGAL_LOCAL_DECLARATION = MessageTemplate1<String>(
        Severity.ERROR,
        "Illegal local declaration: $0"
    )

    val UNIDENTIFIED_DECLARATION = MessageTemplate1<String>(
        Severity.ERROR,
        "Could not identify declaration: $0"
    )

//  PRE TRANSFORM  /////////////////////////////////////////////////////////////////////////////////////////////////////

    val BIT_CONSTANT_NOT_LITERAL = MessageTemplate0(
        Severity.ERROR,
        "Constant literal expected for bit constant"
    )

    val BIT_CONSTANT_ERROR = MessageTemplate1<String>(
        Severity.ERROR,
        "Error parsing bit constant: $0"
    )

//  SPECIALIZE  ////////////////////////////////////////////////////////////////////////////////////////////////////////

    val TYPE_NO_WIDTH = MessageTemplate1<Type>(
        Severity.ERROR,
        "Could not get width of type: $0"
    )

    val EXPRESSION_UNRESOLVED = MessageTemplate0(
        Severity.ERROR,
        "Type of expression could not be resolved"
    )

    val TYPE_ARGUMENT_UNRESOLVED = MessageTemplate0(
        Severity.ERROR,
        "Type of type argument could not be resolved"
    )

    val TYPE_PARAMETERS_ON_TOP = MessageTemplate0(
        Severity.ERROR,
        "Type parameters not permitted on top level declaration"
    )

    val NO_TOP_MODULES = MessageTemplate0(
        Severity.WARNING,
        "No top level modules found"
    )

    val CARDINAL_OUT_OF_RANGE = MessageTemplate0(
        Severity.ERROR,
        "Cardinal type out of range"
    )

    val TYPE_MISMATCH = MessageTemplate2<Type, Type>(
        Severity.ERROR,
        "Type mismatch: Expected $0 actual $1"
    )

    val EXTENSION_ERROR = MessageTemplate2<Type, Type>(
        Severity.ERROR,
        "Unable to extend from $0 to $1"
    )

    val TRUNCATION_ERROR = MessageTemplate2<Type, Type>(
        Severity.ERROR,
        "Unable to truncate from $0 to $1"
    )

//  INTERPRET  /////////////////////////////////////////////////////////////////////////////////////////////////////////

    val TOP_NOT_MODULE = MessageTemplate0(
        Severity.ERROR,
        "Top level declaration must be a module"
    )

    val CONFLICTING_ANNOTATION = MessageTemplate1<String>(
        Severity.ERROR,
        "Conflicts with annotation: $0"
    )

    val MAKE_ANNOTATION_REQUIRED = MessageTemplate0(
        Severity.ERROR,
        "Make annotation required"
    )

    val MAKE_ANNOTATION_ILLEGAL = MessageTemplate0(
        Severity.ERROR,
        "Make annotation only permitted on component instantiations"
    )

    val COMPONENT_INSTANTIATION_OUT_OF_CONTEXT = MessageTemplate0(
        Severity.ERROR,
        "Component instantiation out of context"
    )

    val FUNCTION_MISSING_BODY = MessageTemplate1<String>(
        Severity.ERROR,
        "Function missing body: $0"
    )

    val ON_EXPRESSION_EXPECTED = MessageTemplate0(
        Severity.ERROR,
        "On expression expected"
    )

    val PORT_NOT_MUTABLE = MessageTemplate1<String>(
        Severity.ERROR,
        "Port must be declared as var: $0"
    )

    val PORT_NO_DIRECTIONALITY = MessageTemplate1<String>(
        Severity.ERROR,
        "Could not determine directionality of port: $0"
    )

    val PORT_INSTANTIATION_NAME_MISMATCH = MessageTemplate1<String>(
        Severity.ERROR,
        "Port instantiation must match port name: $0"
    )

    val INPUT_PORT_NOT_CONNECTED = MessageTemplate1<String>(
        Severity.ERROR,
        "Input port not connected: $0"
    )

    val MODULE_PORT_INSTANTIATION_OUT_OF_CONTEXT = MessageTemplate0(
        Severity.ERROR,
        "Module port instantiation out of context"
    )

    val MODULE_PORT_MULTIPLE_PARENTS = MessageTemplate1<String>(
        Severity.ERROR,
        "Module port has multiple parent module interfaces: $0"
    )

//  MID TRANSFORM  /////////////////////////////////////////////////////////////////////////////////////////////////////

    val INJECTED_STATEMENT_NOT_LITERAL = MessageTemplate0(
        Severity.ERROR,
        "String literal expected for injected statement"
    )

    val CAT_INSUFFICIENT_ARGUMENTS = MessageTemplate0(
        Severity.ERROR,
        "Concatenation expects at least two arguments"
    )

    val SUBEXPRESSION_UNABLE_TO_EXTRACT = MessageTemplate0(
        Severity.ERROR,
        "Unable to extract subexpression"
    )

//  POST TRANSFORM  ////////////////////////////////////////////////////////////////////////////////////////////////////

    val EXPRESSION_OUT_OF_CONTEXT = MessageTemplate1<String>(
        Severity.ERROR,
        "Expression used out of context: $0"
    )

//  POST CHECK  ////////////////////////////////////////////////////////////////////////////////////////////////////////

    val FILE_NAME_RESERVED = MessageTemplate1<Path>(
        Severity.ERROR,
        "File name is reserved: $0"
    )

    val FILE_NAME_DUPLICATED = MessageTemplate1<Path>(
        Severity.ERROR,
        "File name is duplicated: $0"
    )

    val CARDINAL_NOT_POSITIVE = MessageTemplate1<Type>(
        Severity.ERROR,
        "Cardinal type not positive: $0"
    )

    val KEYWORD_CONFLICT_SYSTEM_VERILOG = MessageTemplate1<String>(
        Severity.ERROR,
        "Conflict with SystemVerilog reserved keyword: $0"
    )

    val NAME_REDECLARATION = MessageTemplate1<String>(
        Severity.ERROR,
        "Name has already been declared: $0"
    )

    val INVALID_STATEMENT = MessageTemplate0(
        Severity.ERROR,
        "Could not interpret expression as statement"
    )

    val OUTPUT_PORT_ILLEGAL_EXPRESSION = MessageTemplate1<String>(
        Severity.ERROR,
        "Illegal expression for output port: $0"
    )

    val OUTPUT_PORT_IMMUTABLE_PROPERTY = MessageTemplate1<String>(
        Severity.ERROR,
        "Property assigned by output port must be declared as var: $0"
    )

    init {
        Messages::class.memberProperties.forEach {
            val messageTemplate = it.get(Messages)
            if (messageTemplate is AbstractMessageTemplate)
                messageTemplate.name = it.name
        }
    }
}
