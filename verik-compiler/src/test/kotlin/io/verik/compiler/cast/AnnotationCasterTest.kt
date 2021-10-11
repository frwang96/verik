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

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.TestErrorException
import io.verik.compiler.util.assertElementEquals
import io.verik.compiler.util.driveTest
import io.verik.compiler.util.findDeclaration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AnnotationCasterTest : BaseTest() {

    @Test
    fun `annotation simple`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                @Task
                fun f() {}
            """.trimIndent()
        )
        assertElementEquals(
            "KtFunction(f, Unit, *, [], [], [Annotation(Task, [])])",
            projectContext.findDeclaration("f")
        )
    }

    @Test
    fun `annotation with argument`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                @Relabel("g")
                fun f() {}
            """.trimIndent()
        )
        assertElementEquals(
            "KtFunction(f, Unit, *, [], [], [Annotation(Relabel, [g])])",
            projectContext.findDeclaration("f")
        )
    }

    @Test
    fun `annotation with argument illegal`() {
        assertThrows<TestErrorException> {
            driveTest(
                CasterStage::class,
                """
                @Relabel("g" + "h")
                fun f() {}
                """.trimIndent()
            )
        }.apply { assertEquals("String literal expected for annotation argument", message) }
    }

    @Test
    fun `annotation on value parameter`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                class M(@In val x: Boolean) : Module()
            """.trimIndent()
        )
        assertElementEquals(
            "KtValueParameter(x, Boolean, [Annotation(In, [])])",
            projectContext.findDeclaration("x")
        )
    }
}
