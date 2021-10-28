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

package io.verik.compiler.target.declaration

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.property.Type
import io.verik.compiler.serialize.source.SerializedType
import io.verik.compiler.serialize.source.TypeSerializer
import io.verik.compiler.target.common.CompositeTargetClassDeclaration
import io.verik.compiler.target.common.PrimitiveTargetClassDeclaration

object TargetClass {

    val C_Void = object : PrimitiveTargetClassDeclaration("Void") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            return SerializedType("void")
        }
    }

    val C_Int = object : PrimitiveTargetClassDeclaration("Int") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            return SerializedType("int")
        }
    }

    val C_Boolean = object : PrimitiveTargetClassDeclaration("Boolean") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            return SerializedType("logic")
        }
    }

    val C_String = object : PrimitiveTargetClassDeclaration("String") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            return SerializedType("string")
        }
    }

    val C_Ubit = object : PrimitiveTargetClassDeclaration("Ubit") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            val value = typeArguments[0].asCardinalValue(element)
            return SerializedType("logic", "[${value - 1}:0]", null)
        }
    }

    val C_Sbit = object : PrimitiveTargetClassDeclaration("Sbit") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            val value = typeArguments[0].asCardinalValue(element)
            return SerializedType("logic", "[${value - 1}:0]", null)
        }
    }

    val C_Packed = object : PrimitiveTargetClassDeclaration("Packed") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            val serializedType = TypeSerializer.serialize(typeArguments[1], element)
            var packedDimension = "[${typeArguments[0].asCardinalValue(element) - 1}:0]"
            if (serializedType.packedDimension != null)
                packedDimension += serializedType.packedDimension
            return SerializedType(serializedType.base, packedDimension, serializedType.unpackedDimension)
        }
    }

    val C_Unpacked = object : PrimitiveTargetClassDeclaration("Unpacked") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            val serializedType = TypeSerializer.serialize(typeArguments[1], element)
            var unpackedDimension = "[${typeArguments[0].asCardinalValue(element) - 1}:0]"
            if (serializedType.unpackedDimension != null)
                unpackedDimension += serializedType.unpackedDimension
            return SerializedType(serializedType.base, serializedType.packedDimension, unpackedDimension)
        }
    }

    val C_Time = object : PrimitiveTargetClassDeclaration("Time") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            return SerializedType("time")
        }
    }

    val C_Event = object : PrimitiveTargetClassDeclaration("Event") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            return SerializedType("event")
        }
    }

    val C_ArrayList = object : CompositeTargetClassDeclaration("ArrayList") {

        override val content = """
            class ArrayList #(type T = int);
                T arr [${'$'}];
            endclass
        """.trimIndent()
    }
}
