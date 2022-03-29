/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.serialize.source

import io.verik.importer.ast.element.common.EElement
import io.verik.importer.ast.element.declaration.ECompanionObject
import io.verik.importer.ast.element.declaration.EEnum
import io.verik.importer.ast.element.declaration.EEnumEntry
import io.verik.importer.ast.element.declaration.EKtClass
import io.verik.importer.ast.element.declaration.EKtConstructor
import io.verik.importer.ast.element.declaration.EKtFunction
import io.verik.importer.ast.element.declaration.EKtValueParameter
import io.verik.importer.ast.element.declaration.EProperty
import io.verik.importer.ast.element.declaration.ETypeAlias
import io.verik.importer.ast.element.declaration.ETypeParameter
import io.verik.importer.common.Visitor
import io.verik.importer.message.Messages

/**
 * Visitor that traverses the AST to serialize it to a text file.
 */
class SourceSerializerVisitor(
    private val serializeContext: SerializeContext
) : Visitor() {

    override fun visitElement(element: EElement) {
        Messages.INTERNAL_ERROR.on(element, "Unable to serialize element: $element")
    }

    override fun visitKtClass(cls: EKtClass) {
        DeclarationSerializer.serializeClass(cls, serializeContext)
    }

    override fun visitCompanionObject(companionObject: ECompanionObject) {
        DeclarationSerializer.serializeCompanionObject(companionObject, serializeContext)
    }

    override fun visitEnum(enum: EEnum) {
        DeclarationSerializer.serializeEnum(enum, serializeContext)
    }

    override fun visitTypeAlias(typeAlias: ETypeAlias) {
        DeclarationSerializer.serializeTypeAlias(typeAlias, serializeContext)
    }

    override fun visitTypeParameter(typeParameter: ETypeParameter) {
        DeclarationSerializer.serializeTypeParameter(typeParameter, serializeContext)
    }

    override fun visitKtFunction(function: EKtFunction) {
        DeclarationSerializer.serializeFunction(function, serializeContext)
    }

    override fun visitKtConstructor(constructor: EKtConstructor) {
        DeclarationSerializer.serializeConstructor(constructor, serializeContext)
    }

    override fun visitProperty(property: EProperty) {
        DeclarationSerializer.serializeProperty(property, serializeContext)
    }

    override fun visitKtValueParameter(valueParameter: EKtValueParameter) {
        DeclarationSerializer.serializeValueParameter(valueParameter, serializeContext)
    }

    override fun visitEnumEntry(enumEntry: EEnumEntry) {
        DeclarationSerializer.serializeEnumEntry(enumEntry, serializeContext)
    }
}
