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

internal class OverrideCheckerStageTest : BaseTest() {

    @Test
    fun `function is task`() {
        driveMessageTest(
            """
                open class C0 : Class() {
                    @Task
                    open fun f() {}
                }
                class C1 : C0() {
                    override fun f() {}
                }
            """.trimIndent(),
            true,
            "Function should be annotated with @Task: f"
        )
    }

    @Test
    fun `function not task`() {
        driveMessageTest(
            """
                open class C0 : Class() {
                    open fun f() {}
                }
                class C1 : C0() {
                    @Task
                    override fun f() {}
                }
            """.trimIndent(),
            true,
            "Function should not be annotated with @Task: f"
        )
    }
}
