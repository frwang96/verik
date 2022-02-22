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

package io.verik.compiler.specialize

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class SpecializerIndexerTest : BaseTest() {

    @Test
    fun `type resolved`() {
        driveElementTest(
            """
                class C<X: `*`> : Class()
                val x: C<`1`> = C()
            """.trimIndent(),
            SpecializerStage::class,
            "Property(x, C<`1`>, CallExpression(*), 0, 0)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `primary constructor resolved`() {
        driveElementTest(
            """
                class C<X: `*`> : Class()
                val x = C<`1`>()
            """.trimIndent(),
            SpecializerStage::class,
            "CallExpression(C<`*`>, C, null, 0, [], [`1`])"
        ) { it.findExpression("x") }
    }
}
