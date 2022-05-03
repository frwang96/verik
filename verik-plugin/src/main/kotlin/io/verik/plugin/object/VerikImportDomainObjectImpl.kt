/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.`object`

import java.nio.file.Path

/**
 * Implementation for domain object that configures the importer.
 */
class VerikImportDomainObjectImpl : VerikImportDomainObject {

    override var importedFiles: List<Path> = listOf()
    override var includeDirs: List<Path> = listOf()
    override var enablePreprocessorOutput: Boolean = true
    override var suppressedWarnings: List<String> = listOf()
    override var promotedWarnings: List<String> = listOf()
}
