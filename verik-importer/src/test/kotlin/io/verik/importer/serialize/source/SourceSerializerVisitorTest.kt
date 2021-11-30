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

package io.verik.importer.serialize.source

import io.verik.importer.test.BaseTest
import org.junit.jupiter.api.Test

internal class SourceSerializerVisitorTest : BaseTest() {

    @Test
    fun `serialize module`() {
        driveTextFileTest(
            """
                module M;
                endmodule
            """.trimIndent(),
            """
                class M : Module()
            """.trimIndent()
        )
    }

    @Test
    fun `serialize property`() {
        driveTextFileTest(
            """
                logic x;
            """.trimIndent(),
            """
                val x: Boolean = imported()
            """.trimIndent()
        )
    }
}
