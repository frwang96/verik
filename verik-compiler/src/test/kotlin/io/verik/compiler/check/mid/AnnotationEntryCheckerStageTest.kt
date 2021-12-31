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

internal class AnnotationEntryCheckerStageTest : BaseTest() {

    @Test
    fun `class top annotation illegal`() {
        driveMessageTest(
            """
                @SimTop
                class C
            """.trimIndent(),
            true,
            "Declaration annotated as top must be a module"
        )
    }

    @Test
    fun `class top annotations conflicting`() {
        driveMessageTest(
            """
                @SynthTop
                @SimTop
                class M : Module()
            """.trimIndent(),
            true,
            "Conflicting annotations: @SynthTop and @SimTop"
        )
    }

    @Test
    fun `function annotations conflicting`() {
        driveMessageTest(
            """
                @Com
                @Seq
                fun f() {}
            """.trimIndent(),
            true,
            "Conflicting annotations: @Seq and @Com"
        )
    }

    @Test
    fun `value parameter annotations conflicting`() {
        driveMessageTest(
            """
                class M(@In @Out val x: Boolean) : Module()
            """.trimIndent(),
            true,
            "Conflicting annotations: @In and @Out"
        )
    }
}
