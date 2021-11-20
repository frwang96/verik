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

package io.verik.compiler.interpret

import io.verik.compiler.util.BaseTest
import org.junit.jupiter.api.Test

internal class ComponentInstantiationCheckerStageTest : BaseTest() {

    @Test
    fun `component instantiation out of context`() {
        driveTest(
            """
                class M : Module()
                @Make
                val m = M()
            """.trimIndent(),
            true,
            "Component instantiation out of context"
        )
    }

    @Test
    fun `make annotation illegal`() {
        driveTest(
            """
                @Make
                val x = false
            """.trimIndent(),
            true,
            "Make annotation only permitted on component instantiations"
        )
    }

    @Test
    fun `make annotation required`() {
        driveTest(
            """
                class M : Module()
                class N : Module() {
                    val m  = M()
                }
            """.trimIndent(),
            true,
            "Make annotation required"
        )
    }
}
