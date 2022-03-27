/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class EnumPropertyReferenceTransformerStageTest : BaseTest() {

    @Test
    fun `enum property reference`() {
        driveElementTest(
            """
                enum class E(val value: Ubit<`4`>) { A(u0()) }
                var x = E.A
                var y = x.value
            """.trimIndent(),
            EnumPropertyReferenceTransformerStage::class,
            "ReferenceExpression(Ubit<`4`>, x, null, 0)",
        ) { it.findExpression("y") }
    }
}
