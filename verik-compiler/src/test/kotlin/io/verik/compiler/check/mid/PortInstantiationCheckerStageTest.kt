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

package io.verik.compiler.check.mid

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class PortInstantiationCheckerStageTest : BaseTest() {

    @Test
    fun `input not connected`() {
        driveMessageTest(
            """
                class M(@In var x: Boolean) : Module()
                class Top : Module() {
                    @Make
                    val m = M(nc())
                }
            """.trimIndent(),
            true,
            "Input port not connected: x"
        )
    }

    @Test
    fun `output illegal expression`() {
        driveMessageTest(
            """
                class N(@Out var x: Boolean) : Module()
                class M : Module() {
                    @Make
                    val m = N(false)
                }
            """.trimIndent(),
            true,
            "Illegal expression for output port"
        )
    }

    @Test
    fun `output immutable property`() {
        driveMessageTest(
            """
                class N(@Out var x: Boolean) : Module()
                class M : Module() {
                    private val y: Boolean = nc()
                    @Make
                    val m = N(y)
                }
            """.trimIndent(),
            true,
            "Property assigned by output port must be declared as var: y"
        )
    }
}
