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

package io.verik.importer.common

import io.verik.importer.ast.sv.element.common.SvCompilationUnit
import io.verik.importer.ast.sv.element.common.SvElement
import io.verik.importer.ast.sv.element.common.SvTypedElement
import io.verik.importer.ast.sv.element.declaration.SvClass
import io.verik.importer.ast.sv.element.declaration.SvDeclaration
import io.verik.importer.ast.sv.element.declaration.SvModule
import io.verik.importer.ast.sv.element.declaration.SvPackage
import io.verik.importer.ast.sv.element.declaration.SvPort
import io.verik.importer.ast.sv.element.declaration.SvProperty
import io.verik.importer.ast.sv.element.descriptor.SvDescriptor
import io.verik.importer.ast.sv.element.descriptor.SvPackedDescriptor
import io.verik.importer.ast.sv.element.descriptor.SvSimpleDescriptor
import io.verik.importer.ast.sv.element.expression.SvExpression
import io.verik.importer.ast.sv.element.expression.SvLiteralExpression
import io.verik.importer.ast.sv.element.expression.SvNothingExpression

abstract class SvVisitor {

    open fun visitElement(element: SvElement) {}

    open fun visitCompilationUnit(compilationUnit: SvCompilationUnit) {
        visitElement(compilationUnit)
    }

    open fun visitTypedElement(typedElement: SvTypedElement) {
        visitElement(typedElement)
    }

    open fun visitDeclaration(declaration: SvDeclaration) {
        visitTypedElement(declaration)
    }

    open fun visitPackage(`package`: SvPackage) {
        visitDeclaration(`package`)
    }

// Class Like //////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitClass(`class`: SvClass) {
        visitDeclaration(`class`)
    }

    open fun visitModule(module: SvModule) {
        visitDeclaration(module)
    }

// Property Like ///////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitProperty(property: SvProperty) {
        visitDeclaration(property)
    }

    open fun visitPort(port: SvPort) {
        visitDeclaration(port)
    }

// Descriptor Like /////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitDescriptor(descriptor: SvDescriptor) {
        visitTypedElement(descriptor)
    }

    open fun visitSimpleDescriptor(simpleDescriptor: SvSimpleDescriptor) {
        visitDescriptor(simpleDescriptor)
    }

    open fun visitPackedDescriptor(packedDescriptor: SvPackedDescriptor) {
        visitDescriptor(packedDescriptor)
    }

// Expression Like /////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitExpression(expression: SvExpression) {
        visitElement(expression)
    }

    open fun visitNothingExpression(nothingExpression: SvNothingExpression) {
        visitExpression(nothingExpression)
    }

    open fun visitLiteralExpression(literalExpression: SvLiteralExpression) {
        visitExpression(literalExpression)
    }
}
