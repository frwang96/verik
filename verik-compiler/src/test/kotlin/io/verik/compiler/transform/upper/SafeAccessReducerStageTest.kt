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

package io.verik.compiler.transform.upper

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findExpression
import org.junit.jupiter.api.Test

internal class SafeAccessReducerStageTest : BaseTest() {

    @Test
    fun `transform statement`() {
        driveElementTest(
            """
                class C : Class() {
                    fun f() {}
                }
                val c: C? = nc()
                fun g() {
                    c?.f()
                }
            """.trimIndent(),
            SafeAccessReducerStage::class,
            """
                BlockExpression(Unit, [
                    PropertyStatement(Unit, Property(<tmp>, C, ReferenceExpression(C, c, null, 0), 0, 0)),
                    IfExpression(
                        Unit,
                        KtBinaryExpression(
                            Boolean, ReferenceExpression(C, <tmp>, null, 0),
                            ConstantExpression(Nothing, null), EXCL_EQ
                        ),
                        BlockExpression(
                            Unit, [CallExpression(Unit, f, ReferenceExpression(C, <tmp>, null, 0), 0, [], [])]
                        ),
                        null
                    )
                ])
            """.trimIndent()
        ) { it.findExpression("g") }
    }
}
