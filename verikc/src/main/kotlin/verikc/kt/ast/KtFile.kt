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

package verikc.kt.ast

import verikc.al.ast.AlFile
import verikc.base.config.FileConfig
import verikc.base.symbol.SymbolContext
import verikc.kt.parse.KtParserFile

data class KtFile(
    val config: FileConfig,
    val importEntries: List<KtImportEntry>,
    val types: List<KtType>,
    val typeAliases: List<KtTypeAlias>,
    val functions: List<KtFunction>,
    val properties: List<KtProperty>
) {

    companion object {

        operator fun invoke(file: AlFile, topIdentifier: String?, symbolContext: SymbolContext): KtFile {
            return KtParserFile.parse(file, topIdentifier, symbolContext)
        }
    }
}
