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

package io.verik.compiler.transform.pre

import io.verik.compiler.ast.common.Declaration
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.kt.EKtArrayAccessExpression
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Cardinal
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreFunctionDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object ArrayAccessExpressionReducerStage : ProjectStage() {

    private val getReducerEntries = ArrayList<GetReducerEntry>()
    private val setReducerEntries = ArrayList<SetReducerEntry>()

    init {
        getReducerEntries.add(
            GetReducerEntry(Core.Vk.C_Ubit, listOf(Core.Kt.C_Int), Core.Vk.Ubit.F_get_Int)
        )
        getReducerEntries.add(
            GetReducerEntry(Core.Vk.C_Ubit, listOf(Core.Vk.C_Ubit), Core.Vk.Ubit.F_get_Ubit)
        )
        getReducerEntries.add(
            GetReducerEntry(Core.Vk.C_Ubit, listOf(Core.Kt.C_Int, Core.Kt.C_Int), Core.Vk.Ubit.F_get_Int_Int)
        )
        getReducerEntries.add(
            GetReducerEntry(Core.Vk.C_Sbit, listOf(Core.Kt.C_Int), Core.Vk.Sbit.F_get_Int)
        )
        getReducerEntries.add(
            GetReducerEntry(Core.Vk.C_Sbit, listOf(Core.Vk.C_Ubit), Core.Vk.Sbit.F_get_Ubit)
        )
        getReducerEntries.add(
            GetReducerEntry(Core.Vk.C_Packed, listOf(Core.Kt.C_Int), Core.Vk.Packed.F_get_Int)
        )
        getReducerEntries.add(
            GetReducerEntry(Core.Vk.C_Packed, listOf(Core.Vk.C_Ubit), Core.Vk.Packed.F_get_Ubit)
        )
        getReducerEntries.add(
            GetReducerEntry(Core.Vk.C_Unpacked, listOf(Core.Kt.C_Int), Core.Vk.Unpacked.F_get_Int)
        )
        getReducerEntries.add(
            GetReducerEntry(Core.Vk.C_Unpacked, listOf(Core.Vk.C_Ubit), Core.Vk.Unpacked.F_get_Ubit)
        )
        getReducerEntries.add(
            GetReducerEntry(Core.Jv.Util.C_ArrayList, listOf(Core.Kt.C_Int), Core.Jv.Util.ArrayList.F_get_Int)
        )

        setReducerEntries.add(
            SetReducerEntry(
                Core.Vk.C_Ubit,
                listOf(Core.Kt.C_Int),
                Core.Kt.C_Boolean.toType(),
                Core.Vk.Ubit.F_set_Int_Boolean
            )
        )
        setReducerEntries.add(
            SetReducerEntry(
                Core.Vk.C_Ubit,
                listOf(Core.Vk.C_Ubit),
                Core.Kt.C_Boolean.toType(),
                Core.Vk.Ubit.F_set_Ubit_Boolean
            )
        )
        setReducerEntries.add(
            SetReducerEntry(
                Core.Vk.C_Ubit,
                listOf(Core.Kt.C_Int, Core.Kt.C_Int),
                Core.Vk.C_Ubit.toType(Cardinal.UNRESOLVED.toType()),
                Core.Vk.Ubit.F_set_Int_Int_Ubit
            )
        )
        setReducerEntries.add(
            SetReducerEntry(
                Core.Vk.C_Sbit,
                listOf(Core.Kt.C_Int),
                Core.Kt.C_Boolean.toType(),
                Core.Vk.Sbit.F_set_Int_Boolean
            )
        )
        setReducerEntries.add(
            SetReducerEntry(
                Core.Vk.C_Sbit,
                listOf(Core.Vk.C_Ubit),
                Core.Kt.C_Boolean.toType(),
                Core.Vk.Sbit.F_set_Ubit_Boolean
            )
        )
        setReducerEntries.add(
            SetReducerEntry(
                Core.Vk.C_Packed,
                listOf(Core.Kt.C_Int),
                Core.Kt.C_Any.toType(),
                Core.Vk.Packed.F_set_Int_E
            )
        )
        setReducerEntries.add(
            SetReducerEntry(
                Core.Vk.C_Packed,
                listOf(Core.Vk.C_Ubit),
                Core.Kt.C_Any.toType(),
                Core.Vk.Packed.F_set_Ubit_E
            )
        )
        setReducerEntries.add(
            SetReducerEntry(
                Core.Vk.C_Unpacked,
                listOf(Core.Kt.C_Int),
                Core.Kt.C_Any.toType(),
                Core.Vk.Unpacked.F_set_Int_E
            )
        )
        setReducerEntries.add(
            SetReducerEntry(
                Core.Vk.C_Unpacked,
                listOf(Core.Vk.C_Ubit),
                Core.Kt.C_Any.toType(),
                Core.Vk.Unpacked.F_set_Ubit_E
            )
        )
        setReducerEntries.add(
            SetReducerEntry(
                Core.Jv.Util.C_ArrayList,
                listOf(Core.Kt.C_Int),
                Core.Kt.C_Any.toType(),
                Core.Jv.Util.ArrayList.F_set_Int_E
            )
        )
    }

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ArrayAccessExpressionReducerVisitor)
    }

    data class GetReducerEntry(
        val arrayDeclaration: Declaration,
        val indexDeclarations: List<Declaration>,
        val reference: CoreFunctionDeclaration
    ) {

        fun match(
            arrayDeclaration: Declaration,
            indexDeclarations: List<Declaration>
        ): Boolean {
            return this.arrayDeclaration == arrayDeclaration &&
                this.indexDeclarations == indexDeclarations
        }
    }

    data class SetReducerEntry(
        val arrayDeclaration: Declaration,
        val indexDeclarations: List<Declaration>,
        val expressionType: Type,
        val reference: CoreFunctionDeclaration
    ) {

        fun match(
            arrayDeclaration: Declaration,
            indexDeclarations: List<Declaration>,
            expressionType: Type
        ): Boolean {
            return this.arrayDeclaration == arrayDeclaration &&
                this.indexDeclarations == indexDeclarations &&
                expressionType.isSubtype(this.expressionType)
        }
    }

    private object ArrayAccessExpressionReducerVisitor : TreeVisitor() {

        private fun getGetReference(
            arrayDeclaration: Declaration,
            indexDeclarations: List<Declaration>
        ): CoreFunctionDeclaration? {
            getReducerEntries.forEach {
                if (it.match(arrayDeclaration, indexDeclarations))
                    return it.reference
            }
            return null
        }

        private fun getSetReference(
            arrayDeclaration: Declaration,
            indexDeclarations: List<Declaration>,
            expressionType: Type
        ): CoreFunctionDeclaration? {
            setReducerEntries.forEach {
                if (it.match(arrayDeclaration, indexDeclarations, expressionType))
                    return it.reference
            }
            return null
        }

        override fun visitKtBinaryExpression(binaryExpression: EKtBinaryExpression) {
            val left = binaryExpression.left
            if (left is EKtArrayAccessExpression && binaryExpression.kind == KtBinaryOperatorKind.EQ) {
                left.acceptChildren(this)
                binaryExpression.right.accept(this)
                val right = binaryExpression.right
                val arrayDeclaration = left.array.type.reference
                val indexDeclarations = left.indices.map { it.type.reference }
                val expressionType = right.type
                val reference = getSetReference(arrayDeclaration, indexDeclarations, expressionType)
                if (reference != null) {
                    binaryExpression.replace(
                        ECallExpression(
                            binaryExpression.location,
                            binaryExpression.type,
                            reference,
                            left.array,
                            ArrayList(left.indices + right),
                            arrayListOf()
                        )
                    )
                } else {
                    Messages.INTERNAL_ERROR.on(left, "Array access expression could not be reduced")
                }
            } else {
                super.visitKtBinaryExpression(binaryExpression)
            }
        }

        override fun visitKtArrayAccessExpression(arrayAccessExpression: EKtArrayAccessExpression) {
            super.visitKtArrayAccessExpression(arrayAccessExpression)
            val arrayDeclaration = arrayAccessExpression.array.type.reference
            val indexDeclarations = arrayAccessExpression.indices.map { it.type.reference }
            val reference = getGetReference(arrayDeclaration, indexDeclarations)
            if (reference != null) {
                arrayAccessExpression.replace(
                    ECallExpression(
                        arrayAccessExpression.location,
                        arrayAccessExpression.type,
                        reference,
                        arrayAccessExpression.array,
                        arrayAccessExpression.indices,
                        arrayListOf()
                    )
                )
            } else {
                Messages.INTERNAL_ERROR.on(arrayAccessExpression, "Array access expression could not be reduced")
            }
        }
    }
}
