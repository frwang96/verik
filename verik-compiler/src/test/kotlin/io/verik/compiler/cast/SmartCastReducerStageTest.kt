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

package io.verik.compiler.cast

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.findExpression
import org.junit.jupiter.api.Test

internal class SmartCastReducerStageTest : BaseTest() {

    @Test
    fun `smart cast simple`() {
        driveTest(
            """
                open class C
                class D : C()
                fun f(d: D): Boolean { return false }
                fun g() {
                    val c: C = D()
                    if (c is D) {
                        val x = f(c)
                    }
                }
            """.trimIndent(),
            SmartCastReducerStage::class,
            "KtCallExpression(Boolean, f, null, [ReferenceExpression(D, <tmp>, null)], [])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `smart cast no receiver`() {
        driveTest(
            """
                open class C
                class D : C() { fun f(): Boolean { return false } }
                fun g() {
                    val c: C = D()
                    if (c is D) {
                        val x = c.f()
                    }
                }
            """.trimIndent(),
            SmartCastReducerStage::class,
            "KtCallExpression(Boolean, f, ReferenceExpression(D, <tmp>, null), [], [])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `smart cast with receiver`() {
        driveTest(
            """
                open class C
                class D : C() { fun f(): Boolean { return false } }
                class E { val c: C = D() }
                fun g() {
                    val e = E()
                    if (e.c is D) {
                        val x = e.c.f()
                    }
                }
            """.trimIndent(),
            SmartCastReducerStage::class,
            "KtCallExpression(Boolean, f, ReferenceExpression(D, <tmp>, null), [], [])"
        ) { it.findExpression("x") }
    }
}
