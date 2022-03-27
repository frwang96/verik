/*
 * SPDX-License-Identifier: Apache-2.0
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

fun PsiElement.endLocation(): SourceLocation {
    val lineAndColumn = PsiDiagnosticUtils.offsetToLineAndColumn(
        containingFile.viewProvider.document,
        textRange.endOffset
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
