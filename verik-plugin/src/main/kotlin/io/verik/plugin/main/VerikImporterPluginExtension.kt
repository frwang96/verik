/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.main

import java.nio.file.Path

/**
 * Plugin extension class that is used to configure the importer.
 */
abstract class VerikImporterPluginExtension {

    var importedFiles: List<Path> = listOf()
    var includeDirs: List<Path> = listOf()
    var enablePreprocessorOutput: Boolean = true
    var suppressedWarnings: ArrayList<String> = ArrayList()
    var promotedWarnings: ArrayList<String> = ArrayList()
    var maxErrorCount: Int = 60
    var debug: Boolean = false
}
