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

package verikc.rf.reify

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.rf.RfReifyUtil

internal class RfReifierPropertyTest {

    @Test
    fun `port bool`() {
        val string = """
            @input var x = _bool()
        """.trimIndent()
        assertEquals(
            TYPE_BOOL.toTypeReifiedInstance(),
            RfReifyUtil.reifyPort(string).typeReified
        )
    }
}
