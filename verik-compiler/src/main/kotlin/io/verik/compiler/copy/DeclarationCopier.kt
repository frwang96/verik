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

package io.verik.compiler.copy

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.message.Messages

object DeclarationCopier {

    fun <D : EDeclaration> copyDeclaration(declaration: D, copyContext: CopyContext): D {
        val copiedDeclaration = when (declaration) {
            is EKtBasicClass -> copyKtBasicClass(declaration, copyContext)
            else -> {
                Messages.INTERNAL_ERROR.on(declaration, "Unable to copy declaration: $declaration")
                declaration
            }
        }
        @Suppress("UNCHECKED_CAST")
        return copiedDeclaration as D
    }

    private fun copyKtBasicClass(basicClass: EKtBasicClass, copyContext: CopyContext): EKtBasicClass {
        val copiedBasicClass = copyContext.referenceForwardingMap.getAsDeclaration(basicClass)
            ?: return basicClass

        val superType = copyContext.copy(basicClass.supertype)
        val typeParameters = basicClass.typeParameters.map { copyContext.copy(it) }
        val declarations = basicClass.declarations.map { copyContext.copy(it) }
        val annotations = basicClass.annotations.map { copyContext.copy(it) }
        val primaryConstructor = basicClass.primaryConstructor?.let { copyContext.copy(it) }

        copiedBasicClass.init(
            superType,
            typeParameters,
            declarations,
            annotations,
            basicClass.isEnum,
            primaryConstructor
        )
        return copiedBasicClass
    }
}
