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

package io.verik.importer.transform.pre

import io.verik.importer.ast.common.Type
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.ast.element.expression.EExpression
import io.verik.importer.common.TreeVisitor
import io.verik.importer.core.Cardinal
import io.verik.importer.core.Core
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

object CardinalTypeSimplifierStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(CardinalTypeSimplifierVisitor)
    }

    object CardinalTypeSimplifierVisitor : TreeVisitor() {

        private fun simplify(type: Type) {
            type.arguments.forEach { simplify(it) }
            when (type.reference) {
                Core.T_ADD -> {
                    val leftValue = type.arguments[0].asCardinalValueOrNull()
                    val rightValue = type.arguments[1].asCardinalValueOrNull()
                    if (leftValue != null && rightValue != null) {
                        type.reference = Cardinal.of(leftValue + rightValue)
                        type.arguments = ArrayList()
                    }
                }
                Core.T_SUB -> {
                    val leftValue = type.arguments[0].asCardinalValueOrNull()
                    val rightValue = type.arguments[1].asCardinalValueOrNull()
                    if (leftValue != null && rightValue != null) {
                        type.reference = Cardinal.of(leftValue - rightValue)
                        type.arguments = ArrayList()
                    }
                }
            }
        }

        override fun visitDescriptor(descriptor: EDescriptor) {
            super.visitDescriptor(descriptor)
            simplify(descriptor.type)
        }

        override fun visitExpression(expression: EExpression) {
            super.visitExpression(expression)
            simplify(expression.type)
        }
    }
}
