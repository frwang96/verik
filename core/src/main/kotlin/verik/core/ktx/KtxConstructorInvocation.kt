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

package verik.core.ktx

import verik.core.kt.KtConstructorInvocation
import verik.core.main.Line
import verik.core.main.LineException
import verik.core.main.symbol.Symbol

data class KtxConstructorInvocation(
        override val line: Int,
        val type: Symbol,
        val args: List<KtxExpression>
): Line {

    companion object {

        operator fun invoke(constructorInvocation: KtConstructorInvocation): KtxConstructorInvocation {
            val type = constructorInvocation.type
                    ?: throw LineException("constructor invocation has not been assigned a type", constructorInvocation)

            return KtxConstructorInvocation(
                    constructorInvocation.line,
                    type,
                    constructorInvocation.args.map { KtxExpression(it) }
            )
        }
    }
}