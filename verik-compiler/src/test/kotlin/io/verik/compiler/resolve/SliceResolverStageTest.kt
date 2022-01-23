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

internal class SliceResolverStageTest : BaseTest() {

    @Test
    fun `slice constant`() {
        driveElementTest(
            """
                var x = u(0x00)
                var y = x[1, 0]
            """.trimIndent(),
            SliceResolverStage::class,
            "CallExpression(Ubit<`*`>, get, ReferenceExpression(*), [*], [`2`])"
        ) { it.findExpression("y") }
    }

    @Test
    fun `slice illegal`() {
        driveMessageTest(
            """
                val x = u(0x00)
                var y = 0
                val z = x[y, 0]
            """.trimIndent(),
            true,
            "Unable to determine width of slice"
        )
    }
}
