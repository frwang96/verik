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

package io.verik.compiler.main

import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils
import org.jetbrains.kotlin.psi.KtElement
import java.nio.file.Path
import java.nio.file.Paths

class MessageLocation(
    val column: Int,
    val line: Int,
    val path: Path
)

fun KtElement.getMessageLocation(): MessageLocation {
    val lineAndColumn = PsiDiagnosticUtils.offsetToLineAndColumn(
        containingFile.viewProvider.document,
        textRange.startOffset
    )
    val path = Paths.get(containingFile.virtualFile.path)
    return MessageLocation(lineAndColumn.column, lineAndColumn.line, path)
}