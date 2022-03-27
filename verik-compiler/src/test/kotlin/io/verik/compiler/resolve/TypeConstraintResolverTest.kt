/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.resolve

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class TypeConstraintResolverTest : BaseTest() {

    @Test
    fun `equals violation assignment left`() {
        driveMessageTest(
            """
                var x = u(0x00)
                fun f() {
                    x = u(0)
                }
            """.trimIndent(),
            true,
            "Type mismatch: Expected Ubit<`8`> actual Ubit<`1`>"
        )
    }

    @Test
    fun `equals violation assignment right`() {
        driveMessageTest(
            """
                var x = u(0x00)
                fun f() {
                    val y: Ubit<`1`> = x
                }
            """.trimIndent(),
            true,
            "Type mismatch: Expected Ubit<`1`> actual Ubit<`8`>"
        )
    }

    @Test
    fun `unary violation`() {
        driveMessageTest(
            """
                var x: Ubit<`4`> = nc()
                fun f() {
                    x = u<`1`>()
                }
            """.trimIndent(),
            true,
            "Type mismatch: Expected Ubit<`4`> actual Ubit<`1`>"
        )
    }

    @Test
    fun `binary violation`() {
        driveMessageTest(
            """
                var x: Ubit<`4`> = nc()
                fun f() {
                    x = u(0) + u(0)
                }
            """.trimIndent(),
            true,
            "Type mismatch: Expected Ubit<`4`> actual Ubit<`1`>"
        )
    }

    @Test
    fun `constant one violation`() {
        driveMessageTest(
            """
                var x: Ubit<`8`> = u(0b0)
            """.trimIndent(),
            true,
            "Type mismatch: Expected Ubit<`8`> actual Ubit<`1`>"
        )
    }

    @Test
    fun `concatenation violation`() {
        driveMessageTest(
            """
                var x: Ubit<`1`> = cat(false, false)
            """.trimIndent(),
            true,
            "Type mismatch: Expected Ubit<`1`> actual Ubit<`2`>"
        )
    }

    @Test
    fun `replication violation`() {
        driveMessageTest(
            """
                var x: Ubit<`1`> = rep<`3`>(false)
            """.trimIndent(),
            true,
            "Type mismatch: Expected Ubit<`1`> actual Ubit<`3`>"
        )
    }

    @Test
    fun `extension violation`() {
        driveMessageTest(
            """
                var x: Ubit<`8`> = nc()
                fun f() {
                    println(x.ext<`4`>())
                }
            """.trimIndent(),
            true,
            "Unable to extend from Ubit<`8`> to Ubit<`4`>"
        )
    }

    @Test
    fun `truncation violation`() {
        driveMessageTest(
            """
                var x: Ubit<`4`> = nc()
                fun f() {
                    println(x.tru<`8`>())
                }
            """.trimIndent(),
            true,
            "Unable to truncate from Ubit<`4`> to Ubit<`8`>"
        )
    }
}
