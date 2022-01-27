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

package io.verik.importer.interpret

import io.verik.importer.test.BaseTest
import org.junit.jupiter.api.Test

internal class PackageInterpreterStageTest : BaseTest() {

    @Test
    fun `interpret root package`() {
        driveElementTest(
            """
                logic [1:0] x;
            """.trimIndent(),
            PackageInterpreterStage::class,
            "Project([KtPackage(imported, [KtFile(test.kt, [Property(*)])])])"
        ) { it }
    }

    @Test
    fun `interpret regular package`() {
        driveElementTest(
            """
                package p;
                    logic [1:0] x;
                endpackage
            """.trimIndent(),
            PackageInterpreterStage::class,
            "Project([KtPackage(imported.p, [KtFile(test.kt, [Property(*)])])])"
        ) { it }
    }
}
