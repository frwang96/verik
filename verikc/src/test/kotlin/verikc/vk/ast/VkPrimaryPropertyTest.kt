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

package verikc.vk.ast

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.Symbol
import verikc.kt.KtUtil
import verikc.lang.LangSymbol.FUNCTION_TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.vk.VkUtil

internal class VkPrimaryPropertyTest {

    @Test
    fun `bool property`() {
        val string = "val x = _bool()"
        val property = VkUtil.parsePrimaryProperty(string)
        val expected = VkPrimaryProperty(
                Line(1),
                "x",
                Symbol(1, 1, 1),
                TYPE_BOOL,
                VkExpressionFunction(
                        Line(1),
                        TYPE_BOOL,
                        FUNCTION_TYPE_BOOL,
                        null,
                        listOf()
                )
        )
        assertEquals(expected, property)
    }

    @Test
    fun `bool property illegal type`() {
        val string = "@input val x = _bool()"
        val declaration = KtUtil.resolveDeclaration(string)
        assertFalse(VkPrimaryProperty.isPrimaryProperty(declaration))
        assertThrowsMessage<LineException>("property annotations are not supported here") {
            VkPrimaryProperty(declaration)
        }
    }
}
