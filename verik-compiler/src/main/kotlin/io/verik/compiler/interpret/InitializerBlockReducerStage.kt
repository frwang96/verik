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

import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.kt.EInitializerBlock
import io.verik.compiler.ast.element.declaration.sv.ESvClass
import io.verik.compiler.ast.element.declaration.sv.ESvConstructor
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull

object InitializerBlockReducerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(InitializerBlockReducerVisitor)
    }

    private object InitializerBlockReducerVisitor : TreeVisitor() {

        override fun visitSvClass(cls: ESvClass) {
            super.visitSvClass(cls)
            val declarations = ArrayList<EDeclaration>()
            val statements = ArrayList<EExpression>()
            cls.declarations.forEach {
                if (it is EInitializerBlock) {
                    statements.addAll(it.body.statements)
                } else {
                    declarations.add(it)
                }
            }
            cls.declarations = declarations

            if (statements.isNotEmpty()) {
                val constructor = declarations.firstIsInstanceOrNull<ESvConstructor>()
                if (constructor != null) {
                    statements.forEach { it.parent = constructor.body }
                    constructor.body.statements.addAll(statements)
                } else {
                    Messages.INTERNAL_ERROR.on(cls, "Unable to reduce initializer blocks")
                }
            }
        }
    }
}
