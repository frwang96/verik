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
import io.verik.compiler.util.TestDriver
import io.verik.compiler.util.assertElementEquals
import io.verik.compiler.util.findExpression
import org.junit.jupiter.api.Test

internal class CasterExpressionVisitorTest: BaseTest() {

    @Test
    fun `block expression empty`() {
        val projectContext = TestDriver.cast("""
            fun f() {}
        """.trimIndent())
        assertElementEquals(
            "BlockExpression(Unit, [])",
            projectContext.findExpression("f")
        )
    }

    @Test
    fun `reference expression`() {
        val projectContext = TestDriver.cast("""
            var x = 0
            var y = x
        """.trimIndent())
        assertElementEquals(
            "ReferenceExpression(Int, x)",
            projectContext.findExpression("y")
        )
    }

    @Test
    fun `call expression`() {
        val projectContext = TestDriver.cast("""
            fun f() {}
            var x = f()
        """.trimIndent())
        assertElementEquals(
            "CallExpression(Unit, f)",
            projectContext.findExpression("x")
        )
    }

    @Test
    fun `constant expression integer`() {
        val projectContext = TestDriver.cast("""
            var x = 0
        """.trimIndent())
        assertElementEquals(
            "ConstantExpression(Int, 0)",
            projectContext.findExpression("x")
        )
    }
}