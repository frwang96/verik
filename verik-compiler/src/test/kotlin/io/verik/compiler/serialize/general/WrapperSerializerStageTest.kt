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

package io.verik.compiler.serialize.general

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class WrapperSerializerStageTest : BaseTest() {

    @Test
    fun `package wrapper file`() {
        driveTextFileTest(
            """
                class C : Class()
            """.trimIndent(),
            """
                `timescale 1ns / 1ns

                package test_pkg;
                
                typedef class C;
                
                `include "src/test/Test.svh"
                
                endpackage : test_pkg
            """.trimIndent()
        ) { it.wrapperTextFile }
    }

    @Test
    fun `non package wrapper file`() {
        driveTextFileTest(
            """
                class M : Module()
            """.trimIndent(),
            """
                `timescale 1ns / 1ns

                `include "src/test/Test.sv"
            """.trimIndent()
        ) { it.wrapperTextFile }
    }
}
