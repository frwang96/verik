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

package verikc.lang.module

import org.junit.jupiter.api.Test
import verikc.lang.LangUtil

internal class LangModuleRandomTest {

    @Test
    fun `function random`() {
        LangUtil.check(
            "",
            "",
            "random()",
            "\$urandom();"
        )
    }

    @Test
    fun `function random int`() {
        LangUtil.check(
            "",
            "",
            "random(2)",
            "\$urandom_range(2 - 1);"
        )
    }
}