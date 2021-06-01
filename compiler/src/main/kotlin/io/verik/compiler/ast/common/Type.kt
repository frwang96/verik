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

import io.verik.compiler.ast.descriptor.ClassDescriptor

class Type(
    val classDescriptor: ClassDescriptor,
    val arguments: ArrayList<Type>
) {

    fun isSubtypeOf(type: Type): Boolean {
        return getSupertypes().any { it == type }
    }

    override fun toString(): String {
        return if (arguments.isNotEmpty()) {
            "$classDescriptor<${arguments.joinToString()}>"
        } else "$classDescriptor"
    }

    override fun equals(other: Any?): Boolean {
        return (other is Type) && (other.classDescriptor == classDescriptor)
    }

    override fun hashCode(): Int {
        return classDescriptor.hashCode()
    }

    private fun getSupertypes(): List<Type> {
        val supertypes = ArrayList<Type>()
        var supertype = this
        while (supertype.classDescriptor.superclassDescriptor != null) {
            supertype = supertype.classDescriptor.superclassDescriptor!!.getDefaultType()
            supertypes.add(supertype)
        }
        return supertypes.reversed()
    }
}