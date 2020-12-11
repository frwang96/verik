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
import verikc.base.ast.Line
import verikc.base.ast.LiteralValue
import verikc.base.ast.ReifiedType
import verikc.base.ast.TypeClass.INSTANCE
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.ps.ast.PsExpressionLiteral
import verikc.sv.ast.SvExpressionLiteral

internal class PsExpressionExtractorLiteralTest {

    @Test
    fun `bool true`() {
        val literal = PsExpressionLiteral(
                Line(0),
                ReifiedType(TYPE_BOOL, INSTANCE, listOf()),
                LiteralValue.fromBoolean(true)
        )
        val expected = SvExpressionLiteral(Line(0), "1'b1")
        assertEquals(expected, PsExpressionExtractorLiteral.extract(literal))
    }

    @Test
    fun `int positive`() {
        val literal = PsExpressionLiteral(
                Line(0),
                ReifiedType(TYPE_INT, INSTANCE, listOf()),
                LiteralValue.fromInt(1)
        )
        val expected = SvExpressionLiteral(Line(0), "1")
        assertEquals(expected, PsExpressionExtractorLiteral.extract(literal))
    }

    @Test
    fun `int negative`() {
        val literal = PsExpressionLiteral(
                Line(0),
                ReifiedType(TYPE_INT, INSTANCE, listOf()),
                LiteralValue.fromInt(-1)
        )
        val expected = SvExpressionLiteral(Line(0), "-1")
        assertEquals(expected, PsExpressionExtractorLiteral.extract(literal))
    }

    @Test
    fun `ubit short`() {
        val literal = PsExpressionLiteral(
                Line(0),
                ReifiedType(TYPE_UBIT, INSTANCE, listOf(6)),
                LiteralValue.fromInt(0xf)
        )
        val expected = SvExpressionLiteral(Line(0), "6'h0f")
        assertEquals(expected, PsExpressionExtractorLiteral.extract(literal))
    }

    @Test
    fun `ubit long`() {
        val literal = PsExpressionLiteral(
                Line(0),
                ReifiedType(TYPE_UBIT, INSTANCE, listOf(32)),
                LiteralValue.fromInt(0x7fff_ffff)
        )
        val expected = SvExpressionLiteral(Line(0), "32'h7fff_ffff")
        assertEquals(expected, PsExpressionExtractorLiteral.extract(literal))
    }

    @Test
    fun `sbit short`() {
        val literal = PsExpressionLiteral(
                Line(0),
                ReifiedType(TYPE_SBIT, INSTANCE, listOf(8)),
                LiteralValue.fromInt(0x12)
        )
        val expected = SvExpressionLiteral(Line(0), "8'sh12")
        assertEquals(expected, PsExpressionExtractorLiteral.extract(literal))
    }
}
