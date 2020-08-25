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

package verik.core.kt.symbol

import verik.core.base.LineException
import verik.core.base.Symbol
import java.util.concurrent.ConcurrentHashMap

class KtResolutionTable {

    private val resolutionEntriesMap = ConcurrentHashMap<Symbol, List<KtResolutionEntry>>()

    fun addFile(file: Symbol, resolutionEntries: List<KtResolutionEntry>) {
        if (!file.isFileSymbol()) {
            throw LineException("file expected but got $file", 0)
        }
        if (resolutionEntriesMap[file] != null) {
            throw LineException("resolution entries of file $file have already been defined", 0)
        }
        resolutionEntriesMap[file] = resolutionEntries
    }

    fun addScope(scope: Symbol, parent: Symbol, line: Int) {
        if (resolutionEntriesMap[scope] != null) {
            throw LineException("resolution entries of scope $scope have already been defined", line)
        }
        val parentResolutionEntries = resolutionEntries(parent, line)
        resolutionEntriesMap[scope] = listOf(KtResolutionEntry(listOf(scope))) + parentResolutionEntries
    }

    fun resolutionEntries(scope: Symbol, line: Int): List<KtResolutionEntry> {
        return resolutionEntriesMap[scope]
                ?: throw LineException("resolution entries of scope $scope have not been defined", line)
    }
}