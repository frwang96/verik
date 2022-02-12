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

package io.verik.compiler.target.common

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.message.Messages
import io.verik.compiler.serialize.source.SerializedType
import io.verik.compiler.serialize.source.TypeSerializer

sealed class TargetClassDeclaration : TargetDeclaration {

    abstract fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType
}

abstract class PrimitiveTargetClassDeclaration(
    override val parent: TargetDeclaration,
    override var name: String
) : TargetClassDeclaration()

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
