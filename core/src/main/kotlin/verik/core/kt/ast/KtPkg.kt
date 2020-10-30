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

package verik.core.kt.ast

import verik.core.base.Symbol

data class KtPkg(
        val pkg: Symbol,
        val files: List<KtFile>
) {

    fun file(file: Symbol): KtFile {
        if (!file.isFileSymbol()) {
            throw IllegalArgumentException("file expected but got $file")
        }
        return files.find { it.file == file }
                ?: throw IllegalArgumentException("could not find file $file in package $pkg")
    }
}