/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.compiler.resolve

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class ExpressionReferenceForwarderTest : BaseTest() {

    @Test
    fun `forward property parameterized`() {
        driveElementTest(
            """
                @Suppress("MemberVisibilityCanBePrivate")
                class C<X : `*`> {
                    var x: Ubit<X> = nc()
                    var y = x
                }
                val c = C<`8`>()
            """.trimIndent(),
            TypeResolverStage::class,
            "ReferenceExpression(Ubit<`8`>, x, null)"
        ) { it.findExpression("y") }
    }

    @Test
    fun `forward function parameterized`() {
        driveElementTest(
            """
                @Suppress("MemberVisibilityCanBePrivate")
                class C<X : `*`> {
                    fun f(): Boolean { return false }
                    var x = f()
                }
                val c = C<`8`>()
            """.trimIndent(),
            TypeResolverStage::class,
            "CallExpression(Boolean, f, null, [], [])"
        ) { it.findExpression("x") }
    }

    @Test
    fun `forward function not parameterized`() {
        driveElementTest(
            """
                class D
                class C<X : `*`> {
                    var x = D()
                }
                val c = C<`8`>()
            """.trimIndent(),
            TypeResolverStage::class,
            "CallExpression(D, D, null, [], [])"
        ) { it.findExpression("x") }
    }
}
