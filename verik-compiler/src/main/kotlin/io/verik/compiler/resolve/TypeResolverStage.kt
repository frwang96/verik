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

package io.verik.compiler.resolve

import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object TypeResolverStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(TypeResolverVisitor)
    }

    private object TypeResolverVisitor : TreeVisitor() {

        override fun visitKtFunction(function: EKtFunction) {
            val typeConstraints = TypeConstraintCollector.collect(function)
            TypeConstraintResolver.resolve(typeConstraints)
        }

        override fun visitKtProperty(property: EKtProperty) {
            val typeConstraints = TypeConstraintCollector.collect(property)
            TypeConstraintResolver.resolve(typeConstraints)
        }
    }
}
