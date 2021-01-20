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

package verikc.ps.pass

import verikc.base.ast.LiteralValue
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.util.LangEvaluatorUtil
import verikc.ps.ast.*

object PsPassConstantEvaluation: PsPassBase() {

    override fun passComponent(component: PsComponent) {
        component.actionBlocks.forEach { passBlock(it.block) }
        component.methodBlocks.forEach { passBlock(it.block) }
    }

    override fun passCls(cls: PsCls) {
        cls.methodBlocks.forEach { passBlock(it.block) }
    }

    private fun passBlock(block: PsBlock) {
        PsPassUtil.replaceBlock(block) {
            if (it.expression is PsExpressionFunction) replace(it.expression)
            else null
        }
    }

    private fun replace(expression: PsExpressionFunction): PsExpression? {
        val receiverEvaluated = expression.receiver?.let { evaluateLiteral(it) ?: return null }
        val argsEvaluated = expression.args.map { evaluateLiteral(it) ?: return null }

        val value = LangEvaluatorUtil.evaluate(
            expression.functionSymbol,
            receiverEvaluated,
            argsEvaluated,
            expression.line
        )

        return value?.let {
            PsExpressionLiteral(expression.line, TYPE_INT.toTypeGenerified(), LiteralValue.fromInt(value))
        }
    }

    private fun evaluateLiteral(expression: PsExpression): Int? {
        return if (expression is PsExpressionLiteral && expression.typeGenerified.typeSymbol == TYPE_INT) {
            expression.value.toInt()
        } else null
    }
}