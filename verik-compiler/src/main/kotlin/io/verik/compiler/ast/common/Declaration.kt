/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.common

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.common.location
import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation
import org.jetbrains.kotlin.psi.KtElement

interface Declaration {

    var name: String

    fun toType(vararg arguments: Type): Type {
        return Type(this, arrayListOf(*arguments))
    }

    fun toType(arguments: List<Type>): Type {
        return Type(this, ArrayList(arguments))
    }
}

inline fun <reified T> Declaration.cast(element: KtElement): T {
    return cast(element.location())
}

inline fun <reified T> Declaration.cast(element: EElement): T {
    return cast(element.location)
}

inline fun <reified T> Declaration.cast(location: SourceLocation): T {
    return when (this) {
        is T -> this
        else -> {
            val expectedName = T::class.simpleName
            val actualName = this::class.simpleName
            Messages.INTERNAL_ERROR.on(
                location,
                "Could not cast declaration: Expected $expectedName actual $actualName"
            )
        }
    }
}
