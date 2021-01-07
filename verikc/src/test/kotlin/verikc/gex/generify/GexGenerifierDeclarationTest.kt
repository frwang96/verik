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

package verikc.gex.generify

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verikc.gex.GexGenerifyUtil
import verikc.lang.LangSymbol

internal class GexGenerifierDeclarationTest {

    @Test
    fun `function simple`() {
        val string = """
            fun f() {}
        """.trimIndent()

        Assertions.assertEquals(
            LangSymbol.TYPE_UNIT.toTypeGenerifiedInstance(),
            GexGenerifyUtil.generifyFunction("", string).returnTypeGenerified
        )
    }

    @Test
    fun `function simple expression`() {
        val fileContext = """
            fun g() {}
        """.trimIndent()
        val string = """
            g()
        """.trimIndent()

        Assertions.assertEquals(
            LangSymbol.TYPE_UNIT.toTypeGenerifiedInstance(),
            GexGenerifyUtil.generifyExpression(fileContext, string).typeGenerified
        )
    }
}