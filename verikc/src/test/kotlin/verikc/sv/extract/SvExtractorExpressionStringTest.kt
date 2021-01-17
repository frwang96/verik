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
import verikc.line
import verikc.sv.SvExtractUtil
import verikc.sv.ast.SvExpressionFunction
import verikc.sv.ast.SvExpressionLiteral

internal class SvExtractorExpressionStringTest {

    @Test
    fun `literal simple`() {
        val string = "\"0\""
        val expected = SvExpressionLiteral(line(6), "\"0\"")
        assertEquals(
            expected,
            SvExtractUtil.extractModuleActionBlockExpression("", "", string)
        )
    }

    @Test
    fun `literal escaped`() {
        val string = "\"%\""
        val expected = SvExpressionLiteral(line(6), "\"%%\"")
        assertEquals(
            expected,
            SvExtractUtil.extractModuleActionBlockExpression("", "", string)
        )
    }

    @Test
    fun `expression bool`() {
        val string = "\"\${false}\""
        val expected = SvExpressionFunction(
            line(6),
            null,
            "\$sformatf",
            listOf(
                SvExpressionLiteral(line(6), "\"%b\""),
                SvExpressionLiteral(line(6), "1'b0")
            )
        )
        assertEquals(
            expected,
            SvExtractUtil.extractModuleActionBlockExpression("", "", string)
        )
    }

    @Test
    fun `expression dec`() {
        val string = "\"\${0}\""
        val expected = SvExpressionFunction(
            line(6),
            null,
            "\$sformatf",
            listOf(
                SvExpressionLiteral(line(6), "\"%0d\""),
                SvExpressionLiteral(line(6), "0")
            )
        )
        assertEquals(
            expected,
            SvExtractUtil.extractModuleActionBlockExpression("", "", string)
        )
    }

    @Test
    fun `expression hex`() {
        val string = "\"0x\${0}\""
        val expected = SvExpressionFunction(
            line(6),
            null,
            "\$sformatf",
            listOf(
                SvExpressionLiteral(line(6), "\"%h\""),
                SvExpressionLiteral(line(6), "0")
            )
        )
        assertEquals(
            expected,
            SvExtractUtil.extractModuleActionBlockExpression("", "", string)
        )
    }

    @Test
    fun `expression time`() {
        val string = "\"\${time()}\""
        val expected = SvExpressionFunction(
            line(6),
            null,
            "\$sformatf",
            listOf(
                SvExpressionLiteral(line(6), "\"%0t\""),
                SvExpressionFunction(line(6), null, "\$time", listOf())
            )
        )
        assertEquals(
            expected,
            SvExtractUtil.extractModuleActionBlockExpression("", "", string)
        )
    }
}
