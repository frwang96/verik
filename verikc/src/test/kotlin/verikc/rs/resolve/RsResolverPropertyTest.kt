/*
 * Copyright (c) 2020 Francis Wang
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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.rs.RsResolveUtil
import verikc.rs.ast.RsPrimaryProperty

internal class RsResolverPropertyTest {

    @Test
    fun `type bool`() {
        val string = "val x = _bool()"
        val primaryProperty = RsResolveUtil.resolveProperty("", string) as RsPrimaryProperty
        assertEquals(TYPE_BOOL, primaryProperty.typeSymbol)
    }

    @Test
    fun `with expression`() {
        val string = "val x = _bool() with {}"
        val primaryProperty = RsResolveUtil.resolveProperty("", string) as RsPrimaryProperty
        assertEquals(TYPE_BOOL, primaryProperty.typeSymbol)
    }
}
