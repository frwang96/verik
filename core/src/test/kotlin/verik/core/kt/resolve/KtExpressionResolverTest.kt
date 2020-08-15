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

package verik.core.kt.resolve

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.al.AlRuleParser
import verik.core.kt.KtUtil
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_UINT

internal class KtExpressionResolverTest {

    @Test
    fun `bool type function`() {
        val rule = AlRuleParser.parseExpression("_bool()")
        val expression = KtUtil.resolveExpression(rule)
        assertEquals(TYPE_BOOL, expression.type)
    }

    @Test
    fun `uint type function`() {
        val rule = AlRuleParser.parseExpression("_uint(1)")
        val expression = KtUtil.resolveExpression(rule)
        assertEquals(TYPE_UINT, expression.type)
    }

    @Test
    fun `bool literal`() {
        val rule = AlRuleParser.parseExpression("true")
        val expression = KtUtil.resolveExpression(rule)
        assertEquals(TYPE_BOOL, expression.type)
    }

    @Test
    fun `int literal`() {
        val rule = AlRuleParser.parseExpression("0")
        val expression = KtUtil.resolveExpression(rule)
        assertEquals(TYPE_INT, expression.type)
    }
}