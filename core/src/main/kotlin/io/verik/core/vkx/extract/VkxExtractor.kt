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

package io.verik.core.vkx.extract

import io.verik.core.lang.Lang
import io.verik.core.main.Line
import io.verik.core.main.LineException
import io.verik.core.svx.SvxType
import io.verik.core.vkx.VkxType

object VkxExtractor {

    fun extractType(type: VkxType, line: Line): SvxType {
        return Lang.typeTable.extract(type)
                ?: throw LineException("could not extract type", line)
    }
}