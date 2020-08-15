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

package verik.core.vk

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import verik.core.al.AlRuleParser
import verik.core.assertThrowsMessage
import verik.core.kt.KtUtil
import verik.core.lang.LangSymbol.FUN_BOOL_TYPE
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.main.LineException
import verik.core.symbol.Symbol

internal class VkPropertyTest {

    @Test
    fun `bool property`() {
        val rule = AlRuleParser.parseDeclaration("val x = _bool()")
        val property = VkUtil.parseProperty(rule)
        val expected = VkProperty(
                1,
                "x",
                Symbol(1, 1, 1),
                VkExpressionFunction(
                        1,
                        TYPE_BOOL,
                        null,
                        listOf(),
                        FUN_BOOL_TYPE
                )
        )
        assertEquals(expected, property)
    }

    @Test
    fun `bool property illegal type`() {
        val rule = AlRuleParser.parseDeclaration("@input val x = _bool()")
        val declaration = KtUtil.resolveDeclaration(rule)
        assertFalse(VkProperty.isProperty(declaration))
        assertThrowsMessage<LineException>("property annotations are not supported here") {
            VkProperty(declaration)
        }
    }
}