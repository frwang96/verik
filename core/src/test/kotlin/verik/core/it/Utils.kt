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

package verik.core.it

import verik.core.al.AlRule
import verik.core.kt.parseDeclaration
import verik.core.kt.parseFile
import verik.core.sv.SvFile
import verik.core.sv.SvModule
import verik.core.vk.VkFile
import verik.core.vk.VkModule

fun extractModule(rule: AlRule): SvModule {
    return ItModule(VkModule(parseDeclaration(rule))).extract()
}

fun extractFile(rule: AlRule): SvFile {
    return ItFile(VkFile(parseFile(rule))).extract()
}