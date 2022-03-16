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

package io.verik.compiler.evaluate

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class ClusterUnrollTransformerStageTest : BaseTest() {

    @Test
    fun `unroll cluster`() {
        driveElementTest(
            """
                val x = cluster(2) { it }
            """.trimIndent(),
            ClusterUnrollTransformerStage::class,
            """
                Cluster(x, [
                    Property(x_0, Int, ConstantExpression(Int, 0), 0, 0),
                    Property(x_1, Int, ConstantExpression(Int, 1), 0, 0)
                ])
            """.trimIndent()
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `transform cluster reference`() {
        driveElementTest(
            """
                val x = cluster(2) { it }
                val y = x[0]
            """.trimIndent(),
            ClusterUnrollTransformerStage::class,
            "ReferenceExpression(Int, x_0, null, 0)"
        ) { it.findExpression("y") }
    }

    @Test
    fun `transform cluster reference not constant`() {
        driveMessageTest(
            """
                val x = cluster(2) { it }
                var y = 0
                val z = x[y]
            """.trimIndent(),
            true,
            "Expression is not compile time constant"
        )
    }

    @Test
    fun `transform cluster reference out of bounds`() {
        driveMessageTest(
            """
                val x = cluster(2) { it }
                val z = x[2]
            """.trimIndent(),
            true,
            "Cluster index out of bounds: 2"
        )
    }
}
