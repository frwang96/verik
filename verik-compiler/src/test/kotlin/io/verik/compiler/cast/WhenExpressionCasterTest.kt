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

package io.verik.compiler.cast

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.findExpression
import org.junit.jupiter.api.Test

internal class WhenExpressionCasterTest : BaseTest() {

    @Test
    fun `when expression`() {
        driveTest(
            """
                var x = 0
                fun f() {
                    when (x) {
                        0 -> {}
                        else -> {}
                    }
                }
            """.trimIndent(),
            CasterStage::class,
            """
                WhenExpression(
                    Unit,
                    ReferenceExpression(*),
                    [WhenEntry([ConstantExpression(Int, 0)], *), WhenEntry([], *)]
                )
            """.trimIndent()
        ) { it.findExpression("f") }
    }
}
