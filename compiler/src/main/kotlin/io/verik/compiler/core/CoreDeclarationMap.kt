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

package io.verik.compiler.core

import io.verik.compiler.ast.common.Name
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.impl.AbstractTypeAliasDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import kotlin.reflect.full.memberProperties

object CoreDeclarationMap {

    private val declarationMap = HashMap<Name, CoreDeclaration>()

    init {
        CoreClass::class.memberProperties.forEach {
            val property = it.get(CoreClass)
            if (property is CoreClassDeclaration)
                declarationMap[property.qualifiedName] = property
        }
        CoreCardinal::class.memberProperties.forEach {
            val property = it.get(CoreCardinal)
            if (property is CoreCardinalFunctionDeclaration)
                declarationMap[property.qualifiedName] = property
        }
    }

    operator fun get(declarationDescriptor: DeclarationDescriptor): CoreDeclaration? {
        val qualifiedName = declarationDescriptor.fqNameOrNull()
            ?.let { Name(it.toString()) }
            ?: return null
        val declaration = declarationMap[qualifiedName]
        return when {
            declaration != null -> declaration
            declarationDescriptor is AbstractTypeAliasDescriptor -> {
                val nameString = declarationDescriptor.name.toString()
                val cardinal = nameString.toIntOrNull()
                if (cardinal != null) {
                    CoreCardinalLiteralDeclaration(cardinal)
                } else null
            }
            else -> null
        }
    }
}