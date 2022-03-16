/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.compiler.evaluate

import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EPropertyStatement
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.constant.ConstantBuilder
import io.verik.compiler.constant.ConstantNormalizer
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object ForEachUnrollTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val generateForBlockReferenceIndexerVisitor = GenerateForBlockReferenceIndexerVisitor()
        projectContext.project.accept(generateForBlockReferenceIndexerVisitor)

        val forEachUnrollTransformerVisitor = ForEachUnrollTransformerVisitor(
            generateForBlockReferenceIndexerVisitor.references
        )
        projectContext.project.accept(forEachUnrollTransformerVisitor)
    }

    private class GenerateForBlockReferenceIndexerVisitor : TreeVisitor() {

        val references = HashSet<EKtValueParameter>()

        private val innerReferenceIndexerVisitor = InnerReferenceIndexerVisitor()

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            if (callExpression.reference == Core.Vk.Cluster.F_get_Int) {
                callExpression.valueArguments[0].accept(innerReferenceIndexerVisitor)
            }
        }

        private inner class InnerReferenceIndexerVisitor : TreeVisitor() {

            override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
                val reference = referenceExpression.reference
                if (reference is EKtValueParameter) references.add(reference)
            }
        }
    }

    private class ForEachUnrollTransformerVisitor(
        private val references: HashSet<EKtValueParameter>
    ) : TreeVisitor() {

        private fun getIndices(expression: EExpression): Pair<Int, Int>? {
            if (expression !is ECallExpression) return null
            return when (expression.reference) {
                Core.Kt.Int.F_rangeTo_Int -> {
                    val startIndex = ConstantNormalizer.parseIntOrNull(expression.receiver!!)
                    val endIndex = ConstantNormalizer.parseIntOrNull(expression.valueArguments[0])
                    if (startIndex != null && endIndex != null) Pair(startIndex, endIndex + 1)
                    else null
                }
                Core.Kt.Ranges.F_until_Int -> {
                    val startIndex = ConstantNormalizer.parseIntOrNull(expression.receiver!!)
                    val endIndex = ConstantNormalizer.parseIntOrNull(expression.valueArguments[0])
                    if (startIndex != null && endIndex != null) Pair(startIndex, endIndex)
                    else null
                }
                else -> null
            }
        }

        private fun unroll(
            callExpression: ECallExpression,
            functionLiteralExpression: EFunctionLiteralExpression,
            indices: Pair<Int, Int>
        ) {
            if (indices.second <= indices.first) {
                Messages.INTERNAL_ERROR.on(callExpression, "Unable to unroll expression")
            }
            val statementGroups = ArrayList<ArrayList<EExpression>>()
            statementGroups.add(functionLiteralExpression.body.statements)
            for (index in (indices.first + 1) until indices.second) {
                val statements = ArrayList<EExpression>()
                functionLiteralExpression.body.statements.forEach {
                    if (it is EPropertyStatement) {
                        val initializer = it.property.initializer
                        if (initializer != null) {
                            val referenceExpression = EReferenceExpression.of(it.location, it.property)
                            val binaryExpression = EKtBinaryExpression(
                                it.location,
                                Core.Kt.C_Unit.toType(),
                                referenceExpression,
                                ExpressionCopier.deepCopy(initializer),
                                KtBinaryOperatorKind.EQ
                            )
                            statements.add(binaryExpression)
                        }
                    } else {
                        statements.add(ExpressionCopier.deepCopy(it))
                    }
                }
                statementGroups.add(statements)
            }
            val valueParameter = functionLiteralExpression.valueParameters[0].cast<EKtValueParameter>()
            statementGroups.forEachIndexed { index, statements ->
                val valueParameterSubstitutorVisitor = ValueParameterSubstitutorVisitor(
                    valueParameter,
                    index + indices.first
                )
                statements.forEach { it.acceptChildren(valueParameterSubstitutorVisitor) }
            }
            val blockExpression = EBlockExpression(
                callExpression.location,
                callExpression.location,
                Core.Kt.C_Unit.toType(),
                ArrayList(statementGroups.flatten())
            )
            callExpression.replace(blockExpression)
        }

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            if (callExpression.reference == Core.Kt.Collections.F_forEach_Function) {
                val functionLiteralExpression = callExpression.valueArguments[0].cast<EFunctionLiteralExpression>()
                if (functionLiteralExpression.valueParameters[0] in references) {
                    val indices = getIndices(callExpression.receiver!!)
                    if (indices != null) unroll(callExpression, functionLiteralExpression, indices)
                }
            }
        }

        private class ValueParameterSubstitutorVisitor(
            private val valueParameter: EKtValueParameter,
            private val index: Int
        ) : TreeVisitor() {

            override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
                super.visitReferenceExpression(referenceExpression)
                if (referenceExpression.reference == valueParameter) {
                    val constantExpression = ConstantBuilder.buildInt(referenceExpression, index)
                    referenceExpression.replace(constantExpression)
                }
            }
        }
    }
}
