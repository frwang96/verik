/*
 * Copyright 2020 Francis Wang
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

package io.verik.core.svx

import io.verik.core.assertStringEquals
import io.verik.core.main.SourceBuilder
import org.junit.jupiter.api.Test

internal class SvxActionBlockTest {

    @Test
    fun `initial action block empty`() {
        val actionBlock = SvxActionBlock(
                0,
                SvxActionBlockType.INITIAL
        )
        val builder = SourceBuilder()
        actionBlock.build(builder)
        val expected = """
            initial begin
            end
        """.trimIndent()
        assertStringEquals(expected, builder)
    }
}