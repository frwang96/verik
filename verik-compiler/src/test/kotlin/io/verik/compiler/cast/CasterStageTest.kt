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
import org.junit.jupiter.api.Test

internal class CasterStageTest : BaseTest() {

    @Test
    fun `project empty`() {
        driveElementTest(
            "",
            CasterStage::class,
            """
                Project(
                    [Package(test, [File([])], NATIVE_REGULAR)],
                    Package(<root>, [], NATIVE_ROOT),
                    [],
                    Package(imported, [], IMPORTED_ROOT)
                )
            """.trimIndent()
        ) { it }
    }

    @Test
    fun `file class`() {
        driveElementTest(
            """
                class C
            """.trimIndent(),
            CasterStage::class,
            "File([KtClass(C, C, [], [], [], 0, 0, 0, PrimaryConstructor(C, C, [], []), null)])"
        ) { it.files().first() }
    }

    @Test
    fun `file classes`() {
        driveElementTest(
            """
                class C
                class D
            """.trimIndent(),
            CasterStage::class,
            """
                File([
                    KtClass(C, C, [], [], [], 0, 0, 0, PrimaryConstructor(C, C, [], []), null),
                    KtClass(D, D, [], [], [], 0, 0, 0, PrimaryConstructor(D, D, [], []), null)
                ])
            """.trimIndent()
        ) { it.files().first() }
    }
}
