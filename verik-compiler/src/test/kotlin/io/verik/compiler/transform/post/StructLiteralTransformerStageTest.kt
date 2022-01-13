/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.compiler.transform.post

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class StructLiteralTransformerStageTest : BaseTest() {

    @Test
    fun `struct literal`() {
        driveElementTest(
            """
                class S(val x: Boolean) : Struct()
                var s = S(false)
            """.trimIndent(),
            StructLiteralTransformerStage::class,
            "StructLiteralExpression(S, [StructLiteralEntry(x, ConstantExpression(*))])"
        ) { it.findExpression("s") }
    }
}
