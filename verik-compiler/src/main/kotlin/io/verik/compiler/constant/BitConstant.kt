/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.compiler.constant

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.property.Type
import io.verik.compiler.core.common.Cardinal
import io.verik.compiler.core.common.Core
import java.math.BigInteger

class BitConstant(
    val kind: BitComponent,
    val value: BitComponent,
    val signed: Boolean,
    val width: Int
) {

    // encoding (kind, value)
    // 00: 0
    // 01: 1
    // 10: x
    // 11: z

    constructor(value: Int, signed: Boolean, width: Int) : this(
        BitComponent.zeroes(width),
        BitComponent(BigInteger.valueOf(value.toLong()), width),
        signed,
        width
    )

    fun getType(): Type {
        return if (signed) {
            Core.Vk.C_Sbit.toType(Cardinal.of(width).toType())
        } else {
            Core.Vk.C_Ubit.toType(Cardinal.of(width).toType())
        }
    }

    fun add(bitConstant: BitConstant, expression: EExpression): BitConstant {
        val width = expression.type.asBitWidth(expression)
        val signed = expression.type.asBitSigned(expression)
        if (!kind.allZeroes() || !bitConstant.kind.allZeroes()) {
            return BitConstant(BitComponent.ones(width), BitComponent.zeroes(width), signed, width)
        }
        val bigInteger = value.toBigIntegerUnsigned() + bitConstant.value.toBigIntegerUnsigned()
        val value = BitComponent(bigInteger, width)
        return BitConstant(BitComponent.zeroes(width), value, signed, width)
    }

    fun sub(bitConstant: BitConstant, expression: EExpression): BitConstant {
        val width = expression.type.asBitWidth(expression)
        val signed = expression.type.asBitSigned(expression)
        if (!kind.allZeroes() || !bitConstant.kind.allZeroes()) {
            return BitConstant(BitComponent.ones(width), BitComponent.zeroes(width), signed, width)
        }
        val bigInteger = value.toBigIntegerUnsigned() - bitConstant.value.toBigIntegerUnsigned()
        val value = BitComponent(bigInteger, width)
        return BitConstant(BitComponent.zeroes(width), value, signed, width)
    }
}
