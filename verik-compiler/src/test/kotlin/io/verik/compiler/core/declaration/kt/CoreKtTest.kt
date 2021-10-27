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

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.transform.mid.FunctionTransformerStage
import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.findExpression
import org.junit.jupiter.api.Test

internal class CoreKtTest : BaseTest() {

    @Test
    fun `transform repeat`() {
        val projectContext = driveTest(
            FunctionTransformerStage::class,
            """
                fun f() {
                    repeat(1) {}
                }
            """.trimIndent()
        )
        assertElementEquals(
            "RepeatStatement(Void, ConstantExpression(*), KtBlockExpression(*))",
            projectContext.findExpression("f")
        )
    }
}