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

package verik.core.it.extract

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.base.LiteralValue
import verik.core.it.ItExpressionLiteral
import verik.core.it.ItTypeClass
import verik.core.it.ItTypeReified
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_SINT
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.sv.SvExpressionLiteral

internal class ItExpressionExtractorLiteralTest {

    @Test
    fun `bool true`() {
        val literal = ItExpressionLiteral(
                0,
                TYPE_BOOL,
                ItTypeReified(TYPE_BOOL, ItTypeClass.INSTANCE, listOf()),
                LiteralValue.fromBoolean(true)
        )
        val expected = SvExpressionLiteral(0, "1'b1")
        assertEquals(expected, ItExpressionExtractorLiteral.extract(literal))
    }

    @Test
    fun `int positive`() {
        val literal = ItExpressionLiteral(
                0,
                TYPE_INT,
                ItTypeReified(TYPE_INT, ItTypeClass.INSTANCE, listOf()),
                LiteralValue.fromIntImplicit(1)
        )
        val expected = SvExpressionLiteral(0, "1")
        assertEquals(expected, ItExpressionExtractorLiteral.extract(literal))
    }

    @Test
    fun `int negative`() {
        val literal = ItExpressionLiteral(
                0,
                TYPE_INT,
                ItTypeReified(TYPE_INT, ItTypeClass.INSTANCE, listOf()),
                LiteralValue.fromIntImplicit(-1)
        )
        val expected = SvExpressionLiteral(0, "-1")
        assertEquals(expected, ItExpressionExtractorLiteral.extract(literal))
    }

    @Test
    fun `uint short`() {
        val literal = ItExpressionLiteral(
                0,
                TYPE_UINT,
                ItTypeReified(TYPE_UINT, ItTypeClass.INSTANCE, listOf(6)),
                LiteralValue.fromIntExplicit(0xf, 4)
        )
        val expected = SvExpressionLiteral(0, "6'h0f")
        assertEquals(expected, ItExpressionExtractorLiteral.extract(literal))
    }

    @Test
    fun `uint long`() {
        val literal = ItExpressionLiteral(
                0,
                TYPE_UINT,
                ItTypeReified(TYPE_UINT, ItTypeClass.INSTANCE, listOf(32)),
                LiteralValue.fromIntExplicit(0x7fff_ffff, 32)
        )
        val expected = SvExpressionLiteral(0, "32'h7fff_ffff")
        assertEquals(expected, ItExpressionExtractorLiteral.extract(literal))
    }

    @Test
    fun `sint short`() {
        val literal = ItExpressionLiteral(
                0,
                TYPE_SINT,
                ItTypeReified(TYPE_SINT, ItTypeClass.INSTANCE, listOf(8)),
                LiteralValue.fromIntExplicit(0x12, 8)
        )
        val expected = SvExpressionLiteral(0, "8'sh12")
        assertEquals(expected, ItExpressionExtractorLiteral.extract(literal))
    }
}