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

package verikc.rs.pass

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.rs.RsResolveUtil

internal class RsPassRepeatTypeAliasTest {

    @Test
    fun `type alias function parameter`() {
        val fileContext = """
            @alias fun t_Byte() = t_Ubit(8)
        """.trimIndent()
        val string = "fun f(x: Byte) {}"
        Assertions.assertEquals(
            TYPE_UBIT.toTypeGenerified(8),
            RsResolveUtil.resolveFunction(fileContext, string).parameterProperties[0].typeGenerified
        )
    }

    @Test
    fun `type alias function parameter with property`() {
        val fileContext = """
            val WIDTH = 8
            @alias fun t_Byte() = t_Ubit(WIDTH)
        """.trimIndent()
        val string = "fun f(x: Byte) {}"
        Assertions.assertEquals(
            TYPE_UBIT.toTypeGenerified(8),
            RsResolveUtil.resolveFunction(fileContext, string).parameterProperties[0].typeGenerified
        )
    }
}