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

package io.verik.compiler.ast.descriptor

import io.verik.compiler.ast.common.Name
import io.verik.compiler.main.messageCollector

class PackageDescriptor(
    override val name: Name
): DeclarationDescriptor() {

    fun serialize(): String {
        return if (this == ROOT) {
            messageCollector.error("Unable to serialize package descriptor", null)
            "pkg"
        } else {
            val names = name.name.split(".")
            names.joinToString(separator = "_", postfix = "_pkg")
        }
    }

    override fun equals(other: Any?): Boolean {
        return (other is PackageDescriptor) && (other.name == name)
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    companion object {

        val ROOT = PackageDescriptor(Name.ROOT)
    }
}