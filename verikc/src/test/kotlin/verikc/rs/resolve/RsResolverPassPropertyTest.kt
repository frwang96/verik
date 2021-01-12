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

package verikc.rs.resolve

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import verikc.lang.LangSymbol.TYPE_ARRAY
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_INT
import verikc.rs.RsResolveUtil

internal class RsResolverPassPropertyTest {

    @Test
    fun `property bool`() {
        val string = "val x = _bool()"
        Assertions.assertEquals(
            TYPE_BOOL.toTypeGenerified(),
            RsResolveUtil.resolveProperty("", string).typeGenerified
        )
    }

    @Test
    fun `property array bool`() {
        val string = """
            var x = _array(8, _bool())
        """.trimIndent()
        Assertions.assertEquals(
            TYPE_ARRAY.toTypeGenerified(8, TYPE_BOOL.toTypeGenerified()),
            RsResolveUtil.resolveProperty("", string).typeGenerified
        )
    }

    @Test
    @Disabled
    fun `property multiple passes`() {
        val fileContext = """
            val x = y
            val y = 0
        """.trimIndent()
        val string = """
            var z = x
        """.trimIndent()
        Assertions.assertEquals(
            TYPE_INT.toTypeGenerified(),
            RsResolveUtil.resolveProperty(fileContext, string).typeGenerified
        )
    }
}