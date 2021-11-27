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

import io.verik.compiler.main.Platform
import io.verik.compiler.message.SourceLocation
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils

fun PsiElement.location(): SourceLocation {
    val lineAndColumn = PsiDiagnosticUtils.offsetToLineAndColumn(
        containingFile.viewProvider.document,
        textRange.startOffset
    )
    val path = Platform.getPathFromString(containingFile.virtualFile.path)
    return SourceLocation(path, lineAndColumn.line, lineAndColumn.column)
}

fun <T> ArrayList<T>.replaceIfContains(old: T, new: T): Boolean {
    val index = indexOf(old)
    return if (index != -1) {
        set(index, new)
        true
    } else {
        false
    }
}
