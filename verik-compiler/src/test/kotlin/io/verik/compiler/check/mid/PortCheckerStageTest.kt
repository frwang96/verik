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

internal class PortCheckerStageTest : BaseTest() {

    @Test
    fun `output port not mutable`() {
        driveMessageTest(
            """
                class M(@Out val x: Boolean): Module()
            """.trimIndent(),
            true,
            "Output port must be declared as var: x"
        )
    }

    @Test
    fun `module interface port mutable`() {
        driveMessageTest(
            """
                class MI: ModuleInterface()
                class M(var mi: ModuleInterface): Module()
            """.trimIndent(),
            true,
            "Module interface port must be declared as val: mi"
        )
    }

    @Test
    fun `port no directionality`() {
        driveMessageTest(
            """
                class M(var x: Boolean): Module()
            """.trimIndent(),
            true,
            "Could not determine directionality of port: x"
        )
    }
}
