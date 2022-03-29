/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.target.common

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.message.Messages
import io.verik.compiler.serialize.source.SerializedType
import io.verik.compiler.serialize.source.TypeSerializer

/**
 * A target declaration that represents a class.
 */
sealed class TargetClassDeclaration : TargetDeclaration {

    abstract fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType
}

/**
 * A target class declaration that is defined by the SystemVerilog language. A class in this context should be
 * interpreted as a SystemVerilog type.
 */
abstract class PrimitiveTargetClassDeclaration(
    override val parent: TargetDeclaration,
    override var name: String
) : TargetClassDeclaration()

/**
 * A target class declaration that is generated in the Verik SystemVerilog package.
 */
class CompositeTargetClassDeclaration(
    override val parent: TargetDeclaration,
    override var name: String,
    private val typeParameterNames: List<String>,
    override val contentProlog: String,
    override val contentBody: String,
    override val contentEpilog: String
) : TargetClassDeclaration(), CompositeTarget {

    override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
        val base = "${TargetPackage.name}::$name"
        if (typeArguments.size != typeParameterNames.size) {
            val expected = typeParameterNames.size
            val actual = typeArguments.size
            Messages.INTERNAL_ERROR.on(element, "Incorrect number of type arguments: Expected $expected actual $actual")
        }
        return if (typeArguments.isNotEmpty()) {
            val serializedTypeArguments = typeArguments.map { TypeSerializer.serialize(it, element) }
            serializedTypeArguments.forEach { it.checkNoVariableDimension(element) }
            val serializedTypeArgumentsString = typeParameterNames
                .zip(serializedTypeArguments)
                .joinToString { (typeParameterName, serializedTypeArgument) ->
                    ".$typeParameterName(${serializedTypeArgument.base})"
                }
            SerializedType("$base#($serializedTypeArgumentsString)")
        } else SerializedType(base)
    }
}
