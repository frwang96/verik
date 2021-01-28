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

import verikc.lang.LangSymbol.FUNCTION_TYPE_ANY
import verikc.lang.LangSymbol.FUNCTION_TYPE_ANY_ANY
import verikc.ps.ast.*

object PsPassFilterTypeFunction: PsPassBase() {

    override fun passComponent(component: PsComponent) {
        component.methodBlocks.forEach { passBlock(it.block) }
    }

    override fun passCls(cls: PsCls) {
        passBlock(cls.instanceConstructor.block)
        cls.methodBlocks.forEach { passBlock(it.block) }
    }

    private fun passBlock(block: PsBlock) {
        block.expressions.removeIf {
            it is PsExpressionFunction && it.functionSymbol in listOf(FUNCTION_TYPE_ANY, FUNCTION_TYPE_ANY_ANY)
        }
        block.expressions.forEach {
            if (it is PsExpressionOperator) {
                it.blocks.forEach { block -> passBlock(block) }
            }
        }
    }
}