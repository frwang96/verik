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

package io.verik.compiler.check.mid

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class ClassDeclarationCheckerStageTest : BaseTest() {

    @Test
    fun `module in class`() {
        driveMessageTest(
            """
                class C : Class() {
                    class M : Module()
                }
            """.trimIndent(),
            true,
            "Declaration is not permitted in class"
        )
    }

    @Test
    fun `class in struct`() {
        driveMessageTest(
            """
                class S : Struct() {
                    class C : Class()
                }
            """.trimIndent(),
            true,
            "Declaration is not permitted in struct"
        )
    }
}
