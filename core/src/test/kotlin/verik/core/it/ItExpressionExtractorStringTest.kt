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

package verik.core.it

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.al.AlRuleParser
import verik.core.sv.SvExpressionFunction
import verik.core.sv.SvExpressionLiteral

internal class ItExpressionExtractorStringTest {

    @Test
    fun `literal simple`() {
        val rule = AlRuleParser.parseExpression("\"0\"")
        val expected = SvExpressionLiteral(1, "\"0\"")
        assertEquals(expected, ItUtil.extractExpression(rule))
    }

    @Test
    fun `literal escaped`() {
        val rule = AlRuleParser.parseExpression("\"%\"")
        val expected = SvExpressionLiteral(1, "\"%%\"")
        assertEquals(expected, ItUtil.extractExpression(rule))
    }

    @Test
    fun `expression dec`() {
        val rule = AlRuleParser.parseExpression("\"\${0}\"")
        val expected = SvExpressionFunction(
                1,
                null,
                "\$sformatf",
                listOf(
                        SvExpressionLiteral(1, "\"%0d\""),
                        SvExpressionLiteral(1, "0")
                )
        )
        assertEquals(expected, ItUtil.extractExpression(rule))
    }

    @Test
    fun `expression hex`() {
        val rule = AlRuleParser.parseExpression("\"0x\${0}\"")
        val expected = SvExpressionFunction(
                1,
                null,
                "\$sformatf",
                listOf(
                        SvExpressionLiteral(1, "\"%h\""),
                        SvExpressionLiteral(1, "0")
                )
        )
        assertEquals(expected, ItUtil.extractExpression(rule))
    }
}