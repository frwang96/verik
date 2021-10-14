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

package io.verik.compiler.check.pre

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.TestErrorException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UnsupportedModifierCheckerStageTest : BaseTest() {

    @Test
    fun `operator modifier`() {
        assertThrows<TestErrorException> {
            driveTest(
                UnsupportedModifierCheckerStage::class,
                """
                    class C {
                        operator fun get(int: Int) {}
                    }
                """.trimIndent()
            )
        }.apply {
            Assertions.assertEquals("Modifier operator not supported", message)
        }
    }

    @Test
    fun `operator vararg`() {
        assertThrows<TestErrorException> {
            driveTest(
                UnsupportedModifierCheckerStage::class,
                """
                    fun f(vararg x: Int) {}
                """.trimIndent()
            )
        }.apply {
            Assertions.assertEquals("Modifier vararg not supported", message)
        }
    }
}
