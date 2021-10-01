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

package io.verik.compiler.transform.pre

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.assertElementEquals
import io.verik.compiler.util.driveTest
import io.verik.compiler.util.findExpression
import org.junit.jupiter.api.Test

internal class BitConstantTransformerStageTest : BaseTest() {

    @Test
    fun `constant decimal`() {
        val projectContext = driveTest(
            BitConstantTransformerStage::class,
            """
                var x = u(255)
            """.trimIndent()
        )
        assertElementEquals(
            "ConstantExpression(Ubit<`8`>, 8'hff)",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `constant hexadecimal`() {
        val projectContext = driveTest(
            BitConstantTransformerStage::class,
            """
                var x = u(0x00_0000_00ff)
            """.trimIndent()
        )
        assertElementEquals(
            "ConstantExpression(Ubit<`40`>, 40'h00_0000_00ff)",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `constant string binary`() {
        val projectContext = driveTest(
            BitConstantTransformerStage::class,
            """
                var x = u("4'b0011")
            """.trimIndent()
        )
        assertElementEquals(
            "ConstantExpression(Ubit<`4`>, 4'h3)",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `constant string hexadecimal`() {
        val projectContext = driveTest(
            BitConstantTransformerStage::class,
            """
                var x = u("10'h3ff")
            """.trimIndent()
        )
        assertElementEquals(
            "ConstantExpression(Ubit<`10`>, 10'h3ff)",
            projectContext.findExpression("x")
        )
    }
}
