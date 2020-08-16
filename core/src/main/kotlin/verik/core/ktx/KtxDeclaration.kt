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

package verik.core.ktx

import verik.core.kt.*
import verik.core.main.Line
import verik.core.main.LineException
import verik.core.main.symbol.Symbol

sealed class KtxDeclaration(
        override val line: Int,
        open val identifier: String,
        open val symbol: Symbol
): Line {

    companion object {

        operator fun invoke(declaration: KtDeclaration): KtxDeclaration {
            return when (declaration) {
                is KtDeclarationType -> KtxDeclarationType(declaration)
                is KtDeclarationFunction -> KtxDeclarationFunction(declaration)
                is KtDeclarationBaseProperty -> KtxDeclarationBaseProperty(declaration)
                is KtDeclarationParameter -> KtxDeclarationParameter(declaration)
                is KtDeclarationEnumEntry -> KtxDeclarationEnumEntry(declaration)
            }
        }
    }
}

data class KtxDeclarationType(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val annotations: List<KtAnnotationType>,
        val parameters: List<KtxDeclarationParameter>,
        val constructorInvocation: KtxConstructorInvocation,
        val enumEntries: List<KtxDeclarationEnumEntry>?,
        val declarations: List<KtxDeclaration>
): KtxDeclaration(line, identifier, symbol) {

    companion object {

        operator fun invoke(declaration: KtDeclarationType): KtxDeclarationType {
            return KtxDeclarationType(
                    declaration.line,
                    declaration.identifier,
                    declaration.symbol,
                    declaration.annotations,
                    declaration.parameters.map { KtxDeclarationParameter(it) },
                    KtxConstructorInvocation(declaration.constructorInvocation),
                    declaration.enumEntries?.map { KtxDeclarationEnumEntry(it) },
                    declaration.declarations.map { KtxDeclaration(it) }
            )
        }
    }
}

data class KtxDeclarationFunction(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val type: Symbol,
        val annotations: List<KtAnnotationFunction>,
        val parameters: List<KtxDeclarationParameter>,
        val block: KtxBlock
): KtxDeclaration(line, identifier, symbol) {

    companion object {

        operator fun invoke(declaration: KtDeclarationFunction): KtxDeclarationFunction {
            val type = declaration.type
                    ?: throw LineException("function has not been assigned a return type", declaration)

            return KtxDeclarationFunction(
                    declaration.line,
                    declaration.identifier,
                    declaration.symbol,
                    type,
                    declaration.annotations,
                    declaration.parameters.map { KtxDeclarationParameter(it) },
                    KtxBlock(declaration.block)
            )
        }
    }
}

sealed class KtxDeclarationProperty(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        open val type: Symbol
): KtxDeclaration(line, identifier, symbol)

data class KtxDeclarationBaseProperty(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val type: Symbol,
        val annotations: List<KtAnnotationProperty>,
        val expression: KtxExpression
): KtxDeclarationProperty(line, identifier, symbol, type) {

    companion object {

        operator fun invoke(declaration: KtDeclarationBaseProperty): KtxDeclarationBaseProperty {
            val type = declaration.type
                    ?: throw LineException("parameter has not been assigned a type", declaration)

            return KtxDeclarationBaseProperty(
                    declaration.line,
                    declaration.identifier,
                    declaration.symbol,
                    type,
                    declaration.annotations,
                    KtxExpression(declaration.expression)
            )
        }
    }
}

data class KtxDeclarationParameter(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val type: Symbol,
        val expression: KtxExpression?
): KtxDeclarationProperty(line, identifier, symbol, type) {

    companion object {

        operator fun invoke(declaration: KtDeclarationParameter): KtxDeclarationParameter {
            val type = declaration.type
                    ?: throw LineException("parameter has not been assigned a type", declaration)

            return KtxDeclarationParameter(
                    declaration.line,
                    declaration.identifier,
                    declaration.symbol,
                    type,
                    declaration.expression?.let { KtxExpression(it) }
            )
        }
    }
}

data class KtxDeclarationEnumEntry(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val type: Symbol,
        val arg: KtxExpression?
): KtxDeclarationProperty(line, identifier, symbol, type) {

    companion object {

        operator fun invoke(declaration: KtDeclarationEnumEntry): KtxDeclarationEnumEntry {
            val type = declaration.type
                    ?: throw LineException("enum entry has not been assigned a type", declaration)

            return KtxDeclarationEnumEntry(
                    declaration.line,
                    declaration.identifier,
                    declaration.symbol,
                    type,
                    declaration.arg?.let { KtxExpression(it) }
            )
        }
    }
}
