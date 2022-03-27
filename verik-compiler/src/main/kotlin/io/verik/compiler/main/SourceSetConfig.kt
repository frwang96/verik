/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.main

import java.nio.file.Path

data class SourceSetConfig(val name: String, val files: List<Path>)
