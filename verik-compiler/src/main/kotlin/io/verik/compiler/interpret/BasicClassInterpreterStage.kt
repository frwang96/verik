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
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.common.ETemporaryProperty
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
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
            val declarations = ArrayList<EDeclaration>()
            basicClass.declarations.forEach {
                if (it is EKtConstructor) {
                    val interpretedConstructor = interpretConstructor(it)
                    interpretedConstructor.instantiator.parent = basicClass
                    declarations.add(interpretedConstructor.instantiator)
                    if (interpretedConstructor.initializer != null) {
                        interpretedConstructor.initializer.parent = basicClass
                        declarations.add(interpretedConstructor.initializer)
                    }
                } else {
                    declarations.add(it)
                }
            }
            referenceUpdater.replace(
                basicClass,
                ESvBasicClass(
                    basicClass.location,
                    basicClass.name,
                    basicClass.superType,
                    declarations
                )
            )
        }

        private fun interpretConstructor(constructor: EKtConstructor): InterpretedConstructor {
            val initializer = interpretInitializer(constructor)
            val instantiator = interpretInstantiator(constructor, initializer)
            return InterpretedConstructor(instantiator, initializer)
        }

        private fun interpretInitializer(constructor: EKtConstructor): ESvFunction? {
            val body = constructor.getBodyNotNull()
            if (body is EKtBlockExpression && body.statements.size == 0)
                return null

            val valueParameters = ArrayList<ESvValueParameter>()
            constructor.valueParameters.forEach {
                val valueParameter = ESvValueParameter(it.location, it.name, it.type)
                valueParameters.add(valueParameter)
                referenceUpdater.update(it, valueParameter)
            }
            return ESvFunction(
                constructor.location,
                "vkinit",
                Core.Kt.C_Unit.toType(),
                body,
                isScopeStatic = false,
                isVirtual = false,
                ArrayList(valueParameters)
            )
        }

        private fun interpretInstantiator(constructor: EKtConstructor, initializer: ESvFunction?): ESvFunction {
            val temporaryProperty = ETemporaryProperty(
                constructor.location,
                constructor.type.copy(),
                ESvCallExpression(
                    constructor.location,
                    constructor.type.copy(),
                    Core.Sv.F_new,
                    null,
                    arrayListOf(),
                    false
                )
            )
            val valueParameters = constructor.valueParameters.map {
                ESvValueParameter(it.location, it.name, it.type.copy())
            }

            val statements = ArrayList<EExpression>()
            statements.add(EPropertyStatement(constructor.location, temporaryProperty))
            if (initializer != null) {
                val valueArguments = valueParameters.map {
                    EKtReferenceExpression(it.location, it.type.copy(), it, null)
                }
                statements.add(
                    EKtCallExpression(
                        constructor.location,
                        Core.Kt.C_Unit.toType(),
                        initializer,
                        EKtReferenceExpression(constructor.location, constructor.type.copy(), temporaryProperty, null),
                        ArrayList(valueArguments),
                        arrayListOf()
                    )
                )
            }
            statements.add(
                EReturnStatement(
                    constructor.location,
                    Core.Kt.C_Nothing.toType(),
                    ESvReferenceExpression(
                        constructor.location,
                        constructor.type.copy(),
                        temporaryProperty,
                        null,
                        false
                    )
                )
            )

            val instantiator = ESvFunction(
                constructor.location,
                "vknew",
                constructor.type,
                ESvBlockExpression(constructor.location, statements, false, null),
                isScopeStatic = true,
                isVirtual = false,
                ArrayList(valueParameters)
            )
            referenceUpdater.replace(constructor, instantiator)
            return instantiator
        }
    }

    data class InterpretedConstructor(
        val instantiator: ESvFunction,
        val initializer: ESvFunction?
    )
}
