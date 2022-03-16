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
    fun `cluster index property`() {
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
    fun `cluster index value parameter`() {
        driveElementTest(
            """
                fun f(y: Cluster<`2`, Int>): Int {
                    return y[0]
                }
            """.trimIndent(),
            ClusterUnrollTransformerStage::class,
            "ReturnStatement(Nothing, ReferenceExpression(Int, y_0, null, 0))"
        ) { it.findExpression("f") }
    }

    @Test
    fun `cluster index not constant`() {
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
    fun `cluster index out of bounds`() {
        driveMessageTest(
            """
                val x = cluster(2) { it }
                val z = x[2]
            """.trimIndent(),
            true,
            "Cluster index out of bounds: 2"
        )
    }

    @Test
    fun `cluster value argument`() {
        driveElementTest(
            """
                val x = cluster(2) { it }
                fun f(y: Cluster<`2`, Int>) {}
                fun g() {
                    f(x)
                }
            """.trimIndent(),
            ClusterUnrollTransformerStage::class,
            """
                CallExpression(
                    Unit, f, null, 0,
                    [ReferenceExpression(Int, x_0, null, 0), ReferenceExpression(Int, x_1, null, 0)], []
                )
            """.trimIndent()
        ) { it.findExpression("g") }
    }

    @Test
    fun `cluster value parameter`() {
        driveElementTest(
            """
                fun f(y: Cluster<`2`, Int>) {}
            """.trimIndent(),
            ClusterUnrollTransformerStage::class,
            """
                KtFunction(
                    f, Unit, BlockExpression(Unit, []),
                    [KtValueParameter(y_0, Int, null, 0, 0), KtValueParameter(y_1, Int, null, 0, 0)], [], 0
                )
            """.trimIndent()
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `cluster property`() {
        driveElementTest(
            """
                val x = cluster(2) { it }
            """.trimIndent(),
            ClusterUnrollTransformerStage::class,
            """
                [
                    Property(x_0, Int, ConstantExpression(Int, 0), 0, 0),
                    Property(x_1, Int, ConstantExpression(Int, 1), 0, 0)
                ]
            """.trimIndent()
        ) { it.regularFiles()[0].declarations }
    }
}
