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

package verik.core.kt

import verik.core.al.AlRule
import verik.core.base.Line
import verik.core.base.SymbolIndexer
import verik.core.kt.parse.KtBlockParser

data class KtBlock(
        override val line: Int,
        val lambdaProperties: List<KtDeclarationLambdaProperty>,
        val statements: List<KtStatement>
): Line {

    companion object {

        operator fun invoke(block: AlRule, indexer: SymbolIndexer): KtBlock {
            return KtBlockParser.parse(block, indexer)
        }
    }
}