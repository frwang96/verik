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

package verik.core.vk.ast

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.assertThrowsMessage
import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.kt.ast.KtAnnotationProperty
import verik.core.kt.ast.KtPrimaryProperty
import verik.core.kt.ast.KtExpressionFunction
import verik.core.kt.KtUtil
import verik.core.vk.ast.VkComponentInstance

internal class VkComponentInstanceTest {

    @Test
    fun `component instance`() {
        val declaration = KtPrimaryProperty(
                0,
                "m",
                Symbol(1, 1, 2),
                Symbol(1, 1, 1),
                listOf(KtAnnotationProperty.MAKE),
                KtExpressionFunction(0, Symbol(1, 1, 1), "_m", null, listOf(), null)
        )
        val expected = VkComponentInstance(
                0,
                "m",
                Symbol(1, 1, 2),
                Symbol(1, 1, 1),
                listOf()
        )
        assertEquals(
                expected,
                VkComponentInstance(declaration)
        )
    }

    @Test
    fun `no annotation`() {
        val declaration = KtPrimaryProperty(
                0,
                "m",
                Symbol(1, 1, 2),
                null,
                listOf(),
                KtUtil.EXPRESSION_NULL
        )
        assertThrowsMessage<LineException>("component annotation expected") {
            VkComponentInstance(declaration)
        }
    }
}