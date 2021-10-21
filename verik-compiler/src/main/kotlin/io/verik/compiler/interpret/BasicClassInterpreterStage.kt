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

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.common.ETemporaryProperty
import io.verik.compiler.ast.element.common.EThisExpression
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtConstructor
import io.verik.compiler.ast.element.kt.EKtReferenceExpression
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvBlockExpression
import io.verik.compiler.ast.element.sv.ESvCallExpression
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ESvReferenceExpression
import io.verik.compiler.ast.element.sv.ESvValueParameter
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
            referenceUpdater.replace(
                basicClass,
                ESvBasicClass(
                    basicClass.location,
                    basicClass.name,
                    basicClass.supertype,
                    basicClass.declarations
                )
            )
        }

        override fun visitKtConstructor(constructor: EKtConstructor) {
            super.visitKtConstructor(constructor)
            val location = constructor.location
            val type = constructor.type
            val temporaryProperty = ETemporaryProperty(
                location,
                type.copy(),
                ESvCallExpression(location, type.copy(), Core.Sv.F_new, null, arrayListOf(), false)
            )

            val statements = ArrayList<EExpression>()
            statements.add(EPropertyStatement(location, temporaryProperty))

            val receiverReplacerVisitor = ReceiverReplacerVisitor(temporaryProperty)
            val body = constructor.getBodyNotNull()
            body.accept(receiverReplacerVisitor)
            if (body is EKtBlockExpression) statements.addAll(body.statements)
            else statements.add(body)

            statements.add(
                EReturnStatement(
                    location,
                    Core.Kt.C_Nothing.toType(),
                    ESvReferenceExpression(location, type.copy(), temporaryProperty, null, false)
                )
            )
            val valueParameters = constructor.valueParameters.map {
                ESvValueParameter(it.location, it.name, it.type)
            }
            val function = ESvFunction(
                location,
                "vknew",
                type,
                ESvBlockExpression(location, ArrayList(statements), false, null),
                true,
                ArrayList(valueParameters)
            )
            referenceUpdater.replace(constructor, function)
        }
    }

    private class ReceiverReplacerVisitor(private val temporaryProperty: ETemporaryProperty) : TreeVisitor() {

        override fun visitThisExpression(thisExpression: EThisExpression) {
            super.visitThisExpression(thisExpression)
            val referenceExpression = EKtReferenceExpression(
                thisExpression.location,
                thisExpression.type,
                temporaryProperty,
                null
            )
            thisExpression.replace(referenceExpression)
        }
    }
}
