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

package verikc.rs.pass

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verikc.lang.LangSymbol.TYPE_MODULE
import verikc.rs.RsResolveUtil

internal class RsPassTypeTest {

    @Test
    fun `type parent`() {
        val string = "class M : Module()"
        val type = RsResolveUtil.resolveType("", string)
        Assertions.assertEquals(
            TYPE_MODULE.toTypeGenerified(),
            type.typeParent.typeGenerified
        )
    }
}