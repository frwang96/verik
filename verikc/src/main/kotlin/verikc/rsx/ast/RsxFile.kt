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

package verikc.rsx.ast

import verikc.base.config.FileConfig
import verikc.kt.ast.KtFile
import verikc.rsx.symbol.RsxResolutionEntry

data class RsxFile(
    val config: FileConfig,
    val importEntries: List<RsxImportEntry>,
    var resolutionEntries: List<RsxResolutionEntry>?,
    val types: List<RsxType>,
    val functions: List<RsxFunction>,
    val properties: List<RsxProperty>
) {

    constructor(file: KtFile): this(
        file.config,
        file.importEntries.map { RsxImportEntry(it) },
        null,
        file.types.map { RsxType(it) },
        file.functions.map { RsxFunction(it) },
        file.properties.map { RsxProperty(it) }
    )
}
