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

package io.verik.compiler.transform.mid

import io.verik.compiler.ast.element.common.EAbstractClass
import io.verik.compiler.ast.element.common.EAbstractFunction
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.common.ETemporaryProperty
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtReferenceExpression
import io.verik.compiler.message.Messages

class SubexpressionExtractor {

    private val entries = ArrayList<SubexpressionExtractorEntry>()

    fun extract(
        expression: EExpression,
        temporaryProperty: ETemporaryProperty,
        initializerExpressions: List<EExpression>
    ) {
        entries.add(SubexpressionExtractorEntry(expression, temporaryProperty, initializerExpressions))
    }

    fun flush() {
        entries.forEach { flushEntry(it) }
    }

    private fun flushEntry(entry: SubexpressionExtractorEntry) {
        var blockExpression = entry.expression.parent
        var blockExpressionChild: EElement = entry.expression
        while (true) {
            when (blockExpression) {
                is EKtBlockExpression -> {
                    val blockExpressionIndex = blockExpression.statements.indexOf(blockExpressionChild)
                    if (blockExpressionIndex != -1)
                        flushEntry(blockExpression, blockExpressionIndex, entry)
                    else
                        Messages.SUBEXPRESSION_UNABLE_TO_EXTRACT.on(entry.expression)
                    return
                }
                is EAbstractFunction, is EAbstractClass, is EFile, null -> {
                    Messages.SUBEXPRESSION_UNABLE_TO_EXTRACT.on(entry.expression)
                    return
                }
                else -> {
                    blockExpressionChild = blockExpression
                    blockExpression = blockExpression.parent
                }
            }
        }
    }

    private fun flushEntry(
        blockExpression: EKtBlockExpression,
        blockExpressionIndex: Int,
        subexpressionExtractorEntry: SubexpressionExtractorEntry
    ) {
        subexpressionExtractorEntry.initializerExpressions.forEach { it.parent = blockExpression }
        blockExpression.statements.addAll(blockExpressionIndex, subexpressionExtractorEntry.initializerExpressions)

        val referenceExpression = EKtReferenceExpression(
            subexpressionExtractorEntry.expression.location,
            subexpressionExtractorEntry.temporaryProperty.type.copy(),
            subexpressionExtractorEntry.temporaryProperty,
            null
        )
        subexpressionExtractorEntry.expression.replace(referenceExpression)
    }

    private data class SubexpressionExtractorEntry(
        val expression: EExpression,
        val temporaryProperty: ETemporaryProperty,
        val initializerExpressions: List<EExpression>
    )
}
