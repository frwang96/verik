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

package verikc.ps.extract

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.line
import verikc.ps.PsExtractUtil
import verikc.sv.ast.SvExpressionLiteral

internal class PsExpressionExtractorLiteralTest {

    @Test
    fun `bool true`() {
        val string = "true"
        val expected = SvExpressionLiteral(line(6), "1'b1")
        assertEquals(expected, PsExtractUtil.extractExpression("", "", string))
    }

    @Test
    fun `int positive`() {
        val string = "1"
        val expected = SvExpressionLiteral(line(6), "1")
        assertEquals(expected, PsExtractUtil.extractExpression("", "", string))
    }

    @Test
    fun `ubit short`() {
        val string = "ubit(6, 0xf)"
        val expected = SvExpressionLiteral(line(6), "6'h0f")
        assertEquals(expected, PsExtractUtil.extractExpression("", "", string))
    }

    @Test
    fun `ubit long`() {
        val string = "ubit(36, 0x7fff_ffff)"
        val expected = SvExpressionLiteral(line(6), "36'h0_7fff_ffff")
        assertEquals(expected, PsExtractUtil.extractExpression("", "", string))
    }

    @Test
    fun `sbit short`() {
        val string = "sbit(8, 0x12)"
        val expected = SvExpressionLiteral(line(6), "8'sh12")
        assertEquals(expected, PsExtractUtil.extractExpression("", "", string))
    }
}
