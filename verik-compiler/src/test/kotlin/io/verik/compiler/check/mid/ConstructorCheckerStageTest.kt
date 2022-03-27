/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class ConstructorCheckerStageTest : BaseTest() {

    @Test
    fun `multiple constructors illegal`() {
        driveMessageTest(
            """
                class C() : Class() {
                    constructor(x: Int) : this()
                }
            """.trimIndent(),
            true,
            "Multiple constructors are not permitted"
        )
    }
}
