/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin

import java.nio.file.Path

abstract class VerikImporterPluginExtension {

    var importedFiles: List<Path> = listOf()
    var includeDirs: List<Path> = listOf()
    var enablePreprocessorOutput: Boolean = true
    var suppressedWarnings: ArrayList<String> = ArrayList()
    var promotedWarnings: ArrayList<String> = ArrayList()
    var maxErrorCount: Int = 60
    var debug: Boolean = false
}
