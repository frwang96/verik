/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class SafeAccessReducerStageTest : BaseTest() {

    @Test
    fun `transform statement`() {
        driveElementTest(
            """
                class C : Class() {
                    fun f() {}
                }
                val c: C? = nc()
                fun g() {
                    c?.f()
                }
            """.trimIndent(),
            SafeAccessReducerStage::class,
            """
                BlockExpression(Unit, [
                    PropertyStatement(Unit, Property(<tmp>, C, ReferenceExpression(C, c, null, 0), 0, 0)),
                    IfExpression(
                        Unit,
                        KtBinaryExpression(
                            Boolean, ReferenceExpression(C, <tmp>, null, 0),
                            ConstantExpression(Nothing, null), EXCL_EQ
                        ),
                        BlockExpression(
                            Unit, [CallExpression(Unit, f, ReferenceExpression(C, <tmp>, null, 0), 0, [], [])]
                        ),
                        null
                    )
                ])
            """.trimIndent()
        ) { it.findExpression("g") }
    }
}
