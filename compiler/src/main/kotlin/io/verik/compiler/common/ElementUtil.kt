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

package io.verik.compiler.common

import io.verik.compiler.ast.element.common.VkElement
import io.verik.compiler.main.SourceLocation
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils
import org.jetbrains.kotlin.psi.KtElement
import java.nio.file.Paths

fun KtElement.getSourceLocation(): SourceLocation {
    val lineAndColumn = PsiDiagnosticUtils.offsetToLineAndColumn(
        containingFile.viewProvider.document,
        textRange.startOffset
    )
    val path = Paths.get(containingFile.virtualFile.path)
    return SourceLocation(lineAndColumn.column, lineAndColumn.line, path)
}

fun <T : VkElement> ArrayList<T>.replaceIfContains(oldElement: T, newElement: T): Boolean {
    val index = indexOf(oldElement)
    return if (index != -1) {
        set(index, newElement)
        true
    } else {
        false
    }
}
