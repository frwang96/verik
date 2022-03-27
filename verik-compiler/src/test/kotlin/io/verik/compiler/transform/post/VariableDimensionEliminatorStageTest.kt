/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.post

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class VariableDimensionEliminatorStageTest : BaseTest() {

    @Test
    fun `typedef Unpacked`() {
        driveElementTest(
            """
                fun f(x: Unpacked<`8`, Boolean>): Unpacked<`8`, Boolean> {
                    return x
                }
            """.trimIndent(),
            VariableDimensionEliminatorStage::class,
            "TypeDefinition(<tmp>, Unpacked<`8`, Boolean>)"
        ) { it.findDeclaration("<tmp>") }
    }
}
