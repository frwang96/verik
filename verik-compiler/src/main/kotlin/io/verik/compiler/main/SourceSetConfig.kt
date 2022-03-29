/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.main

import java.nio.file.Path

/**
 * Configuration that lists the [files] included in each source set.
 */
data class SourceSetConfig(val name: String, val files: List<Path>)
