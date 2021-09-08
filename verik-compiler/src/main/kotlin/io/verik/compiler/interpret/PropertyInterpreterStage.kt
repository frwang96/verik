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

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.sv.EModuleInstantiation
import io.verik.compiler.ast.element.sv.EPortInstantiation
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object PropertyInterpreterStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val memberReplacer = MemberReplacer(projectContext)
        val propertyInterpreterVisitor = PropertyInterpreterVisitor(memberReplacer)
        projectContext.project.accept(propertyInterpreterVisitor)
        memberReplacer.updateReferences()
    }

    class PropertyInterpreterVisitor(private val memberReplacer: MemberReplacer) : TreeVisitor() {

        override fun visitKtProperty(property: EKtProperty) {
            super.visitKtProperty(property)
            memberReplacer.replace(property, interpret(property))
        }
    }

    private fun interpret(property: EKtProperty): EElement {
        return interpretModuleInstantiation(property)
            ?: ESvProperty(
                property.location,
                property.name,
                property.type,
                property.initializer
            )
    }

    private fun interpretModuleInstantiation(property: EKtProperty): EModuleInstantiation? {
        val callExpression = property.initializer
        if (callExpression !is EKtCallExpression)
            return null

        val primaryConstructor = callExpression.reference
        if (primaryConstructor !is EPrimaryConstructor || !primaryConstructor.type.isSubtype(Core.Vk.MODULE.toType()))
            return null

        if (primaryConstructor.valueParameters.size != callExpression.valueArguments.size) {
            Messages.INTERNAL_ERROR.on(callExpression, "Incorrect number of value arguments")
            return null
        }

        val portInstantiations = primaryConstructor.valueParameters
            .zip(callExpression.valueArguments)
            .map { interpretPortInstantiation(it.first, it.second) }
        return EModuleInstantiation(
            property.location,
            property.name,
            property.type,
            portInstantiations
        )
    }

    private fun interpretPortInstantiation(
        valueParameter: EKtValueParameter,
        expression: EExpression
    ): EPortInstantiation {
        return if (expression is EKtCallExpression && expression.reference == Core.Vk.NC) {
            EPortInstantiation(expression.location, valueParameter, null)
        } else {
            EPortInstantiation(expression.location, valueParameter, expression)
        }
    }
}
