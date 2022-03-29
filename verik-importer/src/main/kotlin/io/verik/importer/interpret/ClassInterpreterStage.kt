/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.interpret

import io.verik.importer.ast.element.declaration.EKtClass
import io.verik.importer.ast.element.declaration.EKtValueParameter
import io.verik.importer.ast.element.declaration.EModule
import io.verik.importer.ast.element.declaration.EStruct
import io.verik.importer.ast.element.declaration.ESvClass
import io.verik.importer.ast.element.descriptor.ESimpleDescriptor
import io.verik.importer.common.ReferenceUpdater
import io.verik.importer.common.TreeVisitor
import io.verik.importer.core.Core
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

/**
 * Stage that interprets SystemVerilog class-like declarations to Kotlin classes.
 */
object ClassInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val classInterpreterVisitor = ClassInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(classInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class ClassInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        override fun visitSvClass(cls: ESvClass) {
            super.visitSvClass(cls)
            val interpretedClass = EKtClass(
                cls.location,
                cls.name,
                cls.signature,
                cls.declarations,
                cls.typeParameters,
                listOf(),
                cls.superDescriptor,
                true
            )
            referenceUpdater.replace(cls, interpretedClass)
        }

        override fun visitModule(module: EModule) {
            super.visitModule(module)
            val valueParameters = module.ports.map {
                val annotationEntry = it.portType.getAnnotationEntry()
                val valueParameter = EKtValueParameter(
                    location = it.location,
                    name = it.name,
                    descriptor = it.descriptor,
                    annotationEntries = listOf(annotationEntry),
                    isMutable = true,
                    hasDefault = false
                )
                referenceUpdater.update(it, valueParameter)
                valueParameter
            }
            val superDescriptor = ESimpleDescriptor(module.location, Core.C_Module.toType())
            val interpretedClass = EKtClass(
                module.location,
                module.name,
                module.signature,
                module.declarations,
                module.typeParameters,
                valueParameters,
                superDescriptor,
                false
            )
            referenceUpdater.replace(module, interpretedClass)
        }

        override fun visitStruct(struct: EStruct) {
            super.visitStruct(struct)
            val valueParameters = struct.entries.map {
                val valueParameter = EKtValueParameter(
                    location = it.location,
                    name = it.name,
                    descriptor = it.descriptor,
                    annotationEntries = listOf(),
                    isMutable = true,
                    hasDefault = false
                )
                referenceUpdater.update(it, valueParameter)
                valueParameter
            }
            val superDescriptor = ESimpleDescriptor(struct.location, Core.C_Struct.toType())
            val interpretedClass = EKtClass(
                struct.location,
                struct.name,
                struct.signature,
                ArrayList(),
                listOf(),
                valueParameters,
                superDescriptor,
                false
            )
            referenceUpdater.replace(struct, interpretedClass)
        }
    }
}
