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

package verik.core.kt.resolve

import verik.core.kt.*
import verik.core.lang.Lang
import verik.core.lang.LangTypeClass
import verik.core.main.LineException

object KtPropertyResolver {

    fun resolveFile(file: KtFile) {
        file.declarations.forEach { resolveDeclaration(it) }
    }

    fun resolveDeclaration(declaration: KtDeclaration) {
        when (declaration) {
            is KtDeclarationType -> {
                declaration.parameters.forEach { resolveDeclaration(it) }
                declaration.declarations.forEach { resolveDeclaration(it) }
            }
            is KtDeclarationFunction -> {
                resolveFunction(declaration)
            }
            is KtDeclarationBaseProperty -> {
                resolveBaseProperty(declaration)
            }
            is KtDeclarationParameter -> {
                throw LineException("resolving parameter declarations not supported", declaration)
            }
            is KtDeclarationEnumEntry -> {
                throw LineException("resolving enum entries not supported", declaration)
            }
        }
    }

    private fun resolveFunction(function: KtDeclarationFunction) {
        function.parameters.forEach { resolveDeclaration(it) }
    }

    private fun resolveBaseProperty(baseProperty: KtDeclarationBaseProperty) {
        KtExpressionResolver.resolveExpression(baseProperty.expression)
        val type = baseProperty.expression.type
                ?: throw LineException("could not resolve expression", baseProperty.expression)
        val typeClass = Lang.typeTable.typeClass(type, baseProperty.expression.line)
        if (typeClass !in listOf(LangTypeClass.INT, LangTypeClass.TYPE)) {
            throw LineException("type expression or Int expected", baseProperty.expression)
        }
        val typeDual = Lang.typeTable.typeDual(type, baseProperty.expression.line)
        baseProperty.type = typeDual
    }
}