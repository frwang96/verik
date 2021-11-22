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
import org.junit.jupiter.api.Test

internal class CasterStageTest : BaseTest() {

    @Test
    fun `project empty`() {
        driveTest(
            "",
            CasterStage::class,
            "Project([BasicPackage(test, [File([])])], [], RootPackage(<root>, []), RootPackage(<root>, []))",
        ) { it }
    }

    @Test
    fun `file class`() {
        driveTest(
            """
                class C
            """.trimIndent(),
            CasterStage::class,
            "File([KtBasicClass(C, C, [], [], [], 0, 0, 0, PrimaryConstructor(C, [], []), null)])"
        ) { it.files().first() }
    }

    @Test
    fun `file classes`() {
        driveTest(
            """
                class C
                class D
            """.trimIndent(),
            CasterStage::class,
            """
                File([
                    KtBasicClass(C, C, [], [], [], 0, 0, 0, PrimaryConstructor(C, [], []), null),
                    KtBasicClass(D, D, [], [], [], 0, 0, 0, PrimaryConstructor(D, [], []), null)
                ])
            """.trimIndent()
        ) { it.files().first() }
    }
}
