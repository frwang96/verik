/*
 * Copyright 2020 Francis Wang
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
                file.declarations.forEach { updateDeclaration(it) }
            }
        }
    }

    fun updateStatement(block: PsBlock, pass: (PsStatement) -> List<PsStatement>?) {
        var head = 0
        var tail = 0
        while (head < block.statements.size) {
            val substitute = pass(block.statements[head])
            if (substitute != null) {
                block.statements.removeAt(head)
                block.statements.addAll(head, substitute)
                head += substitute.size
            } else {
                head++
            }
            while (tail < head) {
                val statement = block.statements[tail]
                if (statement is PsStatementExpression) {
                    val expression = statement.expression
                    if (expression is PsExpressionOperator) {
                        expression.blocks.forEach { updateStatement(it, pass) }
                    }
                }
                tail++
            }
        }
    }

    protected open fun updateModule(module: PsModule) {}

    protected open fun updateEnum(enum: PsEnum) {}

    private fun updateDeclaration(declaration: PsDeclaration) {
        when (declaration) {
            is PsModule -> updateModule(declaration)
            is PsEnum -> updateEnum(declaration)
            else -> throw LineException("declaration type not supported", declaration.line)
        }
    }
}
