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

package io.verik.compiler.transform.pre

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.TestDriver
import io.verik.compiler.util.assertElementEquals
import org.junit.jupiter.api.Test

internal class KeywordTransformerTest: BaseTest() {

    @Test
    fun `keyword property`() {
        val projectContext = TestDriver.preTransform("""
            const val alias = false
        """.trimIndent())
        assertElementEquals(
            "File([BaseProperty(alias\$K, Boolean, ConstantExpression(BOOLEAN, 1'b0))])",
            projectContext.vkFiles.first()
        )
    }
}