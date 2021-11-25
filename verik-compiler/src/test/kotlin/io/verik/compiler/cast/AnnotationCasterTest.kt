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

package io.verik.compiler.cast

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class AnnotationCasterTest : BaseTest() {

    @Test
    fun `annotation simple`() {
        driveElementTest(
            """
                @Task
                fun f() {}
            """.trimIndent(),
            CasterStage::class,
            "KtFunction(f, Unit, *, [], [], [Annotation(Task)], 0)"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `annotation unrecognized`() {
        driveMessageTest(
            """
                @Synchronized
                fun f() {}
            """.trimIndent(),
            true,
            "Could not identify annotation: Synchronized"
        )
    }

    @Test
    fun `annotation on value parameter`() {
        driveElementTest(
            """
                class M(@In val x: Boolean) : Module()
            """.trimIndent(),
            CasterStage::class,
            "KtValueParameter(x, Boolean, [Annotation(In)], 1, 0)",
        ) { it.findDeclaration("x") }
    }
}
