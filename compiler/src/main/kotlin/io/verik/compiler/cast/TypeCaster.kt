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

import io.verik.compiler.ast.common.NullDeclaration
import io.verik.compiler.ast.common.Type
import io.verik.compiler.main.messageCollector
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType

object TypeCaster {

    fun castType(type: KotlinType, element: PsiElement): Type {
        if (type.isMarkedNullable)
            messageCollector.error("Nullable type not supported: $type", element)
        return Type(NullDeclaration, arrayListOf())
    }

    fun castType(bindingContext: BindingContext, typeReference: KtTypeReference): Type {
        val type = bindingContext.getSliceContents(BindingContext.TYPE)[typeReference]!!
        if (type.isMarkedNullable)
            messageCollector.error("Nullable type not supported: $type", typeReference)
        return Type(NullDeclaration, arrayListOf())
    }
}