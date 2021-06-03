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

package io.verik.compiler.cast

import io.verik.compiler.ast.common.Declaration
import io.verik.compiler.ast.common.NullDeclaration
import io.verik.compiler.ast.element.VkDeclaration
import io.verik.compiler.core.CoreDeclarationMap
import io.verik.compiler.main.m
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor

class DeclarationMap {

    private val declarationMap = HashMap<DeclarationDescriptor, VkDeclaration>()

    operator fun set(declarationDescriptor: DeclarationDescriptor, declaration: VkDeclaration) {
        declarationMap[declarationDescriptor] = declaration
    }

    operator fun get(declarationDescriptor: DeclarationDescriptor, element: PsiElement): Declaration {
        val declaration = declarationMap[declarationDescriptor]
            ?: CoreDeclarationMap[declarationDescriptor]
        return if (declaration == null) {
            val name = declarationDescriptor.name
            m.error("Could not identify declaration: $name", element)
            NullDeclaration
        } else declaration
    }
}