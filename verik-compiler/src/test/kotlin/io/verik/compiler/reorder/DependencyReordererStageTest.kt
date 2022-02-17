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

package io.verik.compiler.reorder

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class DependencyReordererStageTest : BaseTest() {

    @Test
    fun `package dependency illegal root`() {
        driveMessageTest(
            """
                object M : Module() { var x: Boolean = nc() }
                class C : Class() {
                    fun f() {
                        println(M.x)
                    }
                }
            """.trimIndent(),
            true,
            "Illegal package dependency: From test to <root>"
        )
    }

    @Test
    fun `declaration dependency simple`() {
        driveElementTest(
            """
                class B(val a: A) : Struct()
                class A(val x: Boolean) : Struct()
            """.trimIndent(),
            DependencyReordererStage::class,
            "File([Struct(A, A, [*]), Struct(B, B, [*])])"
        ) { it.files().first() }
    }

    @Test
    fun `declaration dependency circular`() {
        driveMessageTest(
            """
                class A(val b: B) : Struct()
                class B(val b: A) : Struct()
            """.trimIndent(),
            true,
            "Circular dependency between declarations: From A to B"
        )
    }

    @Test
    fun `class dependency not reordered`() {
        driveElementTest(
            """
                class B(val a: A) : Class()
                class A(val x: Boolean) : Class()
            """.trimIndent(),
            DependencyReordererStage::class,
            "File([SvClass(B, B, Class, *, *, *), SvClass(A, A, Class, *, *, *)])"
        ) { it.files().first() }
    }
}
