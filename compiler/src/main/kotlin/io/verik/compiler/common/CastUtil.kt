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

package io.verik.compiler.common

import io.verik.compiler.ast.common.Declaration
import io.verik.compiler.ast.element.VkElement
import io.verik.compiler.main.MessageLocation
import io.verik.compiler.main.getMessageLocation
import io.verik.compiler.main.m
import org.jetbrains.kotlin.com.intellij.psi.PsiElement

object CastUtil {

    inline fun <reified T: VkElement> cast(element: VkElement?): T? {
        val expectedName = T::class.simpleName
        return when (element) {
            null -> {
                m.error("Could not cast element: Expected $expectedName actual null", null)
                null
            }
            is T -> element
            else -> {
                val actualName = element::class.simpleName
                m.error("Could not cast element: Expected $expectedName actual $actualName", element)
                null
            }
        }
    }

    inline fun <reified T: Declaration> cast(declaration: Declaration, element: PsiElement): T? {
        return cast(declaration, element.getMessageLocation())
    }

    inline fun <reified T: Declaration> cast(declaration: Declaration, location: MessageLocation): T? {
        return when (declaration) {
            is T -> declaration
            else -> {
                val expectedName = T::class.simpleName
                val actualName = declaration::class.simpleName
                m.error("Could not cast declaration: Expected $expectedName actual $actualName", location)
                null
            }
        }
    }
}