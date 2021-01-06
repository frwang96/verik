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

package verikc.kt.parse

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.symbol.Symbol
import verikc.kt.KtParseUtil
import verikc.kt.ast.KtBlock
import verikc.kt.ast.KtFunction
import verikc.kt.ast.KtParameterProperty
import verikc.kt.ast.KtStatementExpression
import verikc.line

internal class KtParserFunctionTest {

    @Test
    fun `function simple`() {
        val string = "fun x() {}"
        val expected = KtFunction(
            line(2),
            "x",
            Symbol(3),
            listOf(),
            listOf(),
            "_unit",
            KtBlock(line(2), Symbol(4), listOf(), listOf())
        )
        assertEquals(expected, KtParseUtil.parseFunction(string))
    }

    @Test
    fun `function with parameters`() {
        val string = "fun x(x: _int) {}"
        val expected = KtFunction(
            line(2),
            "x",
            Symbol(3),
            listOf(),
            listOf(KtParameterProperty(line(2), "x", Symbol(4), "_int", null)),
            "_unit",
            KtBlock(line(2), Symbol(5), listOf(), listOf())
        )
        assertEquals(expected, KtParseUtil.parseFunction(string))
    }

    @Test
    fun `function with return type`() {
        val string = "fun x(): _int {}"
        val expected = KtFunction(
            line(2),
            "x",
            Symbol(3),
            listOf(),
            listOf(),
            "_int",
            KtBlock(line(2), Symbol(4), listOf(), listOf())
        )
        assertEquals(expected, KtParseUtil.parseFunction(string))
    }

    @Test
    fun `function with block`() {
        val string = "fun x() { 0 }"
        val expected = KtFunction(
            line(2),
            "x",
            Symbol(3),
            listOf(),
            listOf(),
            "_unit",
            KtBlock(
                line(2),
                Symbol(4),
                listOf(),
                listOf(KtStatementExpression.wrapLiteral(line(2), "0"))
            )
        )
        assertEquals(expected, KtParseUtil.parseFunction(string))
    }
}
