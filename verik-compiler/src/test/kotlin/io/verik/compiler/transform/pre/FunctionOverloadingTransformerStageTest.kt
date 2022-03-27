/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.pre

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class FunctionOverloadingTransformerStageTest : BaseTest() {

    @Test
    fun `overloaded function`() {
        driveElementTest(
            """
                fun f() {}
                fun f(x: Int) {}
            """.trimIndent(),
            FunctionOverloadingTransformerStage::class,
            "KtFunction(f_Int, Unit, BlockExpression(*), *, [], 0)"
        ) { it.findDeclaration("f_Int") }
    }
}
