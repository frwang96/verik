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

package io.verik.compiler.interpret

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class ClusterInterpreterStageTest : BaseTest() {

    @Test
    fun `interpret cluster property`() {
        driveElementTest(
            """
                val x = cluster(2) { it }
            """.trimIndent(),
            ClusterInterpreterStage::class,
            """
                Cluster(x, [
                    Property(x_0, Int, ConstantExpression(Int, 0), 0, 0),
                    Property(x_1, Int, ConstantExpression(Int, 1), 0, 0)
                ])
            """.trimIndent()
        ) { it.findDeclaration("x") }
    }
}
