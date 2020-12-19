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

package verikc.lang.modules

import org.junit.jupiter.api.Test
import verikc.assertStringEquals

internal class LangModuleFunctionsNativeTest {

    @Test
    fun `function native not bool`() {
        val string = "!a"
        val expected = "!a;"
        assertStringEquals(expected, LangModuleUtil.buildExpressionWithContext(string))
    }

    @Test
    fun `function native add int int`() {
        val string = "1 + 1"
        val expected = "1 + 1;"
        assertStringEquals(expected, LangModuleUtil.buildExpressionWithContext(string))
    }

    @Test
    fun `function native add ubit ubit`() {
        val string = "x + y"
        val expected = "x + y;"
        assertStringEquals(expected, LangModuleUtil.buildExpressionWithContext(string))
    }

    @Test
    fun `function native equality instance instance`() {
        val string = "x == y"
        val expected = "x == y;"
        assertStringEquals(expected, LangModuleUtil.buildExpressionWithContext(string))
    }
}
