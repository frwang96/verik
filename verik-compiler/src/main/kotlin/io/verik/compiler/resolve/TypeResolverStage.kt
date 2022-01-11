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

import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object TypeResolverStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val specializeContext = projectContext.specializeContext!!
        var expressionReferenceForwarderEntries =
            ExpressionReferenceForwarder.getExpressionReferenceForwarderEntries(projectContext)
        var typeConstraints = TypeConstraintCollector.collect(projectContext)
        var isLoop: Boolean
        do {
            isLoop = false
            val newTypeConstraints = ArrayList<TypeConstraint>()
            if (expressionReferenceForwarderEntries.isNotEmpty()) {
                expressionReferenceForwarderEntries = expressionReferenceForwarderEntries.filter {
                    val isForwarded = ExpressionReferenceForwarder.forward(it, specializeContext)
                    if (isForwarded) {
                        newTypeConstraints.addAll(TypeConstraintCollector.collect(it.receiverExpression))
                        isLoop = true
                    }
                    !isForwarded
                }
            }
            newTypeConstraints.addAll(typeConstraints)
            if (newTypeConstraints.isNotEmpty()) {
                typeConstraints = newTypeConstraints.filter {
                    val isResolved = TypeConstraintResolver.resolve(it)
                    if (isResolved) {
                        isLoop = true
                    }
                    !isResolved
                }
            }
        } while (isLoop)
    }
}
