/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.compiler.specialize

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.kt.EKtAbstractFunction
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.message.Messages

object SpecializerIndexer {

    fun index(declaration: EDeclaration): List<DeclarationBinding> {
        val specializerIndexerVisitor = SpecializerIndexerVisitor()
        declaration.accept(specializerIndexerVisitor)
        return specializerIndexerVisitor.declarationBindings
    }

    private class SpecializerIndexerVisitor : TreeVisitor() {

        val declarationBindings = ArrayList<DeclarationBinding>()

        private fun isTopLevel(reference: EDeclaration): Boolean {
            return reference.parent is EFile || reference is EPrimaryConstructor
        }

        override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
            super.visitReferenceExpression(referenceExpression)
            if (referenceExpression.receiver == null) {
                val reference = referenceExpression.reference
                if (reference is EDeclaration && isTopLevel(reference)) {
                    declarationBindings.add(DeclarationBinding(reference, listOf()))
                }
            }
        }

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            if (callExpression.receiver == null) {
                val reference = callExpression.reference
                if (reference is EKtAbstractFunction && isTopLevel(reference)) {
                    if (callExpression.typeArguments.any { !it.isResolved() }) {
                        Messages.UNRESOLVED_CALL_EXPRESSION_TYPE_ARGUMENTS.on(callExpression, reference.name)
                        return
                    }
                    val typeParameterBindings = reference.typeParameters
                        .zip(callExpression.typeArguments)
                        .map { (typeParameter, typeArgument) ->
                            TypeParameterBinding(typeParameter, typeArgument)
                        }
                    val declarationBinding = if (reference is EPrimaryConstructor) {
                        DeclarationBinding(reference.parentNotNull().cast(), typeParameterBindings)
                    } else {
                        DeclarationBinding(reference, typeParameterBindings)
                    }
                    declarationBindings.add(declarationBinding)
                }
            }
        }
    }
}
