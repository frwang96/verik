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

package verikc.kt.parse

import verikc.al.ast.AlRule
import verikc.al.ast.AlTree
import verikc.base.ast.AnnotationFunction
import verikc.base.ast.LineException
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.KtDeclaration

object KtParserDeclaration {

    fun parse(declaration: AlTree, topIdentifier: String?, symbolContext: SymbolContext): KtDeclaration {
        val child = declaration.unwrap()
        return when (child.index) {
            AlRule.CLASS_DECLARATION, AlRule.OBJECT_DECLARATION ->
                KtParserType.parse(child, topIdentifier, symbolContext)
            AlRule.FUNCTION_DECLARATION -> {
                val annotations = if (child.contains(AlRule.MODIFIERS)) {
                    KtParserAnnotation.parseAnnotationsFunction(child.find(AlRule.MODIFIERS))
                } else listOf()

                if (annotations.none { it == AnnotationFunction.ALIAS }) {
                    KtParserFunction.parse(child, annotations, symbolContext)
                } else {
                    if (annotations.size != 1)
                        throw LineException("illegal type alias declaration", child.line)
                    KtParserTypeAlias.parse(child, symbolContext)
                }
            }
            AlRule.PROPERTY_DECLARATION -> KtParserProperty.parse(child, symbolContext)
            else -> throw LineException("class or function or property declaration expected", child.line)
        }
    }
}