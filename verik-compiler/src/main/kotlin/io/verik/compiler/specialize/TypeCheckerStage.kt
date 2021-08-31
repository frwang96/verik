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

package io.verik.compiler.specialize

import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object TypeCheckerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(TypeCheckerVisitor)
    }

    object TypeCheckerVisitor : TreeVisitor() {

        override fun visitKtFunction(function: EKtFunction) {
            super.visitKtFunction(function)
            val body = function.body
            if (body != null) {
                val typeConstraints = TypeConstraintCollector.collect(body)
                TypeConstraintChecker.check(typeConstraints)
            }
        }

        override fun visitKtProperty(property: EKtProperty) {
            super.visitKtProperty(property)
            val typeConstraints = TypeConstraintCollector.collect(property)
            TypeConstraintChecker.check(typeConstraints)
        }
    }
}
