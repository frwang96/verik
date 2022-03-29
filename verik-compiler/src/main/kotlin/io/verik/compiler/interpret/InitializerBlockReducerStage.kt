/*
 * SPDX-License-Identifier: Apache-2.0
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

/**
 * Stage that reduces Kotlin initializer blocks by folding them into constructors.
 */
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
