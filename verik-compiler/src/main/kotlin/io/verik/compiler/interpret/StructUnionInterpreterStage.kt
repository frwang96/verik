/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.declaration.sv.EStruct
import io.verik.compiler.ast.element.declaration.sv.EUnion
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

/**
 * Stage that interprets SystemVerilog packed structs and packed unions from Kotlin classes if applicable.
 */
object StructUnionInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val structUnionInterpreterVisitor = StructUnionInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(structUnionInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class StructUnionInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            when {
                cls.type.isSubtype(Core.Vk.C_Struct) -> interpretStruct(cls)
                cls.type.isSubtype(Core.Vk.C_Union) -> interpretUnion(cls)
            }
        }

        private fun interpretStruct(cls: EKtClass) {
            val properties = cls.primaryConstructor!!
                .valueParameters
                .map { interpretProperty(it, referenceUpdater) }
            val struct = EStruct(
                cls.location,
                cls.bodyStartLocation,
                cls.bodyEndLocation,
                cls.name,
                cls.type,
                cls.annotationEntries,
                cls.documentationLines,
                properties
            )
            referenceUpdater.replace(cls, struct)
            referenceUpdater.update(cls.primaryConstructor!!, struct)
        }

        private fun interpretUnion(cls: EKtClass) {
            val properties = cls.primaryConstructor!!
                .valueParameters
                .map { interpretProperty(it, referenceUpdater) }
            if (properties.size < 2) Messages.UNION_INSUFFICIENT_ARGUMENTS.on(cls, cls.name)
            val union = EUnion(
                cls.location,
                cls.bodyStartLocation,
                cls.bodyEndLocation,
                cls.name,
                cls.type,
                cls.annotationEntries,
                cls.documentationLines,
                properties
            )
            referenceUpdater.replace(cls, union)
            referenceUpdater.update(cls.primaryConstructor!!, union)
        }

        private fun interpretProperty(
            valueParameter: EKtValueParameter,
            referenceUpdater: ReferenceUpdater
        ): EProperty {
            val property = EProperty.named(
                location = valueParameter.location,
                name = valueParameter.name,
                type = valueParameter.type,
                initializer = null,
                isMutable = valueParameter.isMutable
            )
            referenceUpdater.update(valueParameter, property)
            return property
        }
    }
}
