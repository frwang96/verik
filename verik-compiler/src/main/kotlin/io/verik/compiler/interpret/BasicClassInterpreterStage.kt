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

import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.common.ETemporaryProperty
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvBlockExpression
import io.verik.compiler.ast.element.sv.ESvCallExpression
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ESvReferenceExpression
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext

object BasicClassInterpreterStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val classInterpreterVisitor = ClassInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(classInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class ClassInterpreterVisitor(private val referenceUpdater: ReferenceUpdater) : TreeVisitor() {

        override fun visitKtBasicClass(basicClass: EKtBasicClass) {
            super.visitKtBasicClass(basicClass)
            val primaryConstructor = basicClass.primaryConstructor
            val declarations = if (primaryConstructor != null) {
                val function = interpretConstructor(primaryConstructor)
                listOf(function) + basicClass.declarations
            } else {
                basicClass.declarations
            }
            referenceUpdater.replace(
                basicClass,
                ESvBasicClass(
                    basicClass.location,
                    basicClass.name,
                    basicClass.supertype,
                    ArrayList(declarations)
                )
            )
        }

        private fun interpretConstructor(primaryConstructor: EPrimaryConstructor): ESvFunction {
            val location = primaryConstructor.location
            val type = primaryConstructor.type
            val property = ETemporaryProperty(
                location,
                type.copy(),
                ESvCallExpression(location, type.copy(), Core.Sv.F_new, null, arrayListOf(), false)
            )
            val statements = listOf(
                EPropertyStatement(location, property),
                EReturnStatement(
                    location,
                    Core.Kt.C_Nothing.toType(),
                    ESvReferenceExpression(location, type.copy(), property, null, false)
                )
            )
            val function = ESvFunction(
                location,
                "vknew",
                type.copy(),
                ESvBlockExpression(location, ArrayList(statements), false, null),
                true,
                arrayListOf()
            )
            referenceUpdater.update(primaryConstructor, function)
            return function
        }
    }
}
