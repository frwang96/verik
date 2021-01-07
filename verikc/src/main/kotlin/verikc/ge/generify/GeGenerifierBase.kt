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

package verikc.ge.generify

import verikc.ge.ast.GeCompilationUnit
import verikc.ge.ast.GeFunction
import verikc.ge.ast.GeProperty
import verikc.ge.ast.GeType
import verikc.ge.table.GeSymbolTable

abstract class GeGenerifierBase {

    fun generify(compilationUnit: GeCompilationUnit, symbolTable: GeSymbolTable) {
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                file.types.forEach { generifyType(it, symbolTable) }
                file.functions.forEach { generifyFunction(it, symbolTable) }
                file.properties.forEach { generifyProperty(it, symbolTable) }
            }
        }
    }

    protected open fun generifyType(type: GeType, symbolTable: GeSymbolTable) {}

    protected open fun generifyFunction(function: GeFunction, symbolTable: GeSymbolTable) {}

    protected open fun generifyProperty(property: GeProperty, symbolTable: GeSymbolTable) {}
}
