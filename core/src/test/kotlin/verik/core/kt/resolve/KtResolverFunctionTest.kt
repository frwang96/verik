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

package verik.core.kt.resolve

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verik.core.al.AlRuleParser
import verik.core.kt.KtBlock
import verik.core.kt.KtDeclarationFunction
import verik.core.kt.KtUtil
import verik.core.lang.LangSymbol
import verik.core.base.Symbol

internal class KtResolverFunctionTest {

    @Test
    fun `function without return type`() {
        val rule = AlRuleParser.parseDeclaration("fun f() {}")
        val function = KtUtil.resolveDeclarationFunction(rule)
        val expected = KtDeclarationFunction(
                1,
                "f",
                Symbol(1, 1, 1),
                listOf(),
                listOf(),
                "Unit",
                KtBlock(1, listOf()),
                LangSymbol.TYPE_UNIT
        )
        Assertions.assertEquals(expected, function)
    }
}