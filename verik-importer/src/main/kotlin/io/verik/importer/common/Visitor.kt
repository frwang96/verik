/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.common

import io.verik.importer.ast.element.common.EElement
import io.verik.importer.ast.element.common.EProject
import io.verik.importer.ast.element.declaration.ECompanionObject
import io.verik.importer.ast.element.declaration.EContainerDeclaration
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.declaration.EEnum
import io.verik.importer.ast.element.declaration.EEnumEntry
import io.verik.importer.ast.element.declaration.EKtAbstractFunction
import io.verik.importer.ast.element.declaration.EKtClass
import io.verik.importer.ast.element.declaration.EKtConstructor
import io.verik.importer.ast.element.declaration.EKtFile
import io.verik.importer.ast.element.declaration.EKtFunction
import io.verik.importer.ast.element.declaration.EKtPackage
import io.verik.importer.ast.element.declaration.EKtValueParameter
import io.verik.importer.ast.element.declaration.EModule
import io.verik.importer.ast.element.declaration.EPort
import io.verik.importer.ast.element.declaration.EProperty
import io.verik.importer.ast.element.declaration.EStruct
import io.verik.importer.ast.element.declaration.EStructEntry
import io.verik.importer.ast.element.declaration.ESvAbstractFunction
import io.verik.importer.ast.element.declaration.ESvClass
import io.verik.importer.ast.element.declaration.ESvConstructor
import io.verik.importer.ast.element.declaration.ESvFunction
import io.verik.importer.ast.element.declaration.ESvPackage
import io.verik.importer.ast.element.declaration.ESvValueParameter
import io.verik.importer.ast.element.declaration.ETask
import io.verik.importer.ast.element.declaration.ETypeAlias
import io.verik.importer.ast.element.declaration.ETypeDeclaration
import io.verik.importer.ast.element.declaration.ETypeParameter
import io.verik.importer.ast.element.descriptor.EArrayDimensionDescriptor
import io.verik.importer.ast.element.descriptor.EBitDescriptor
import io.verik.importer.ast.element.descriptor.EContainerDescriptor
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.ast.element.descriptor.EIndexDimensionDescriptor
import io.verik.importer.ast.element.descriptor.ELiteralDescriptor
import io.verik.importer.ast.element.descriptor.ENothingDescriptor
import io.verik.importer.ast.element.descriptor.ERangeDimensionDescriptor
import io.verik.importer.ast.element.descriptor.EReferenceDescriptor
import io.verik.importer.ast.element.descriptor.ESimpleDescriptor
import io.verik.importer.ast.element.descriptor.ETypeArgument

abstract class Visitor {

    open fun visitElement(element: EElement) {}

    open fun visitDeclaration(declaration: EDeclaration) {
        visitElement(declaration)
    }

    open fun visitContainerDeclaration(containerDeclaration: EContainerDeclaration) {
        visitDeclaration(containerDeclaration)
    }

    open fun visitProject(project: EProject) {
        visitElement(project)
    }

    open fun visitSvPackage(pkg: ESvPackage) {
        visitContainerDeclaration(pkg)
    }

    open fun visitKtPackage(pkg: EKtPackage) {
        visitDeclaration(pkg)
    }

    open fun visitKtFile(file: EKtFile) {
        visitContainerDeclaration(file)
    }

// Class Like //////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitSvClass(cls: ESvClass) {
        visitContainerDeclaration(cls)
    }

    open fun visitKtClass(cls: EKtClass) {
        visitContainerDeclaration(cls)
    }

    open fun visitCompanionObject(companionObject: ECompanionObject) {
        visitContainerDeclaration(companionObject)
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

    open fun visitTypeParameter(typeParameter: ETypeParameter) {
        visitDeclaration(typeParameter)
    }

// Function Like ///////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitSvAbstractFunction(abstractFunction: ESvAbstractFunction) {
        visitDeclaration(abstractFunction)
    }

    open fun visitSvFunction(function: ESvFunction) {
        visitSvAbstractFunction(function)
    }

    open fun visitTask(task: ETask) {
        visitSvAbstractFunction(task)
    }

    open fun visitSvConstructor(constructor: ESvConstructor) {
        visitSvAbstractFunction(constructor)
    }

    open fun visitKtAbstractFunction(abstractFunction: EKtAbstractFunction) {
        visitDeclaration(abstractFunction)
    }

    open fun visitKtFunction(function: EKtFunction) {
        visitKtAbstractFunction(function)
    }

    open fun visitKtConstructor(constructor: EKtConstructor) {
        visitKtAbstractFunction(constructor)
    }

// Property Like ///////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitProperty(property: EProperty) {
        visitDeclaration(property)
    }

    open fun visitSvValueParameter(valueParameter: ESvValueParameter) {
        visitDeclaration(valueParameter)
    }

    open fun visitKtValueParameter(valueParameter: EKtValueParameter) {
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

    open fun visitNothingDescriptor(nothingDescriptor: ENothingDescriptor) {
        visitDescriptor(nothingDescriptor)
    }

    open fun visitSimpleDescriptor(simpleDescriptor: ESimpleDescriptor) {
        visitDescriptor(simpleDescriptor)
    }

    open fun visitLiteralDescriptor(literalDescriptor: ELiteralDescriptor) {
        visitDescriptor(literalDescriptor)
    }

    open fun visitBitDescriptor(bitDescriptor: EBitDescriptor) {
        visitDescriptor(bitDescriptor)
    }

    open fun visitReferenceDescriptor(referenceDescriptor: EReferenceDescriptor) {
        visitDescriptor(referenceDescriptor)
    }

    open fun visitContainerDescriptor(containerDescriptor: EContainerDescriptor) {
        visitDescriptor(containerDescriptor)
    }

    open fun visitArrayDimensionDescriptor(arrayDimensionDescriptor: EArrayDimensionDescriptor) {
        visitDescriptor(arrayDimensionDescriptor)
    }

    open fun visitIndexDimensionDescriptor(indexDimensionDescriptor: EIndexDimensionDescriptor) {
        visitDescriptor(indexDimensionDescriptor)
    }

    open fun visitRangeDimensionDescriptor(rangeDimensionDescriptor: ERangeDimensionDescriptor) {
        visitDescriptor(rangeDimensionDescriptor)
    }

    open fun visitTypeArgument(typeArgument: ETypeArgument) {
        visitElement(typeArgument)
    }
}
