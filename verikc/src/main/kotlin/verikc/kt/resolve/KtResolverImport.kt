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

package verikc.kt.resolve

import verikc.kt.ast.KtCompilationUnit
import verikc.kt.symbol.KtResolutionEntry
import verikc.lang.LangSymbol.SCOPE_LANG

object KtResolverImport {

    fun resolve(compilationUnit: KtCompilationUnit) {
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                // TODO resolve imports
                val resolutionEntries = listOf(
                    KtResolutionEntry(pkg.config.fileConfigs.map { it.symbol }),
                    KtResolutionEntry(listOf(SCOPE_LANG))
                )
                file.resolutionEntries = resolutionEntries
            }
        }
    }
}