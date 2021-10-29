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

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.property.Type
import io.verik.compiler.serialize.source.SerializedType
import io.verik.compiler.serialize.source.TypeSerializer

sealed class TargetClassDeclaration : TargetDeclaration {

    abstract fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType
}

abstract class PrimitiveTargetClassDeclaration(override var name: String) : TargetClassDeclaration()

class CompositeTargetClassDeclaration(
    override var name: String,
    val prolog: String,
    val body: String,
    val epilog: String
) : TargetClassDeclaration() {

    override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
        val base = "${TargetPackage.name}::$name"
        return if (typeArguments.isNotEmpty()) {
            val serializedTypeArguments = typeArguments.map { TypeSerializer.serialize(it, element) }
            serializedTypeArguments.forEach { it.checkNoUnpackedDimension(element) }
            val serializedTypeArgumentString = serializedTypeArguments.joinToString { it.getBaseAndPackedDimension() }
            SerializedType("$base#($serializedTypeArgumentString)")
        } else SerializedType(base)
    }
}
