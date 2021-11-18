/*
 * Copyright (c) 2021 Francis Wang
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

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.transform.mid.FunctionTransformerStage
import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.TestErrorException
import io.verik.compiler.util.findExpression
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class CoreVkMiscTest : BaseTest() {

    @Test
    fun `transform cat`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                val x = cat(u(0), u(0))
            """.trimIndent()
        )
        assertElementEquals(
            "ConcatenationExpression(Ubit<`2`>, [*, *])",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `transform cat illegal`() {
        assertThrows<TestErrorException> {
            driveTest(
                FunctionTransformerStage::class,
                """
                val x = cat(u(0))
                """.trimIndent()
            )
        }.apply { Assertions.assertEquals("Concatenation expects at least two arguments", message) }
    }

    @Test
    fun `transform rep`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                val x = rep<`3`>(false)
            """.trimIndent()
        )
        assertElementEquals(
            "ReplicationExpression(Ubit<`3`>, *, 3)",
            projectContext.findExpression("x")
        )
    }
}
