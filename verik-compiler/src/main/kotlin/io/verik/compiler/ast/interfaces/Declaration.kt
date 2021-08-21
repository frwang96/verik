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

package io.verik.compiler.ast.interfaces

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.location
import io.verik.compiler.main.m
import io.verik.compiler.message.SourceLocation
import org.jetbrains.kotlin.psi.KtElement

interface Declaration {

    var name: String

    fun toType(vararg arguments: Type): Type {
        return Type(this, arrayListOf(*arguments))
    }
}

inline fun <reified T> Declaration.cast(element: KtElement): T? {
    return cast(element.location())
}

inline fun <reified T> Declaration.cast(element: EElement): T? {
    return cast(element.location)
}

inline fun <reified T> Declaration.cast(location: SourceLocation): T? {
    return when (this) {
        is T -> this
        else -> {
            val expectedName = T::class.simpleName
            val actualName = this::class.simpleName
            m.error("Could not cast declaration: Expected $expectedName actual $actualName", location)
            null
        }
    }
}
