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

import io.verik.compiler.ast.descriptor.CardinalDescriptor
import io.verik.compiler.ast.descriptor.ClassDescriptor
import io.verik.compiler.ast.descriptor.ClassifierDescriptor
import io.verik.compiler.core.CoreClass
import io.verik.compiler.main.messageCollector

class Type(
    val classifierDescriptor: ClassifierDescriptor,
    val arguments: ArrayList<Type>
) {

    fun isSubtypeOf(type: Type): Boolean {
        return getSupertypes().any { it == type }
    }

    override fun toString(): String {
        return if (arguments.isNotEmpty()) {
            "$classifierDescriptor<${arguments.joinToString()}>"
        } else "$classifierDescriptor"
    }

    override fun equals(other: Any?): Boolean {
        return (other is Type)
                && (other.classifierDescriptor == classifierDescriptor)
                && (other.arguments == arguments)
    }

    override fun hashCode(): Int {
        var result = classifierDescriptor.hashCode()
        result = 31 * result + arguments.hashCode()
        return result
    }

    private fun getSupertypes(): List<Type> {
        return when (classifierDescriptor) {
            is ClassDescriptor -> {
                val supertypes = ArrayList<Type>()
                var classDescriptor: ClassDescriptor? = this.classifierDescriptor
                while (classDescriptor != null) {
                    supertypes.add(classDescriptor.getDefaultType())
                    classDescriptor = classDescriptor.superclassDescriptor
                }
                supertypes.reversed()
            }
            is CardinalDescriptor -> {
                listOf(CoreClass.CARDINAL.getDefaultType())
            }
            else -> messageCollector.fatal("Classifier descriptor not recognized", null)
        }
    }
}