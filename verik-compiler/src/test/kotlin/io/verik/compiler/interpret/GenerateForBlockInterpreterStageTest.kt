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

package io.verik.compiler.interpret

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class GenerateForBlockInterpreterStageTest : BaseTest() {

    @Test
    fun `interpret cluster property`() {
        driveElementTest(
            """
                val x = cluster(8) { it }
            """.trimIndent(),
            GenerateForBlockInterpreterStage::class,
            """
                GenerateForBlock(
                    x, Property(it, Int, null, 1, 0),
                    Property(gen, Int, ReferenceExpression(Int, it, null, 0), 1, 0), 8
                )
            """.trimIndent()
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `transform reference cluster property`() {
        driveElementTest(
            """
                val x = cluster(8) { it }
                val y = x[0]
            """.trimIndent(),
            GenerateForBlockInterpreterStage::class,
            """
                ReferenceExpression(
                    Int, gen,
                    CallExpression(Int, get, ReferenceExpression(Cluster<`8`, Int>, x, null, 0), 0, [*], []), 0
                )
            """.trimIndent()
        ) { it.findExpression("y") }
    }
}
