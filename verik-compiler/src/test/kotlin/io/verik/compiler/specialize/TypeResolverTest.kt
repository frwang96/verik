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

package io.verik.compiler.specialize

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.assertElementEquals
import io.verik.compiler.util.driveTest
import io.verik.compiler.util.findDeclaration
import io.verik.compiler.util.findExpression
import org.junit.jupiter.api.Test

internal class TypeResolverTest : BaseTest() {

    @Test
    fun `resolve property`() {
        val projectContext = driveTest(
            TypeResolver::class,
            """
                val x = u<`8`>(0)
            """.trimIndent()
        )
        assertElementEquals(
            "KtProperty(x, Ubit<`8`>, *)",
            projectContext.findDeclaration("x")
        )
    }

    @Test
    fun `resolve reference expression`() {
        val projectContext = driveTest(
            TypeResolver::class,
            """
                var x = u<`8`>(0)
                fun f() {
                    x
                }
            """.trimIndent()
        )
        assertElementEquals(
            "KtReferenceExpression(Ubit<`8`>, x, null)",
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `resolve call expression`() {
        val projectContext = driveTest(
            TypeResolver::class,
            """
                fun f() {
                    u<`8`>(0)
                }
            """.trimIndent()
        )
        assertElementEquals(
            "KtCallExpression(Ubit<`8`>, u, null, *, *)",
            projectContext.findExpression("f")
        )
    }
}
