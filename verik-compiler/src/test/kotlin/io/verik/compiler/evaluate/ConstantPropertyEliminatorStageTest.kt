/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.evaluate

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import io.verik.compiler.test.findStatements
import org.junit.jupiter.api.Test

internal class ConstantPropertyEliminatorStageTest : BaseTest() {

    @Test
    fun `eliminate property`() {
        driveElementTest(
            """
                class C : Class() {
                    val x = 0
                }
            """.trimIndent(),
            ConstantPropertyEliminatorStage::class,
            "KtClass(C, C, Class, [], [], PrimaryConstructor(*), 0, 0)"
        ) { it.findDeclaration("C") }
    }

    @Test
    fun `eliminate property statement`() {
        driveElementTest(
            """
                fun f() {
                    val x = 0
                }
            """.trimIndent(),
            ConstantPropertyEliminatorStage::class,
            "[]"
        ) { it.findStatements("f") }
    }
}
