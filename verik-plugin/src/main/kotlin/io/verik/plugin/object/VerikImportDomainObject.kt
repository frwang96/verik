/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.`object`

import java.nio.file.Path

/**
 * Interface for domain object that configures the importer.
 */
interface VerikImportDomainObject {

    var importedFiles: List<Path>
    var includeDirs: List<Path>
    var enablePreprocessorOutput: Boolean
    var suppressedWarnings: List<String>
    var promotedWarnings: List<String>
}
