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
import io.verik.compiler.util.assertElementEquals
import io.verik.compiler.util.driveTest
import org.junit.jupiter.api.Test

internal class CasterStageTest : BaseTest() {

    @Test
    fun `project empty`() {
        val projectContext = driveTest(CasterStage::class, "")
        assertElementEquals(
            "Project([BasicPackage(verik, [File([])])], RootPackage(root, []))",
            projectContext.project
        )
    }

    @Test
    fun `file class`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                class C
            """.trimIndent()
        )
        assertElementEquals(
            "File([KtBasicClass(C, [], [], [], false, PrimaryConstructor(C, []))])",
            projectContext.project.files().first()
        )
    }

    @Test
    fun `file classes`() {
        val projectContext = driveTest(
            CasterStage::class,
            """
                class C
                class D
            """.trimIndent()
        )
        assertElementEquals(
            """
                File([
                    KtBasicClass(C, [], [], [], false, PrimaryConstructor(C, [])),
                    KtBasicClass(D, [], [], [], false, PrimaryConstructor(D, []))
                ])
            """.trimIndent(),
            projectContext.project.files().first()
        )
    }
}
