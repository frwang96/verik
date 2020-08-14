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

package io.verik.core.symbol

data class Symbol(
        val pkg: Int,
        val file: Int,
        val declaration: Int
) {

    fun isPkgSymbol(): Boolean {
        return pkg != 0 && file == 0 && declaration == 0
    }

    fun isFileSymbol(): Boolean {
        return pkg != 0 && file != 0 && declaration == 0
    }

    fun isDeclarationSymbol(): Boolean {
        return pkg != 0 && file != 0 && declaration != 0
    }

    fun toPkgSymbol(): Symbol {
        return Symbol(pkg, 0, 0)
    }

    override fun toString(): String {
        return "($pkg, $file, $declaration)"
    }
}