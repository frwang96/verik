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

package io.verik.compiler.transform.mid

import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.sv.EStruct
import io.verik.compiler.ast.element.sv.EStructLiteralExpression
import io.verik.compiler.ast.property.StructLiteralEntry
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object StructLiteralTransformerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(StructLiteralTransformerVisitor)
    }

    private object StructLiteralTransformerVisitor : TreeVisitor() {

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            val reference = callExpression.reference
            if (reference is EStruct) {
                val entries = callExpression.valueArguments
                    .zip(reference.properties)
                    .map { (valueArgument, property) ->
                        StructLiteralEntry(property, valueArgument)
                    }
                val structLiteralExpression = EStructLiteralExpression(
                    callExpression.location,
                    callExpression.type.copy(),
                    entries
                )
                callExpression.replace(structLiteralExpression)
            }
        }
    }
}
