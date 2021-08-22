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
import io.verik.compiler.util.assertElementEquals
import io.verik.compiler.util.driveTest
import io.verik.compiler.util.findExpression
import org.junit.jupiter.api.Test

internal class CallExpressionCasterTest : BaseTest() {

    @Test
    fun `type argument`() {
        val projectContext = driveTest(
            ProjectCaster::class,
            """
                var x = u<`8`>(0)
            """.trimIndent()
        )
        assertElementEquals(
            "KtCallExpression(Ubit<`*`>, u, null, [`8`], *)",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `value argument unnamed`() {
        val projectContext = driveTest(
            ProjectCaster::class,
            """
                fun f(x: Int) {}
                var x = f(0)
            """.trimIndent()
        )
        assertElementEquals(
            "KtCallExpression(Unit, f, null, null, [ConstantExpression(*)])",
            projectContext.findExpression("x")
        )
    }
}