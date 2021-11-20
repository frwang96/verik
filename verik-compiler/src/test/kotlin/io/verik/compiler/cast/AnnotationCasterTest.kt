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
import io.verik.compiler.util.findDeclaration
import org.junit.jupiter.api.Test

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
            "KtFunction(f, Unit, *, [], [], [Annotation(Task, [])], 0)",
            projectContext.findDeclaration("f")
        )
    }

    @Test
    fun `annotation with argument`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                @Rename("g")
                fun f() {}
            """.trimIndent()
        )
        assertElementEquals(
            "KtFunction(f, Unit, *, [], [], [Annotation(Rename, [g])], 0)",
            projectContext.findDeclaration("f")
        )
    }

    @Test
    fun `annotation with argument illegal`() {
        driveTest(
            """
                @Rename("g" + "h")
                fun f() {}
            """.trimIndent(),
            true,
            "String literal expected for annotation argument"
        )
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
            "KtValueParameter(x, Boolean, [Annotation(In, [])], 1, 0)",
            projectContext.findDeclaration("x")
        )
    }
}
