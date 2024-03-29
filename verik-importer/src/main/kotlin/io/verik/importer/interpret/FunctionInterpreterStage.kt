/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.interpret

import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.declaration.EKtClass
import io.verik.importer.ast.element.declaration.EKtConstructor
import io.verik.importer.ast.element.declaration.EKtFunction
import io.verik.importer.ast.element.declaration.EKtValueParameter
import io.verik.importer.ast.element.declaration.ESvConstructor
import io.verik.importer.ast.element.declaration.ESvFunction
import io.verik.importer.ast.element.declaration.ESvValueParameter
import io.verik.importer.ast.element.declaration.ETask
import io.verik.importer.ast.element.descriptor.ESimpleDescriptor
import io.verik.importer.ast.property.AnnotationEntry
import io.verik.importer.common.ReferenceUpdater
import io.verik.importer.common.TreeVisitor
import io.verik.importer.core.Core
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

/**
 * Stage that interprets SystemVerilog function-like declarations to Kotlin functions.
 */
object FunctionInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val functionInterpreterVisitor = FunctionInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(functionInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class FunctionInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        override fun visitSvFunction(function: ESvFunction) {
            super.visitSvFunction(function)
            val valueParameters = function.valueParameters.map { interpretValueParameter(it) }
            val isOpen = isOpen(function)
            val interpretedFunction = EKtFunction(
                function.location,
                function.name,
                function.signature,
                valueParameters,
                function.descriptor,
                listOf(),
                isOpen,
                false
            )
            referenceUpdater.replace(function, interpretedFunction)
        }

        override fun visitTask(task: ETask) {
            super.visitTask(task)
            val valueParameters = task.valueParameters.map { interpretValueParameter(it) }
            val descriptor = ESimpleDescriptor(task.location, Core.C_Unit.toType())
            val annotationEntry = AnnotationEntry("Task")
            val isOpen = isOpen(task)
            val interpretedFunction = EKtFunction(
                task.location,
                task.name,
                task.signature,
                valueParameters,
                descriptor,
                listOf(annotationEntry),
                isOpen,
                false
            )
            referenceUpdater.replace(task, interpretedFunction)
        }

        override fun visitSvConstructor(constructor: ESvConstructor) {
            super.visitSvConstructor(constructor)
            val valueParameters = constructor.valueParameters.map { interpretValueParameter(it) }
            val interpretedConstructor = EKtConstructor(
                constructor.location,
                constructor.signature,
                valueParameters
            )
            referenceUpdater.replace(constructor, interpretedConstructor)
        }

        private fun interpretValueParameter(valueParameter: ESvValueParameter): EKtValueParameter {
            val interpretedValueParameter = EKtValueParameter(
                valueParameter.location,
                valueParameter.name,
                valueParameter.descriptor,
                listOf(),
                null,
                valueParameter.hasDefault
            )
            referenceUpdater.update(valueParameter, interpretedValueParameter)
            return interpretedValueParameter
        }

        private fun isOpen(declaration: EDeclaration): Boolean {
            val parent = declaration.parent
            return parent is EKtClass && parent.isOpen
        }
    }
}
