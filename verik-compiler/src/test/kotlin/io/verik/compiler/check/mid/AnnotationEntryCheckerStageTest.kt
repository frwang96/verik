/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class AnnotationEntryCheckerStageTest : BaseTest() {

    @Test
    fun `entry parameterized`() {
        driveMessageTest(
            """
                @Entry
                class C<T> : Class()
            """.trimIndent(),
            true,
            "Type parameters not permitted on entry points"
        )
    }

    @Test
    fun `entry invalid`() {
        driveMessageTest(
            """
                class C : Class() {
                    @Entry
                    fun f() {}
                }
            """.trimIndent(),
            true,
            "Invalid entry point"
        )
    }

    @Test
    fun `function annotations conflicting`() {
        driveMessageTest(
            """
                @Com
                @Seq
                fun f() {}
            """.trimIndent(),
            true,
            "Conflicting annotations: @Seq and @Com"
        )
    }

    @Test
    fun `property com not mutable`() {
        driveMessageTest(
            """
                @Suppress("MemberVisibilityCanBePrivate")
                class M : Module() {
                    var x: Boolean = nc()
                    @Com
                    val y = x
                }
            """.trimIndent(),
            true,
            "Property must be declared as var: y"
        )
    }

    @Test
    fun `value parameter annotations conflicting`() {
        driveMessageTest(
            """
                class M(@In @Out val x: Boolean) : Module()
            """.trimIndent(),
            true,
            "Conflicting annotations: @In and @Out"
        )
    }
}
