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

package io.verik.compiler.ast.property

import io.verik.compiler.main.SourceLocation
import io.verik.compiler.main.m
import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType

enum class KtUnaryOperatorKind {
    EXCL;

    companion object {

        operator fun invoke(token: IElementType, location: SourceLocation): KtUnaryOperatorKind? {
            return when (token.toString()) {
                "EXCL" -> EXCL
                else -> {
                    m.error("Unrecognised unary operator kind: $token", location)
                    null
                }
            }
        }
    }
}