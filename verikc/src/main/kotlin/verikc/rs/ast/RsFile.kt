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

package verikc.rs.ast

import verikc.base.config.FileConfig
import verikc.kt.ast.KtFile
import verikc.rs.table.RsResolutionEntry

data class RsFile(
    val config: FileConfig,
    val importEntries: List<RsImportEntry>,
    var resolutionEntries: List<RsResolutionEntry>?,
    val types: List<RsType>,
    val functions: List<RsFunction>,
    val properties: List<RsProperty>
) {

    constructor(file: KtFile): this(
        file.config,
        file.importEntries.map { RsImportEntry(it) },
        null,
        file.types.map { RsType(it) },
        file.functions.map { RsFunction(it) },
        file.properties.map { RsProperty(it) }
    )
}
