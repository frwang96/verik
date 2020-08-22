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

package verik.core.lang.reify

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.assertThrowsMessage
import verik.core.base.LineException
import verik.core.base.LiteralValue
import verik.core.it.ItExpressionLiteral
import verik.core.it.ItTypeClass
import verik.core.it.ItTypeReified
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_REIFIED_UNIT
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.lang.LangSymbol.TYPE_UNIT

internal class LangReifierUtilTest {

    @Test
    fun `implicit cast to uint`() {
        val intExpression = ItExpressionLiteral(
                0,
                TYPE_INT,
                null,
                LiteralValue.fromIntImplicit(0)
        )
        val pairedExpression = ItExpressionLiteral(
                0,
                TYPE_UINT,
                ItTypeReified(TYPE_UINT, ItTypeClass.INSTANCE, listOf(8)),
                LiteralValue.fromIntImplicit(0)
        )
        LangReifierUtil.implicitCast(intExpression, pairedExpression)
        assertEquals(intExpression.typeReified, pairedExpression.typeReified)
    }

    @Test
    fun `implicit cast invalid type`() {
        val intExpression = ItExpressionLiteral(
                0,
                TYPE_INT,
                null,
                LiteralValue.fromIntImplicit(0)
        )
        val pairedExpression = ItExpressionLiteral(
                0,
                TYPE_UNIT,
                TYPE_REIFIED_UNIT,
                LiteralValue.fromIntImplicit(0)
        )
        assertThrowsMessage<LineException>("unable to cast integer to $TYPE_UNIT()") {
            LangReifierUtil.implicitCast(intExpression, pairedExpression)
        }
    }

    @Test
    fun `implicit cast size mismatch`() {
        val intExpression = ItExpressionLiteral(
                0,
                TYPE_INT,
                null,
                LiteralValue.fromIntExplicit(0, 8)
        )
        val pairedExpression = ItExpressionLiteral(
                0,
                TYPE_UINT,
                ItTypeReified(TYPE_UINT, ItTypeClass.INSTANCE, listOf(4)),
                LiteralValue.fromIntImplicit(0)
        )
        assertThrowsMessage<LineException>("unable to cast integer of size 8 to $TYPE_UINT(4)") {
            LangReifierUtil.implicitCast(intExpression, pairedExpression)
        }
    }
}