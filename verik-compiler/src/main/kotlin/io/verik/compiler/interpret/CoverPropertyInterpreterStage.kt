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

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.sv.ECoverBin
import io.verik.compiler.ast.element.declaration.sv.ECoverCross
import io.verik.compiler.ast.element.declaration.sv.ECoverPoint
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.expression.kt.EStringTemplateExpression
import io.verik.compiler.ast.property.LiteralStringEntry
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object CoverPropertyInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val coverPointInterpreterVisitor = CoverPointInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(coverPointInterpreterVisitor)
        referenceUpdater.flush()

        val coverCrossInterpreterVisitor = CoverCrossInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(coverCrossInterpreterVisitor)
        referenceUpdater.flush()
    }

    private fun interpretCoverBins(functionLiteralExpression: EFunctionLiteralExpression): List<ECoverBin> {
        val coverBins = ArrayList<ECoverBin>()
        functionLiteralExpression.body.statements.forEach {
            val coverBin = interpretCoverBin(it)
            if (coverBin != null) coverBins.add(coverBin)
        }
        return coverBins
    }

    private fun interpretCoverBin(expression: EExpression): ECoverBin? {
        if (expression !is ECallExpression) {
            Messages.COVER_BIN_EXPECTED.on(expression)
            return null
        }
        return when (expression.reference) {
            Core.Vk.CoverPoint.F_bin_String_String, Core.Vk.CoverCross.F_bin_String_String -> {
                val name = interpretCoverBinName(expression.valueArguments[0]) ?: return null
                ECoverBin(expression.location, name, expression.valueArguments[1], isIgnored = false, isArray = false)
            }
            Core.Vk.CoverPoint.F_bins_String_String, Core.Vk.CoverCross.F_bins_String_String -> {
                val name = interpretCoverBinName(expression.valueArguments[0]) ?: return null
                ECoverBin(expression.location, name, expression.valueArguments[1], isIgnored = false, isArray = true)
            }
            Core.Vk.CoverPoint.F_ignoreBin_String_String, Core.Vk.CoverCross.F_ignoreBin_String_String -> {
                val name = interpretCoverBinName(expression.valueArguments[0]) ?: return null
                ECoverBin(expression.location, name, expression.valueArguments[1], isIgnored = true, isArray = false)
            }
            else -> {
                Messages.COVER_BIN_EXPECTED.on(expression)
                null
            }
        }
    }

    private fun interpretCoverBinName(expression: EExpression): String? {
        if (expression is EStringTemplateExpression && expression.entries.size == 1) {
            val entry = expression.entries[0]
            if (entry is LiteralStringEntry) {
                return entry.text
            }
        }
        Messages.COVER_BIN_NAME_EXPECTED.on(expression)
        return null
    }

    private class CoverPointInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        private fun interpretCoverPoint(property: EProperty, initializer: EExpression) {
            if (initializer is ECallExpression) {
                when (initializer.reference) {
                    Core.Vk.F_cp_Any -> {
                        val coverPoint = ECoverPoint(
                            property.location,
                            property.endLocation,
                            property.name,
                            property.annotationEntries,
                            property.documentationLines,
                            initializer.valueArguments[0],
                            listOf()
                        )
                        referenceUpdater.replace(property, coverPoint)
                    }
                    Core.Vk.F_cp_Any_Function -> {
                        val coverBins = interpretCoverBins(initializer.valueArguments[1].cast())
                        val coverPoint = ECoverPoint(
                            property.location,
                            property.endLocation,
                            property.name,
                            property.annotationEntries,
                            property.documentationLines,
                            initializer.valueArguments[0],
                            coverBins
                        )
                        referenceUpdater.replace(property, coverPoint)
                    }
                }
            }
        }

        override fun visitProperty(property: EProperty) {
            super.visitProperty(property)
            val initializer = property.initializer
            if (property.hasAnnotationEntry(AnnotationEntries.Cover) && initializer != null) {
                if (property.type.reference == Core.Vk.C_CoverPoint) {
                    interpretCoverPoint(property, initializer)
                }
            }
        }
    }

    private class CoverCrossInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        private fun interpretCoverCross(property: EProperty, initializer: EExpression) {
            if (initializer is ECallExpression) {
                when (initializer.reference) {
                    Core.Vk.F_cc_Any -> {
                        if (initializer.valueArguments.size < 2) {
                            Messages.COVER_CROSS_INSUFFICIENT_ARGUMENTS.on(initializer)
                        }
                        val coverPoints = initializer.valueArguments.mapNotNull { getCoverPoint(it) }
                        val coverCross = ECoverCross(
                            property.location,
                            property.endLocation,
                            property.name,
                            property.annotationEntries,
                            property.documentationLines,
                            coverPoints,
                            listOf()
                        )
                        referenceUpdater.replace(property, coverCross)
                    }
                    Core.Vk.F_cc_Any_Function -> {
                        val valueArguments = initializer.valueArguments.dropLast(1)
                        if (valueArguments.size < 2) {
                            Messages.COVER_CROSS_INSUFFICIENT_ARGUMENTS.on(initializer)
                        }
                        val coverPoints = valueArguments.mapNotNull { getCoverPoint(it) }
                        val coverBins = interpretCoverBins(initializer.valueArguments.last().cast())
                        val coverCross = ECoverCross(
                            property.location,
                            property.endLocation,
                            property.name,
                            property.annotationEntries,
                            property.documentationLines,
                            coverPoints,
                            coverBins
                        )
                        referenceUpdater.replace(property, coverCross)
                    }
                }
            }
        }

        private fun getCoverPoint(expression: EExpression): ECoverPoint? {
            if (expression is EReferenceExpression) {
                val reference = expression.reference
                if (reference is ECoverPoint) return reference
            }
            Messages.COVER_POINT_EXPECTED.on(expression)
            return null
        }

        override fun visitProperty(property: EProperty) {
            super.visitProperty(property)
            val initializer = property.initializer
            if (property.hasAnnotationEntry(AnnotationEntries.Cover) && initializer != null) {
                if (property.type.reference == Core.Vk.C_CoverCross) {
                    interpretCoverCross(property, initializer)
                }
            }
        }
    }
}
