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

package io.verik.importer.preprocess

import io.verik.importer.main.ImporterContext
import io.verik.importer.main.ImporterStage

object PreprocessorFilterStage : ImporterStage() {

    override fun process(importerContext: ImporterContext) {
        val preprocessorFragments = importerContext.preprocessorFragments.filter {
            !isCommentOrWhitespace(it)
        }
        importerContext.preprocessorFragments = ArrayList(preprocessorFragments)
    }

    private fun isCommentOrWhitespace(preprocessorFragment: PreprocessorFragment): Boolean {
        val content = preprocessorFragment.content
        return when {
            content.startsWith("/*") -> true
            content.startsWith("//") -> true
            content.isBlank() -> true
            else -> false
        }
    }
}
