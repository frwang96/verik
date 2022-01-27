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

import io.verik.importer.ast.element.common.ECompilationUnit
import io.verik.importer.ast.element.common.EElement
import io.verik.importer.ast.element.declaration.EAbstractFunction
import io.verik.importer.ast.element.declaration.EClass
import io.verik.importer.ast.element.declaration.EConstructor
import io.verik.importer.ast.element.declaration.EContainerDeclaration
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.declaration.EEnum
import io.verik.importer.ast.element.declaration.EEnumEntry
import io.verik.importer.ast.element.declaration.EFunction
import io.verik.importer.ast.element.declaration.EModule
import io.verik.importer.ast.element.declaration.EPackage
import io.verik.importer.ast.element.declaration.EPort
import io.verik.importer.ast.element.declaration.EProperty
import io.verik.importer.ast.element.declaration.EStruct
import io.verik.importer.ast.element.declaration.EStructEntry
import io.verik.importer.ast.element.declaration.ETask
import io.verik.importer.ast.element.declaration.ETypeAlias
import io.verik.importer.ast.element.declaration.ETypeDeclaration
import io.verik.importer.ast.element.declaration.EValueParameter
import io.verik.importer.ast.element.descriptor.EBitDescriptor
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.ast.element.descriptor.EPackedDescriptor
import io.verik.importer.ast.element.descriptor.EReferenceDescriptor
import io.verik.importer.ast.element.descriptor.ESimpleDescriptor
import io.verik.importer.ast.element.expression.EExpression
import io.verik.importer.ast.element.expression.ELiteralExpression
import io.verik.importer.ast.element.expression.ENothingExpression
import io.verik.importer.ast.element.expression.EReferenceExpression

abstract class Visitor {

    open fun visitElement(element: EElement) {}

    open fun visitDeclaration(declaration: EDeclaration) {
        visitElement(declaration)
    }

    open fun visitContainerDeclaration(containerDeclaration: EContainerDeclaration) {
        visitDeclaration(containerDeclaration)
    }

    open fun visitCompilationUnit(compilationUnit: ECompilationUnit) {
        visitContainerDeclaration(compilationUnit)
    }

    open fun visitPackage(`package`: EPackage) {
        visitContainerDeclaration(`package`)
    }

// Class Like //////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitClass(`class`: EClass) {
        visitContainerDeclaration(`class`)
    }

    open fun visitModule(module: EModule) {
        visitContainerDeclaration(module)
    }

    open fun visitTypeDeclaration(typeDeclaration: ETypeDeclaration) {
        visitDeclaration(typeDeclaration)
    }

    open fun visitStruct(struct: EStruct) {
        visitTypeDeclaration(struct)
    }

    open fun visitEnum(enum: EEnum) {
        visitTypeDeclaration(enum)
    }

    open fun visitTypeAlias(typeAlias: ETypeAlias) {
        visitTypeDeclaration(typeAlias)
    }

// Function Like ///////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitAbstractFunction(abstractFunction: EAbstractFunction) {
        visitDeclaration(abstractFunction)
    }

    open fun visitFunction(function: EFunction) {
        visitAbstractFunction(function)
    }

    open fun visitTask(task: ETask) {
        visitAbstractFunction(task)
    }

    open fun visitConstructor(constructor: EConstructor) {
        visitAbstractFunction(constructor)
    }

// Property Like ///////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitProperty(property: EProperty) {
        visitDeclaration(property)
    }

    open fun visitValueParameter(valueParameter: EValueParameter) {
        visitDeclaration(valueParameter)
    }

    open fun visitPort(port: EPort) {
        visitDeclaration(port)
    }

    open fun visitStructEntry(structEntry: EStructEntry) {
        visitDeclaration(structEntry)
    }

    open fun visitEnumEntry(enumEntry: EEnumEntry) {
        visitDeclaration(enumEntry)
    }

// Descriptor Like /////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitDescriptor(descriptor: EDescriptor) {
        visitElement(descriptor)
    }

    open fun visitSimpleDescriptor(simpleDescriptor: ESimpleDescriptor) {
        visitDescriptor(simpleDescriptor)
    }

    open fun visitBitDescriptor(bitDescriptor: EBitDescriptor) {
        visitDescriptor(bitDescriptor)
    }

    open fun visitPackedDescriptor(packedDescriptor: EPackedDescriptor) {
        visitDescriptor(packedDescriptor)
    }

    open fun visitReferenceDescriptor(referenceDescriptor: EReferenceDescriptor) {
        visitDescriptor(referenceDescriptor)
    }

// Expression Like /////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitExpression(expression: EExpression) {
        visitElement(expression)
    }

    open fun visitNothingExpression(nothingExpression: ENothingExpression) {
        visitExpression(nothingExpression)
    }

    open fun visitLiteralExpression(literalExpression: ELiteralExpression) {
        visitExpression(literalExpression)
    }

    open fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
        visitExpression(referenceExpression)
    }
}
