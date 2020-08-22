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

class KtScopeResolutionTable {

    private val parentMap = ConcurrentHashMap<Symbol, Symbol>()
    private val fileResolutionEntriesMap = ConcurrentHashMap<Symbol, List<Symbol>>()

    fun addFile(file: Symbol, resolutionEntries: List<Symbol>) {
        if (!file.isFileSymbol()) {
            throw LineException("file expected but got $file", 0)
        }
        if (fileResolutionEntriesMap[file] != null) {
            throw LineException("resolution entries of file $file have already been defined", 0)
        }
        resolutionEntries.forEach {
            if (!it.isPkgSymbol()) {
                throw LineException("expected package for resolution entry but got $it", 0)
            }
        }
        fileResolutionEntriesMap[file] = resolutionEntries
    }

    fun addScope(scope: Symbol, parent: Symbol, line: Int) {
        if (parentMap[scope] != null) {
            throw LineException("parent of scope $scope has already been defined", line)
        }
        when {
            parent.isPkgSymbol() -> {
                throw LineException("parent of scope $scope cannot be package $parent", line)
            }
            parent.isFileSymbol() -> {
                getFileResolutionEntries(parent, line)
            }
            parent.isDeclarationSymbol() -> {
                getParent(parent, line)
            }
        }
        parentMap[scope] = parent
    }

    fun resolutionEntries(parent: Symbol, line: Int): List<Symbol> {
        val resolutionEntries = ArrayList<Symbol>()
        var parentWalk = parent
        while (parentWalk.isDeclarationSymbol()) {
            resolutionEntries.add(parentWalk)
            parentWalk = getParent(parentWalk, line)
        }
        resolutionEntries.addAll(getFileResolutionEntries(parentWalk, line))
        return resolutionEntries
    }

    private fun getParent(scope: Symbol, line: Int): Symbol {
        return parentMap[scope]
                ?: throw LineException("parent of scope $scope has not been defined", line)
    }

    private fun getFileResolutionEntries(file: Symbol, line: Int): List<Symbol> {
        return fileResolutionEntriesMap[file]
                ?: throw LineException("resolution entries of file $file have not been defined", line)
    }
}