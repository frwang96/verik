/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.target.declaration

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.serialize.source.SerializedType
import io.verik.compiler.serialize.source.TypeSerializer
import io.verik.compiler.target.common.CompositeTargetClassDeclaration
import io.verik.compiler.target.common.PrimitiveTargetClassDeclaration
import io.verik.compiler.target.common.TargetPackage
import io.verik.compiler.target.common.TargetScope

object TargetClasses : TargetScope(TargetPackage) {

    val C_Void = object : PrimitiveTargetClassDeclaration(parent, "Void") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            return SerializedType("void")
        }
    }

    val C_Int = object : PrimitiveTargetClassDeclaration(parent, "Int") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            return SerializedType("int")
        }
    }

    val C_Double = object : PrimitiveTargetClassDeclaration(parent, "Double") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            return SerializedType("real")
        }
    }

    val C_Boolean = object : PrimitiveTargetClassDeclaration(parent, "Boolean") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            return SerializedType("logic")
        }
    }

    val C_String = object : PrimitiveTargetClassDeclaration(parent, "String") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            return SerializedType("string")
        }
    }

    val C_ArrayList = CompositeTargetClassDeclaration(
        parent,
        "ArrayList",
        listOf("E"),
        "class ArrayList #(type E = int);",
        "E queue [${'$'}];",
        "endclass : ArrayList"
    )

    val C_Ubit = object : PrimitiveTargetClassDeclaration(parent, "Ubit") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            val value = typeArguments[0].asCardinalValue(element)
            return SerializedType("logic [${value - 1}:0]")
        }
    }

    val C_Sbit = object : PrimitiveTargetClassDeclaration(parent, "Sbit") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            val value = typeArguments[0].asCardinalValue(element)
            return SerializedType("logic signed [${value - 1}:0]")
        }
    }

    val C_Packed = object : PrimitiveTargetClassDeclaration(parent, "Packed") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            val serializedType = TypeSerializer.serialize(typeArguments[1], element)
            val packedDimension = "[${typeArguments[0].asCardinalValue(element) - 1}:0]"
            val index = serializedType.base.indexOfFirst { it == '[' }
            return if (index == -1) {
                SerializedType("${serializedType.base} $packedDimension", serializedType.variableDimension)
            } else {
                val base = serializedType.base
                SerializedType(
                    base.substring(0, index) + packedDimension + base.substring(index),
                    serializedType.variableDimension
                )
            }
        }
    }

    val C_Unpacked = object : PrimitiveTargetClassDeclaration(parent, "Unpacked") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            val serializedType = TypeSerializer.serialize(typeArguments[1], element)
            val unpackedDimension = "[${typeArguments[0].asCardinalValue(element) - 1}:0]"
            return SerializedType(
                serializedType.base,
                unpackedDimension + (serializedType.variableDimension ?: "")
            )
        }
    }

    val C_Queue = object : PrimitiveTargetClassDeclaration(parent, "Queue") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            val serializedType = TypeSerializer.serialize(typeArguments[0], element)
            serializedType.checkNoVariableDimension(element)
            return SerializedType(serializedType.base, "[$]")
        }
    }

    val C_DynamicArray = object : PrimitiveTargetClassDeclaration(parent, "DynamicArray") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            val serializedType = TypeSerializer.serialize(typeArguments[0], element)
            serializedType.checkNoVariableDimension(element)
            return SerializedType(serializedType.base, "[]")
        }
    }

    val C_AssociativeArray = object : PrimitiveTargetClassDeclaration(parent, "AssociativeArray") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            val serializedTypes = typeArguments.map { TypeSerializer.serialize(it, element) }
            serializedTypes[0].checkNoVariableDimension(element)
            serializedTypes[1].checkNoVariableDimension(element)
            return SerializedType(serializedTypes[1].base, "[${serializedTypes[0].base}]")
        }
    }

    val C_Time = object : PrimitiveTargetClassDeclaration(parent, "Time") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            return SerializedType("time")
        }
    }

    val C_Event = object : PrimitiveTargetClassDeclaration(parent, "Event") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            return SerializedType("event")
        }
    }

    val C_Mailbox = object : PrimitiveTargetClassDeclaration(parent, "Mailbox") {

        override fun serializeType(typeArguments: List<Type>, element: EElement): SerializedType {
            return SerializedType("mailbox")
        }
    }
}
