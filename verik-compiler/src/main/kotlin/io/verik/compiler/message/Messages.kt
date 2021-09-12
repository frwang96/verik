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

    val FILE_NAME_RESERVED = MessageTemplate1<Path>(
        Severity.ERROR,
        "File name is reserved: $0"
    )

    val FILE_LOCATION_MISMATCH = MessageTemplate0(
        Severity.ERROR,
        "Package directive does not match file location"
    )

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

    val CARDINAL_TYPE_EXPECTED = MessageTemplate0(
        Severity.ERROR,
        "Cardinal type expected"
    )

    val ANNOTATION_ARGUMENT_NOT_LITERAL = MessageTemplate0(
        Severity.ERROR,
        "String literal expected for annotation argument"
    )

//  PRE TRANSFORM  /////////////////////////////////////////////////////////////////////////////////////////////////////

    val BIT_CONSTANT_NOT_CONSTANT = MessageTemplate0(
        Severity.ERROR,
        "Constant literal expected for bit constant"
    )

//  SPECIALIZE  ////////////////////////////////////////////////////////////////////////////////////////////////////////

    val EXPRESSION_UNRESOLVED = MessageTemplate0(
        Severity.ERROR,
        "Type of expression could not be resolved"
    )

    val TYPE_ARGUMENT_UNRESOLVED = MessageTemplate0(
        Severity.ERROR,
        "Type of type argument could not be resolved"
    )

    val TYPE_MISMATCH = MessageTemplate2<Type, Type>(
        Severity.ERROR,
        "Type mismatch: Expected $0 actual $1"
    )

//  INTERPRET  /////////////////////////////////////////////////////////////////////////////////////////////////////////

    val CONFLICTING_ANNOTATION = MessageTemplate1<String>(
        Severity.ERROR,
        "Conflicts with annotation: $0"
    )

    val FUNCTION_MISSING_BODY = MessageTemplate1<String>(
        Severity.ERROR,
        "Function missing body: $0"
    )

    val ON_EXPRESSION_EXPECTED = MessageTemplate0(
        Severity.ERROR,
        "On expression expected"
    )

    val PORT_NO_DIRECTIONALITY = MessageTemplate1<String>(
        Severity.ERROR,
        "Could not determine directionality of port: $0"
    )

    val INPUT_PORT_NOT_CONNECTED = MessageTemplate1<String>(
        Severity.ERROR,
        "Input port not connected: $0"
    )

//  MID TRANSFORM  /////////////////////////////////////////////////////////////////////////////////////////////////////

    val INJECTED_EXPRESSION_NOT_LITERAL = MessageTemplate0(
        Severity.ERROR,
        "String literal expected for injected expression"
    )

//  POST TRANSFORM  ////////////////////////////////////////////////////////////////////////////////////////////////////

    val EXPRESSION_OUT_OF_CONTEXT = MessageTemplate1<String>(
        Severity.ERROR,
        "Expression used out of context: $0"
    )

//  POST CHECK  ////////////////////////////////////////////////////////////////////////////////////////////////////////

    val KEYWORD_CONFLICT_SYSTEM_VERILOG = MessageTemplate1<String>(
        Severity.ERROR,
        "Conflict with SystemVerilog reserved keyword: $0"
    )

    val NAME_REDECLARATION = MessageTemplate1<String>(
        Severity.ERROR,
        "Name has already been declared: $0"
    )

    init {
        Messages::class.memberProperties.forEach {
            val messageTemplate = it.get(Messages)
            if (messageTemplate is AbstractMessageTemplate)
                messageTemplate.name = it.name
        }
    }
}
