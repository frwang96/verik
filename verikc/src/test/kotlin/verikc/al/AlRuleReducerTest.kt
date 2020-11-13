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

package verikc.al

import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.LineException

internal class AlRuleReducerTest {

    @Test
    fun `primary constructor reduce`() {
        assertThrowsMessage<LineException>("\"constructor\" keyword is not permitted in primary constructor") {
            AlRuleParser.parseKotlinFile("""
                class c constructor(val x: Int)
            """.trimIndent())
        }
    }

    @Test
    fun `unary prefix annotation reduce`() {
        assertThrowsMessage<LineException>("annotations are not permitted here") {
            AlRuleParser.parseKotlinFile("""
                val x = @input 0
            """.trimIndent())
        }
    }

    @Test
    fun `function modifier reduce`() {
        assertThrowsMessage<LineException>("parser rule type \"functionModifier\" not supported") {
            AlRuleParser.parseKotlinFile("""
                inline fun f() {}
            """.trimIndent())
        }
    }
}

