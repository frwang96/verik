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

package io.verik.compiler.check.post

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.TestErrorException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PortInstantiationCheckerStageTest : BaseTest() {

    @Test
    fun `illegal expression`() {
        assertThrows<TestErrorException> {
            driveTest(
                PortInstantiationCheckerStage::class,
                """
                    class N(@Out var x: Boolean) : Module()
                    class M : Module() {
                        @Make
                        val m = N(false)
                    }
                """.trimIndent()
            )
        }.apply {
            Assertions.assertEquals("Illegal expression for output port: x", message)
        }
    }

    @Test
    fun `immutable property`() {
        assertThrows<TestErrorException> {
            driveTest(
                PortInstantiationCheckerStage::class,
                """
                    class N(@Out var x: Boolean) : Module()
                    class M : Module() {
                        private val y: Boolean = nc()
                        @Make
                        val m = N(y)
                    }
                """.trimIndent()
            )
        }.apply {
            Assertions.assertEquals("Property assigned by output port must be declared as var: y", message)
        }
    }
}
