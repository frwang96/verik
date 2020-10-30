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
import verik.core.base.ast.LiteralValue
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_SINT
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.rf.ast.RfExpressionLiteral
import verik.core.rf.ast.RfReifiedType
import verik.core.rf.ast.RfTypeClass
import verik.core.sv.ast.SvExpressionLiteral

internal class RfExpressionExtractorLiteralTest {

    @Test
    fun `bool true`() {
        val literal = RfExpressionLiteral(
                0,
                TYPE_BOOL,
                RfReifiedType(TYPE_BOOL, RfTypeClass.INSTANCE, listOf()),
                LiteralValue.fromBoolean(true)
        )
        val expected = SvExpressionLiteral(0, "1'b1")
        assertEquals(expected, RfExpressionExtractorLiteral.extract(literal))
    }

    @Test
    fun `int positive`() {
        val literal = RfExpressionLiteral(
                0,
                TYPE_INT,
                RfReifiedType(TYPE_INT, RfTypeClass.INSTANCE, listOf()),
                LiteralValue.fromInt(1)
        )
        val expected = SvExpressionLiteral(0, "1")
        assertEquals(expected, RfExpressionExtractorLiteral.extract(literal))
    }

    @Test
    fun `int negative`() {
        val literal = RfExpressionLiteral(
                0,
                TYPE_INT,
                RfReifiedType(TYPE_INT, RfTypeClass.INSTANCE, listOf()),
                LiteralValue.fromInt(-1)
        )
        val expected = SvExpressionLiteral(0, "-1")
        assertEquals(expected, RfExpressionExtractorLiteral.extract(literal))
    }

    @Test
    fun `uint short`() {
        val literal = RfExpressionLiteral(
                0,
                TYPE_UINT,
                RfReifiedType(TYPE_UINT, RfTypeClass.INSTANCE, listOf(6)),
                LiteralValue.fromInt(0xf)
        )
        val expected = SvExpressionLiteral(0, "6'h0f")
        assertEquals(expected, RfExpressionExtractorLiteral.extract(literal))
    }

    @Test
    fun `uint long`() {
        val literal = RfExpressionLiteral(
                0,
                TYPE_UINT,
                RfReifiedType(TYPE_UINT, RfTypeClass.INSTANCE, listOf(32)),
                LiteralValue.fromInt(0x7fff_ffff)
        )
        val expected = SvExpressionLiteral(0, "32'h7fff_ffff")
        assertEquals(expected, RfExpressionExtractorLiteral.extract(literal))
    }

    @Test
    fun `sint short`() {
        val literal = RfExpressionLiteral(
                0,
                TYPE_SINT,
                RfReifiedType(TYPE_SINT, RfTypeClass.INSTANCE, listOf(8)),
                LiteralValue.fromInt(0x12)
        )
        val expected = SvExpressionLiteral(0, "8'sh12")
        assertEquals(expected, RfExpressionExtractorLiteral.extract(literal))
    }
}