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
import verik.core.base.ast.LineException
import verik.core.base.ast.LiteralValue
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_REIFIED_UNIT
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.lang.LangSymbol.TYPE_UNIT
import verik.core.rf.ast.RfExpressionLiteral
import verik.core.rf.ast.RfReifiedType
import verik.core.rf.ast.RfTypeClass

internal class LangReifierUtilTest {

    @Test
    fun `implicit cast to uint`() {
        val intExpression = RfExpressionLiteral(
                0,
                TYPE_INT,
                null,
                LiteralValue.fromInt(0)
        )
        val pairedExpression = RfExpressionLiteral(
                0,
                TYPE_UINT,
                RfReifiedType(TYPE_UINT, RfTypeClass.INSTANCE, listOf(8)),
                LiteralValue.fromInt(0)
        )
        LangReifierUtil.implicitCast(intExpression, pairedExpression)
        assertEquals(intExpression.reifiedType, pairedExpression.reifiedType)
    }

    @Test
    fun `implicit cast invalid type`() {
        val intExpression = RfExpressionLiteral(
                0,
                TYPE_INT,
                null,
                LiteralValue.fromInt(0)
        )
        val pairedExpression = RfExpressionLiteral(
                0,
                TYPE_UNIT,
                TYPE_REIFIED_UNIT,
                LiteralValue.fromInt(0)
        )
        assertThrowsMessage<LineException>("unable to cast integer to $TYPE_UNIT()") {
            LangReifierUtil.implicitCast(intExpression, pairedExpression)
        }
    }

    @Test
    fun `implicit cast width mismatch`() {
        val intExpression = RfExpressionLiteral(
                0,
                TYPE_INT,
                null,
                LiteralValue.fromInt(0xFF)
        )
        val pairedExpression = RfExpressionLiteral(
                0,
                TYPE_UINT,
                RfReifiedType(TYPE_UINT, RfTypeClass.INSTANCE, listOf(4)),
                LiteralValue.fromInt(0)
        )
        assertThrowsMessage<LineException>("unable to cast integer of width 8 to $TYPE_UINT(4)") {
            LangReifierUtil.implicitCast(intExpression, pairedExpression)
        }
    }
}