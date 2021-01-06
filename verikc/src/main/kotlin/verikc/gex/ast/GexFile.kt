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

package verikc.gex.ast

import verikc.base.config.FileConfig
import verikc.rs.ast.RsFile

data class GexFile(
    val config: FileConfig,
    val types: List<GexType>,
    val functions: List<GexFunction>,
    val properties: List<GexProperty>
) {

    constructor(file: RsFile): this(
        file.config,
        file.types.map { GexType(it) },
        file.functions.map { GexFunction(it) },
        file.properties.map { GexProperty(it) }
    )
}
