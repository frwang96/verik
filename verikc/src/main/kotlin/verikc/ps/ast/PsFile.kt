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

package verikc.ps.ast

import verikc.base.config.FileConfig
import verikc.vk.ast.VkFile

data class PsFile(
    val config: FileConfig,
    val components: List<PsComponent>,
    val primaryProperties: List<PsPrimaryProperty>,
    val enums: List<PsEnum>,
    val clses: List<PsCls>
) {

    constructor(file: VkFile): this(
        file.config,
        file.components.map { PsComponent(it) },
        file.primaryProperties.map { PsPrimaryProperty(it) },
        file.enums.map { PsEnum(it) },
        file.clses.map { PsCls(it) }
    )
}
