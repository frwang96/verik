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

package io.verik.compiler.compile

import io.verik.compiler.util.BaseTest
import org.junit.jupiter.api.Test

class KotlinCompilerAnalyzerStageTest : BaseTest() {

    @Test
    fun `compile simple`() {
        driveTest(
            KotlinCompilerAnalyzerStage::class,
            """
                class C {
                    fun f() {
                        println()
                    }
                }
            """.trimIndent()
        )
    }

    @Test
    fun `compile error`() {
        driveTest(
            """
                class C {
                    fun f() {
                        g()
                    }
                }
            """.trimIndent(),
            true,
            "Unresolved reference: g"
        )
    }
}
