/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class ClassDeclarationCheckerStageTest : BaseTest() {

    @Test
    fun `module in class`() {
        driveMessageTest(
            """
                class C : Class() {
                    class M : Module()
                }
            """.trimIndent(),
            true,
            "Declaration is not permitted in class"
        )
    }

    @Test
    fun `class in struct`() {
        driveMessageTest(
            """
                class S : Struct() {
                    class C : Class()
                }
            """.trimIndent(),
            true,
            "Declaration is not permitted in struct"
        )
    }
}
