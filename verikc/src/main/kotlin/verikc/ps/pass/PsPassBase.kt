/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.ps.pass

import verikc.base.ast.LineException
import verikc.ps.ast.*

abstract class PsPassBase {

    open fun pass(compilationUnit: PsCompilationUnit) {
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                file.declarations.forEach { passDeclaration(it) }
            }
        }
    }

    fun passExpression(block: PsBlock, replacer: (PsExpression) -> PsExpression?) {
        block.expressions.indices.forEach {
            val replacement = replacer(block.expressions[it])
            if (replacement != null) {
                block.expressions[it] = replacement
            }
            val expression = block.expressions[it]
            if (expression is PsExpressionOperator) {
                expression.blocks.forEach { block -> passExpression(block, replacer) }
            }
        }
    }

    protected open fun passModule(module: PsModule) {}

    protected open fun passEnum(enum: PsEnum) {}

    private fun passDeclaration(declaration: PsDeclaration) {
        when (declaration) {
            is PsModule -> passModule(declaration)
            is PsEnum -> passEnum(declaration)
            else -> throw LineException("declaration type not supported", declaration.line)
        }
    }
}
