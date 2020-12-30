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

package verikc.kt.resolve

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.kt.KtResolveUtil
import verikc.kt.ast.KtFunction
import verikc.lang.LangSymbol

internal class KtResolverFunctionTest {

    @Test
    fun `function without return type`() {
        val string = "fun f() {}"
        val function = KtResolveUtil.resolveDeclaration("", string) as KtFunction
        assertEquals(LangSymbol.TYPE_UNIT, function.returnTypeSymbol)
    }
}
