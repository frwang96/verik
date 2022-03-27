/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.common

class CorePackage(
    override var name: String
) : CoreDeclaration {

    override val parent: CoreDeclaration? = null

    override val signature: String = "package $name"

    companion object {

        val KT = CorePackage("kotlin")
        val KT_IO = CorePackage("kotlin.io")
        val KT_COLLECTIONS = CorePackage("kotlin.collections")
        val KT_TEXT = CorePackage("kotlin.text")
        val KT_RANGES = CorePackage("kotlin.ranges")
        val JV = CorePackage("java")
        val JV_UTIL = CorePackage("java.util")
        val VK = CorePackage("io.verik.core")
    }
}
