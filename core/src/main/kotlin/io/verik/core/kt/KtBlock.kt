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

package io.verik.core.kt

import io.verik.core.FileLine
import io.verik.core.FileLineException
import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType

data class KtBlock(
        val statements: List<KtStatement>,
        val fileLine: FileLine
) {

    companion object {

        operator fun invoke(block: AlRule): KtBlock {
            val statements = when (block.type) {
                AlRuleType.BLOCK -> {
                    block
                            .childAs(AlRuleType.STATEMENTS)
                            .childrenAs(AlRuleType.STATEMENT)
                            .map { KtStatement(it) }
                }
                AlRuleType.STATEMENT -> {
                    listOf(KtStatement(block))
                }
                else -> throw FileLineException("block or statement expected", block.fileLine)
            }
            return KtBlock(statements, block.fileLine)
        }
    }
}