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

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtConstructor
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object ConstructorDesugarTransformerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val constructorDesugarTransformerVisitor = ConstructorDesugarTransformerVisitor(referenceUpdater)
        projectContext.project.accept(constructorDesugarTransformerVisitor)
        referenceUpdater.flush()
    }

    private class ConstructorDesugarTransformerVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        override fun visitKtBasicClass(basicClass: EKtBasicClass) {
            super.visitKtBasicClass(basicClass)
            val primaryConstructor = basicClass.primaryConstructor
            if (primaryConstructor != null) {
                val declarations = ArrayList<EDeclaration>()
                val properties = desugarValueParameterProperties(primaryConstructor.valueParameters)
                declarations.addAll(properties)

                val constructor = EKtConstructor(primaryConstructor.location)
                constructor.init(
                    primaryConstructor.type,
                    listOf(),
                    primaryConstructor.typeParameters,
                    null
                )
                declarations.add(constructor)
                basicClass.primaryConstructor = null
                referenceUpdater.update(primaryConstructor, constructor)

                declarations.forEach { it.parent = basicClass }
                basicClass.declarations.addAll(0, declarations)
            }
        }

        private fun desugarValueParameterProperties(valueParameters: List<EKtValueParameter>): List<EKtProperty> {
            val properties = ArrayList<EKtProperty>()
            valueParameters.forEach {
                if (it.isPrimaryConstructorProperty) {
                    val property = EKtProperty(it.location, it.name)
                    property.init(it.type.copy(), null, listOf())
                    properties.add(property)
                    referenceUpdater.update(it, property)
                }
            }
            return properties
        }
    }
}
