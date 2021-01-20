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

package verikc.lang.module

import org.junit.jupiter.api.Test
import verikc.lang.LangUtil

internal class LangModuleArrayTest {

    @Test
    fun `function native get array int`() {
        LangUtil.check(
            "",
            "val a = _array(8, _bool())",
            "a[0]",
            "a[0];"
        )
    }

    @Test
    fun `function native get array ubit`() {
        LangUtil.check(
            "",
            "val a = _array(8, _bool())",
            "a[u(3, 0)]",
            "a[3'h0];"
        )
    }
}