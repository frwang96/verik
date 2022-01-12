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
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class SpecializerIndexerTest : BaseTest() {

    @Test
    fun `call expression resolved`() {
        driveElementTest(
            """
                class C<X: `*`>
                val x = C<`1`>()
            """.trimIndent(),
            SpecializerStage::class,
            "CallExpression(C<`*`>, C, null, [], [`1`])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `call expression unresolved`() {
        driveMessageTest(
            """
                class C<X: `*`>
                val x = C<`*`>()
            """.trimIndent(),
            true,
            "Type arguments must be explicitly provided: C"
        )
    }
}
