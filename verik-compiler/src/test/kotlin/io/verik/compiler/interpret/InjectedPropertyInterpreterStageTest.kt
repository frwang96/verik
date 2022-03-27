/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class InjectedPropertyInterpreterStageTest : BaseTest() {

    @Test
    fun `injected property simple`() {
        driveElementTest(
            """
                @Inj
                val x = "abc"
            """.trimIndent(),
            InjectedPropertyInterpreterStage::class,
            "InjectedProperty(x, InjectedExpression(Void, [abc]))"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `injected property multiline`() {
        driveElementTest(
            """
                @Inj
                val x = ${"\"\"\""}
                    abc
                ${"\"\"\""}.trimIndent()
            """.trimIndent(),
            InjectedPropertyInterpreterStage::class,
            "InjectedProperty(x, InjectedExpression(Void, [abc]))"
        ) { it.findDeclaration("x") }
    }
}
