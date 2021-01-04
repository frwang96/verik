/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.sv.extract

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.ast.MethodBlockType
import verikc.line
import verikc.sv.SvExtractUtil
import verikc.sv.ast.SvBlock
import verikc.sv.ast.SvExpressionFunction
import verikc.sv.ast.SvMethodBlock
import verikc.sv.ast.SvTypeExtracted

internal class SvExtractorMethodBlockTest {

    @Test
    fun `function simple`() {
        val string = """
            fun f() {}
        """.trimIndent()
        val expected = SvMethodBlock(
            line(5),
            "f",
            MethodBlockType.FUNCTION,
            listOf(),
            SvTypeExtracted("void", "", ""),
            SvBlock(line(5), listOf(), listOf())
        )
        assertEquals(expected, SvExtractUtil.extractMethodBlock("", "", string))
    }

    @Test
    fun `function simple expression`() {
        val moduleContext = """
            fun g() {}
        """.trimIndent()
        val string = """
            g()
        """.trimIndent()
        val expected = SvExpressionFunction(
            line(6),
            null,
            "g",
            listOf()
        )
        assertEquals(expected, SvExtractUtil.extractExpression("", moduleContext, string))
    }
}
