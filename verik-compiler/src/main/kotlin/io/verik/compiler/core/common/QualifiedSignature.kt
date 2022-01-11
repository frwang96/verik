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

import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation
import org.jetbrains.kotlin.builtins.isFunctionType
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.impl.AbstractClassDescriptor
import org.jetbrains.kotlin.descriptors.impl.AbstractTypeAliasDescriptor
import org.jetbrains.kotlin.resolve.calls.components.isVararg
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

data class QualifiedSignature(val qualifiedName: String, val signature: String) {

    companion object {

        fun get(declarationDescriptor: DeclarationDescriptor): QualifiedSignature {
            val fqNameSafe = declarationDescriptor.fqNameSafe
            val qualifiedName = if (fqNameSafe.shortName().asString() == "<init>") {
                "${fqNameSafe.parent().asString()}.${fqNameSafe.parent().shortName().asString()}"
            } else {
                fqNameSafe.asString()
            }
            return QualifiedSignature(qualifiedName, getSignature(declarationDescriptor.original))
        }

        private fun getSignature(declarationDescriptor: DeclarationDescriptor): String {
            return when (declarationDescriptor) {
                is AbstractTypeAliasDescriptor -> {
                    "typealias ${declarationDescriptor.name}"
                }
                is AbstractClassDescriptor -> {
                    "class ${declarationDescriptor.name}"
                }
                is ClassConstructorDescriptor -> {
                    val name = declarationDescriptor.containingDeclaration.name
                    "fun $name(): $name"
                }
                is SimpleFunctionDescriptor -> {
                    val name = declarationDescriptor.name
                    val valueParameters = declarationDescriptor.valueParameters.map {
                        when {
                            it.type.isFunctionType -> "Function"
                            it.isVararg -> "vararg " + it.varargElementType!!.constructor.declarationDescriptor!!.name
                            else -> it.type.constructor.declarationDescriptor!!.name
                        }
                    }
                    "fun $name(${valueParameters.joinToString()})"
                }
                is PropertyDescriptor -> {
                    "val ${declarationDescriptor.name}"
                }
                else -> {
                    val className = declarationDescriptor::class.simpleName
                    Messages.INTERNAL_ERROR.on(SourceLocation.NULL, "Unable to get declaration signature: $className")
                }
            }
        }
    }
}
