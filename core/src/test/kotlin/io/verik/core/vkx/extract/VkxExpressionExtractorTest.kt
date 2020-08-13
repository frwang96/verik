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

package io.verik.core.vkx.extract

import io.verik.core.al.AlRuleParser
import io.verik.core.svx.SvxExpressionFunction
import io.verik.core.vkx.resolveExpression
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VkxExpressionExtractorTest {

    @Test
    fun `extract finish function`() {
        val rule = AlRuleParser.parseExpression("finish()")
        val expression = resolveExpression(rule).extract()
        val expected = SvxExpressionFunction(
                1,
                null,
                "\$finish",
                listOf()
        )
        assertEquals(expected, expression)
    }
}