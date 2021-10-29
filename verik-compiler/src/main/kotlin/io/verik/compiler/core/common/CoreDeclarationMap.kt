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

import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.common.Cardinal
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.impl.AbstractTypeAliasDescriptor
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

object CoreDeclarationMap {

    private val qualifiedSignatureMap = HashMap<QualifiedSignature, Declaration>()

    init {
        addCoreDeclarations(Core::class)
        qualifiedSignatureMap[
            QualifiedSignature("${CorePackage.VK.name}.Cardinal", "class Cardinal")
        ] = Cardinal.UNRESOLVED
        qualifiedSignatureMap[
            QualifiedSignature("${CorePackage.VK.name}.*", "typealias *")
        ] = Cardinal.UNRESOLVED
    }

    private fun addCoreDeclarations(kClass: KClass<*>) {
        val kClassInstance = kClass.objectInstance!!
        kClass.declaredMemberProperties.forEach {
            @Suppress("UNCHECKED_CAST")
            val coreDeclaration = (it as KProperty1<Any, *>).get(kClassInstance) as CoreDeclaration
            val qualifiedSignature = coreDeclaration.getQualifiedSignature()
            if (qualifiedSignature != null) {
                qualifiedSignatureMap[qualifiedSignature] = coreDeclaration
            }
        }
        kClass.nestedClasses.forEach { addCoreDeclarations(it) }
    }

    operator fun get(declarationDescriptor: DeclarationDescriptor): Declaration? {
        val qualifiedSignature = QualifiedSignature.get(declarationDescriptor)
        val coreDeclaration = qualifiedSignatureMap[qualifiedSignature]
        return when {
            coreDeclaration != null -> coreDeclaration
            declarationDescriptor is AbstractTypeAliasDescriptor -> {
                val name = declarationDescriptor.name.asString()
                val cardinal = name.toIntOrNull()
                if (cardinal != null) Cardinal.of(cardinal)
                else null
            }
            else -> null
        }
    }
}
