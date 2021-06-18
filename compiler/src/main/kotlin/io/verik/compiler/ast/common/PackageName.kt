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

package io.verik.compiler.ast.common

import io.verik.compiler.main.m

class PackageName(val name: String) {

    fun serialize(): String {
        return if (this == ROOT) {
            m.error("Invalid package name: $this", null)
            "pkg"
        } else {
            val names = name.split(".")
            names.joinToString(separator = "_", postfix = "_pkg")
        }
    }

    fun isReserved(): Boolean {
        return name == "io.verik" || name.startsWith("io.verik.")
    }

    override fun toString(): String {
        return if (this == ROOT) "<root>" else name
    }

    override fun equals(other: Any?): Boolean {
        return (other is PackageName) && (other.name == name)
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    companion object {

        val ROOT = PackageName("")
        val KOTLIN = PackageName("kotlin")
        val CORE = PackageName("io.verik.core")
        val SV = PackageName("io.verik.sv")
    }
}