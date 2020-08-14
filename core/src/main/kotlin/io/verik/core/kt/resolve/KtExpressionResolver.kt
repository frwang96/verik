/*
 * Copyright 2020 Francis Wang
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

package io.verik.core.kt.resolve

import io.verik.core.kt.*
import io.verik.core.lang.Lang
import io.verik.core.lang.LangFunctionMatchMultiple
import io.verik.core.lang.LangFunctionMatchNone
import io.verik.core.lang.LangFunctionMatchSingle
import io.verik.core.lang.LangSymbol.TYPE_BOOL
import io.verik.core.lang.LangSymbol.TYPE_INT
import io.verik.core.main.LineException

object KtExpressionResolver {

    fun resolveFile(file: KtFile) {
        file.declarations.map { resolveDeclaration(it) }
    }

    fun resolveDeclaration(declaration: KtDeclaration) {
        when (declaration) {
            is KtDeclarationType -> {
                declaration.declarations.forEach { resolveDeclaration(it) }
            }
            is KtDeclarationFunction -> {
                declaration.block.statements.forEach { resolveExpression(it.expression) }
            }
            is KtDeclarationBaseProperty -> {
                resolveExpression(declaration.expression)
            }
            is KtDeclarationParameter -> {
                throw LineException("resolving parameter declarations not supported", declaration)
            }
            is KtDeclarationEnumEntry -> {
                throw LineException("resolving enum entries not supported", declaration)
            }
        }
    }

    fun resolveExpression(expression: KtExpression) {
        when (expression) {
            is KtExpressionFunction -> resolveFunction(expression)
            is KtExpressionOperator -> throw LineException("resolving operator expressions is not supported", expression)
            is KtExpressionProperty -> throw LineException("resolving property expressions is not supported", expression)
            is KtExpressionString -> throw LineException("resolving string expressions is not supported", expression)
            is KtExpressionLiteral -> resolveLiteral(expression)
        }
        if (expression.type == null) {
            throw LineException("could not resolve expression", expression)
        }
    }

    private fun resolveFunction(expression: KtExpressionFunction) {
        expression.args.forEach { resolveExpression(it) }
        val argTypes = expression.args.map { it.type!! }
        when (val match = Lang.functionTable.match(expression.identifier, argTypes)) {
            LangFunctionMatchNone -> {
                throw LineException("no matches for function ${expression.identifier}", expression)
            }
            LangFunctionMatchMultiple -> {
                throw LineException("multiple matches for function ${expression.identifier}", expression)
            }
            is LangFunctionMatchSingle -> {
                expression.function = match.symbol
                expression.type = match.returnType
            }
        }
    }

    private fun resolveLiteral(expression: KtExpressionLiteral) {
        expression.type = when (expression.value) {
            "true", "false" -> TYPE_BOOL
            else -> TYPE_INT
        }
    }
}