/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.declaration.sv.ECoverGroup
import io.verik.compiler.ast.element.declaration.sv.ESvConstructor
import io.verik.compiler.ast.element.declaration.sv.ESvValueParameter
import io.verik.compiler.ast.property.ValueParameterKind
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object CoverGroupInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val coverGroupInterpreterVisitor = CoverGroupInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(coverGroupInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class CoverGroupInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            if (cls.type.isSubtype(Core.Vk.C_CoverGroup)) {
                val constructor = interpretConstructor(cls.primaryConstructor!!)
                val coverGroup = ECoverGroup(
                    location = cls.location,
                    bodyStartLocation = cls.bodyStartLocation,
                    bodyEndLocation = cls.bodyEndLocation,
                    name = cls.name,
                    type = cls.type,
                    annotationEntries = cls.annotationEntries,
                    documentationLines = cls.documentationLines,
                    typeParameters = cls.typeParameters,
                    declarations = cls.declarations,
                    constructor = constructor
                )
                referenceUpdater.replace(cls, coverGroup)
            }
        }

        private fun interpretConstructor(primaryConstructor: EPrimaryConstructor): ESvConstructor {
            val valueParameters = ArrayList<ESvValueParameter>()
            primaryConstructor.valueParameters.forEach {
                val kind = if (it.hasAnnotationEntry(AnnotationEntries.IN)) {
                    ValueParameterKind.REF
                } else {
                    ValueParameterKind.INPUT
                }
                val valueParameter = ESvValueParameter(
                    location = it.location,
                    name = it.name,
                    type = it.type,
                    annotationEntries = it.annotationEntries,
                    expression = it.expression,
                    kind = kind
                )
                referenceUpdater.update(it, valueParameter)
                valueParameters.add(valueParameter)
            }
            val constructor = ESvConstructor(
                location = primaryConstructor.location,
                type = primaryConstructor.type,
                annotationEntries = primaryConstructor.annotationEntries,
                documentationLines = primaryConstructor.documentationLines,
                body = primaryConstructor.body,
                valueParameters = valueParameters
            )
            referenceUpdater.update(primaryConstructor, constructor)
            return constructor
        }
    }
}
