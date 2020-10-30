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

package verik.core.rf.extract

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.rf.RfUtil
import verik.core.sv.ast.SvExpressionFunction
import verik.core.sv.ast.SvExpressionLiteral

internal class RfExpressionExtractorStringTest {

    @Test
    fun `literal simple`() {
        val string = "\"0\""
        val expected = SvExpressionLiteral(1, "\"0\"")
        assertEquals(expected, RfUtil.extractExpression(string))
    }

    @Test
    fun `literal escaped`() {
        val string = "\"%\""
        val expected = SvExpressionLiteral(1, "\"%%\"")
        assertEquals(expected, RfUtil.extractExpression(string))
    }

    @Test
    fun `expression bool`() {
        val string = "\"\${false}\""
        val expected = SvExpressionFunction(
                1,
                null,
                "\$sformatf",
                listOf(
                        SvExpressionLiteral(1, "\"%b\""),
                        SvExpressionLiteral(1, "1'b0")
                )
        )
        assertEquals(expected, RfUtil.extractExpression(string))
    }

    @Test
    fun `expression dec`() {
        val string = "\"\${0}\""
        val expected = SvExpressionFunction(
                1,
                null,
                "\$sformatf",
                listOf(
                        SvExpressionLiteral(1, "\"%0d\""),
                        SvExpressionLiteral(1, "0")
                )
        )
        assertEquals(expected, RfUtil.extractExpression(string))
    }

    @Test
    fun `expression hex`() {
        val string = "\"0x\${0}\""
        val expected = SvExpressionFunction(
                1,
                null,
                "\$sformatf",
                listOf(
                        SvExpressionLiteral(1, "\"%h\""),
                        SvExpressionLiteral(1, "0")
                )
        )
        assertEquals(expected, RfUtil.extractExpression(string))
    }
}