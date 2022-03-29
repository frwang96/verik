/*
 * SPDX-License-Identifier: Apache-2.0
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
    fun `cluster value argument map`() {
        driveElementTest(
            """
                val x = cluster(1) { ArrayList<Int>() }
                fun f(y: Cluster<`1`, Int>) {}
                fun g() {
                    f(x.map { it.size })
                }
            """.trimIndent(),
            ClusterUnrollTransformerStage::class,
            """
                CallExpression(
                    Unit, f, null, 0,
                    [ReferenceExpression(Int, size, ReferenceExpression(ArrayList<Int>, x_0, null, 0), 0)], []
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

    @Test
    fun `cluster property multiple`() {
        driveElementTest(
            """
                val x = cluster(1) { it }
                val y = cluster(1) { x[it] }
            """.trimIndent(),
            ClusterUnrollTransformerStage::class,
            """
                [
                    Property(x_0, Int, ConstantExpression(Int, 0), 0, 0),
                    Property(y_0, Int, ReferenceExpression(Int, x_0, null, 0), 0, 0)
                ]
            """.trimIndent()
        ) { it.regularFiles()[0].declarations }
    }
}
