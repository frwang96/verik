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

package io.verik.compiler.core.common

class CorePackage(
    override var name: String
) : CoreDeclaration {

    override val parent: CoreDeclaration? = null

    override val signature: String? = null

    companion object {

        val KT = CorePackage("kotlin")
        val KT_IO = CorePackage("kotlin.io")
        val KT_COLLECTIONS = CorePackage("kotlin.collections")
        val Kt_RANGES = CorePackage("kotlin.ranges")
        val JV = CorePackage("java")
        val JV_UTIL = CorePackage("java.util")
        val VK = CorePackage("io.verik.core")

        fun isReserved(name: String): Boolean {
            return name in listOf("verik", "io.verik.core")
        }
    }
}
