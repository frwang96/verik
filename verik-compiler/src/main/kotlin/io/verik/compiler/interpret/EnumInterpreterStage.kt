/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.ast.common.ResizableDeclarationContainer
import io.verik.compiler.ast.element.declaration.common.EEnumEntry
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.sv.EEnum
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object EnumInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val enumInterpreterVisitor = EnumInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(enumInterpreterVisitor)
        val enumEntryCollectorVisitor = EnumEntryCollectorVisitor()
        projectContext.project.accept(enumEntryCollectorVisitor)
        enumEntryCollectorVisitor.enumEntries.forEach {
            val parent = it.parent
            if (parent is ResizableDeclarationContainer) {
                parent.insertChild(it)
            } else {
                Messages.INTERNAL_ERROR.on(it, "Count not insert $it into $parent")
            }
        }
        referenceUpdater.flush()
    }

    private class EnumInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        private fun interpretEnum(cls: EKtClass): EEnum {
            val property = interpretEnumProperty(cls)
            val enumEntries = cls.declarations.map { it.cast<EEnumEntry>() }
            val enum = EEnum(
                cls.location,
                cls.bodyStartLocation,
                cls.bodyEndLocation,
                cls.name,
                cls.type,
                cls.annotationEntries,
                cls.documentationLines,
                property,
                enumEntries
            )
            referenceUpdater.replace(cls, enum)
            return enum
        }

        private fun interpretEnumProperty(cls: EKtClass): EProperty? {
            val primaryConstructor = cls.primaryConstructor ?: return null
            val valueParameters = primaryConstructor.valueParameters
            val valueParameter = when (valueParameters.size) {
                0 -> return null
                1 -> valueParameters[0]
                else -> {
                    valueParameters.drop(1).forEach {
                        Messages.ENUM_PROPERTY_ILLEGAL.on(it, it.name)
                    }
                    return null
                }
            }
            if (valueParameter.expression != null) {
                Messages.ENUM_PROPERTY_ILLEGAL.on(valueParameter, valueParameter.name)
            }
            if (valueParameter.type.reference !in listOf(Core.Kt.C_Int, Core.Vk.C_Ubit)) {
                Messages.ENUM_PROPERTY_ILLEGAL_TYPE.on(valueParameter, valueParameter.type)
            }
            val property = EProperty.named(
                valueParameter.location,
                valueParameter.name,
                valueParameter.type,
                null,
                false
            )
            referenceUpdater.update(valueParameter, property)
            return property
        }

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            if (cls.isEnum) interpretEnum(cls)
        }
    }

    private class EnumEntryCollectorVisitor : TreeVisitor() {

        val enumEntries = ArrayList<EEnumEntry>()

        override fun visitEnum(enum: EEnum) {
            enum.enumEntries.forEach {
                it.parent = enum.parent
                enumEntries.add(it)
            }
        }
    }
}
