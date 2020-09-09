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

package verik.core.it.check

import verik.core.it.ItComponentInstance
import verik.core.it.ItFile
import verik.core.it.ItModule
import verik.core.it.symbol.ItSymbolTable

object ItPortChecker {

    fun check(file: ItFile, symbolTable: ItSymbolTable) {
        file.declarations.forEach {
            if (it is ItModule) {
                it.componentInstances.forEach { componentInstance ->
                    checkComponentInstance(componentInstance, symbolTable)
                }
            }
        }
    }

    private fun checkComponentInstance(componentInstance: ItComponentInstance, symbolTable: ItSymbolTable) {
        // TODO check ports
    }
}