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

package verikc.vk.check

import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.vk.ast.*

object VkCheckerCls {

    fun check(compilationUnit: VkCompilationUnit) {
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                file.clses.forEach { checkCls(it) }
            }
        }
    }

    private fun checkCls(cls: VkCls) {
        if (cls.setvalFunction != null) {
            if (!containsSetvalFunction(cls.instanceConstructor.block, cls.setvalFunction.symbol))
                throw LineException(
                    "init must contain setval function to initialize immutable properties",
                    cls.instanceConstructor.line
                )
            cls.methodBlocks.forEach {
                if (containsSetvalFunction(it.block, cls.setvalFunction.symbol))
                    throw LineException("method block cannot contain setval function", it.block.line)
            }
        }
    }

    private fun containsSetvalFunction(block: VkBlock, setvalFunctionSymbol: Symbol): Boolean {
        block.expressions.forEach {
            when (it) {
                is VkExpressionFunction ->
                    if (it.functionSymbol == setvalFunctionSymbol) return true
                is VkExpressionOperator ->
                    if (it.blocks.any { block -> containsSetvalFunction(block, setvalFunctionSymbol) }) return true
                else -> {}
            }
        }
        return false
    }
}