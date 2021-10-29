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

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedClassConstructorDescriptor
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedClassDescriptor
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedSimpleFunctionDescriptor
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedTypeAliasDescriptor

data class QualifiedSignature(val qualifiedName: String, val signature: String) {

    companion object {

        fun get(declarationDescriptor: DeclarationDescriptor): QualifiedSignature {
            val qualifiedName = declarationDescriptor.fqNameSafe.asString()
            return QualifiedSignature(qualifiedName, getSignature(declarationDescriptor.original))
        }

        private fun getSignature(declarationDescriptor: DeclarationDescriptor): String {
            return when (declarationDescriptor) {
                is DeserializedTypeAliasDescriptor -> {
                    "typealias ${declarationDescriptor.name}"
                }
                is DeserializedClassDescriptor -> {
                    "class ${declarationDescriptor.name}"
                }
                is DeserializedClassConstructorDescriptor -> {
                    val name = declarationDescriptor.containingDeclaration.name
                    "fun $name(): $name"
                }
                is DeserializedSimpleFunctionDescriptor -> {
                    val name = declarationDescriptor.name
                    "fun $name()"
                }
                else -> {
                    val className = declarationDescriptor::class.simpleName
                    throw IllegalArgumentException("Unable to get declaration signature: $className")
                }
            }
        }
    }
}
