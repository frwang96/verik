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

package io.verik.compiler.ast

class VkType(
    val name: Name,
    val packageName: Name,
    val supertype: VkType?
) {

    fun getSupertypes(): List<VkType> {
        val supertypes = ArrayList<VkType>()
        var supertype = this
        while (supertype.supertype != null) {
            supertype = supertype.supertype!!
            supertypes.add(supertype)
        }
        return supertypes.reversed()
    }

    fun isSubtypeOf(type: VkType): Boolean {
        return getSupertypes().any { it == type }
    }

    override fun toString() = packageName.name + "." + name.name

    override fun equals(other: Any?): Boolean {
        return (other is VkType) && (other.name == name) && (other.packageName == packageName)
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + packageName.hashCode()
        return result
    }
}