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

package io.verik.core.kt

import io.verik.core.LinePosException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class KtRuleReducerTest {

    @Test
    fun `primary constructor reduce`() {
        val exception = assertThrows<LinePosException> {
            KtRuleParser.parseKotlinFile("""
                class c constructor(val x: Int)
            """.trimIndent())
        }
        assertEquals("\"constructor\" keyword is not permitted in primary constructor", exception.message)
    }

    @Test
    fun `unary prefix annotation reduce`() {
        val exception = assertThrows<LinePosException> {
            KtRuleParser.parseKotlinFile("""
                val x = @input 0
            """.trimIndent())
        }
        assertEquals("annotations are not permitted here", exception.message)
    }

    @Test
    fun `function modifier reduce`() {
        val exception = assertThrows<LinePosException> {
            KtRuleParser.parseKotlinFile("""
                inline fun f() {}
            """.trimIndent())
        }
        assertEquals("parser rule type \"functionModifier\" is not supported", exception.message)
    }
}

