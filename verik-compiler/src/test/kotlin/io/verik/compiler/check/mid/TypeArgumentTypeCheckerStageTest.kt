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

internal class TypeArgumentTypeCheckerStageTest : BaseTest() {

    @Test
    fun `cardinal type expected`() {
        driveMessageTest(
            """
                var x: Ubit<ADD<`8`, Int>> = u(0)
            """.trimIndent(),
            true,
            "Cardinal type expected but found: Int"
        )
    }

    @Test
    fun `cardinal type expected type parameter`() {
        driveMessageTest(
            """
                class C<N> : Class() {
                    var x: Ubit<INC<N>> = u(0)
                }
            """.trimIndent(),
            true,
            "Cardinal type expected but found: N"
        )
    }
}
