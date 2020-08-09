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

package io.verik.core.kt

import io.verik.core.Line
import io.verik.core.LineException
import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType
import io.verik.core.kt.symbol.KtSymbolType

sealed class KtDeclaration(
        override val line: Int,
        open val identifier: String,
        open val modifiers: List<KtModifier>
): Line {

    companion object {

        operator fun invoke(declaration: AlRule): KtDeclaration {
            val child = declaration.firstAsRule()
            val modifiers = child
                    .childrenAs(AlRuleType.MODIFIERS)
                    .flatMap { it.children }
                    .map { it.asRule() }
                    .mapNotNull { KtModifier(it) }
            return when (child.type) {
                AlRuleType.CLASS_DECLARATION -> KtDeclarationType(child, modifiers)
                AlRuleType.FUNCTION_DECLARATION -> KtDeclarationFunction(child, modifiers)
                AlRuleType.PROPERTY_DECLARATION -> KtDeclarationProperty(child, modifiers)
                else -> throw LineException("class or function or property expected", declaration)
            }
        }
    }
}

data class KtDeclarationType(
        override val line: Int,
        override val identifier: String,
        override val modifiers: List<KtModifier>,
        val parameters: List<KtDeclarationProperty>,
        val parentExpression: KtExpressionFunction,
        val enumEntries: List<KtDeclarationProperty>,
        val declarations: List<KtDeclaration>,
        var parentType: KtSymbolType?
): KtDeclaration(line, identifier, modifiers) {

    companion object {

        operator fun invoke(classDeclaration: AlRule, modifiers: List<KtModifier>): KtDeclarationType {
            return KtDeclarationParser.parseDeclarationType(classDeclaration, modifiers)
        }
    }
}

data class KtDeclarationFunction(
        override val line: Int,
        override val identifier: String,
        override val modifiers: List<KtModifier>,
        val parameters: List<KtDeclarationProperty>,
        val typeIdentifier: String,
        val block: KtBlock,
        var type: KtSymbolType?
): KtDeclaration(line, identifier, modifiers) {

    companion object {

        operator fun invoke(functionDeclaration: AlRule, modifiers: List<KtModifier>): KtDeclarationFunction {
            return KtDeclarationParser.parseDeclarationFunction(functionDeclaration, modifiers)
        }
    }

}

data class KtDeclarationProperty(
        override val line: Int,
        override val identifier: String,
        override val modifiers: List<KtModifier>,
        val expression: KtExpression
): KtDeclaration(line, identifier, modifiers) {

    companion object {

        operator fun invoke(propertyDeclaration: AlRule, modifiers: List<KtModifier>): KtDeclarationProperty {
            return KtDeclarationParser.parseDeclarationProperty(propertyDeclaration, modifiers)
        }
    }

}
