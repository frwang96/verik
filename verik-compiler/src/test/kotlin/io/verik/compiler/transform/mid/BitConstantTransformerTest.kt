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

package io.verik.compiler.transform.mid

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.assertElementEquals
import io.verik.compiler.util.driveTest
import io.verik.compiler.util.findExpression
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class BitConstantTransformerTest : BaseTest() {

    @Test
    @Disabled
    // TODO resolve bit constant
    fun `constant decimal`() {
        val projectContext = driveTest(
            BitConstantTransformer::class,
            """
                var x = u(80)
            """.trimIndent()
        )
        assertElementEquals(
            "ConstantExpression(Ubit<`8`>, 8'h50)",
            projectContext.findExpression("x")
        )
    }

    @Test
    @Disabled
    // TODO resolve bit constant
    fun `constant hexadecimal`() {
        val projectContext = driveTest(
            BitConstantTransformer::class,
            """
                var x = u(0xffff)
            """.trimIndent()
        )
        assertElementEquals(
            "ConstantExpression(Ubit<`36`>, 36'h0_0000_ffff)",
            projectContext.findExpression("x")
        )
    }
}
