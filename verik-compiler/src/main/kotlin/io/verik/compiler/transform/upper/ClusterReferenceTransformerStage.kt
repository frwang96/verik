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

package io.verik.compiler.transform.upper

import io.verik.compiler.ast.common.cast
import io.verik.compiler.ast.element.declaration.sv.ECluster
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.constant.ConstantNormalizer
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.utils.addToStdlib.cast

object ClusterReferenceTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ClusterReferenceTransformerVisitor)
    }

    private object ClusterReferenceTransformerVisitor : TreeVisitor() {

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            if (callExpression.reference == Core.Vk.Cluster.F_get_Int) {
                val cluster = callExpression.receiver
                    .cast<EReferenceExpression>()
                    .reference
                    .cast<ECluster>(callExpression)
                val index = ConstantNormalizer.parseIntOrNull(callExpression.valueArguments[0])
                if (index == null) {
                    Messages.EXPRESSION_NOT_CONSTANT.on(callExpression.valueArguments[0])
                } else if (index < 0 || index >= cluster.declarations.size) {
                    Messages.CLUSTER_INDEX_INVALID.on(callExpression.valueArguments[0], index)
                } else {
                    val referenceExpression = EReferenceExpression.of(cluster.declarations[index])
                    callExpression.replace(referenceExpression)
                }
            }
        }
    }
}
