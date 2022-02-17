/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
            "Combinational assignment must be declared as var: y"
        )
    }

    @Test
    fun `property seq not mutable`() {
        driveMessageTest(
            """
                @Suppress("MemberVisibilityCanBePrivate")
                class M : Module() {
                    var x: Boolean = nc()
                    @Seq
                    val y = on(posedge(false)) { x }
                }
            """.trimIndent(),
            true,
            "Sequential assignment must be declared as var: y"
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
