/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.main

import io.verik.compiler.common.TextFile
import org.jetbrains.kotlin.psi.KtFile

/**
 * Context that stores the input [textFiles] and the parsed [ktFiles] for each source set.
 */
class SourceSetContext(val name: String, val textFiles: List<TextFile>) {

    lateinit var ktFiles: List<KtFile>
}
