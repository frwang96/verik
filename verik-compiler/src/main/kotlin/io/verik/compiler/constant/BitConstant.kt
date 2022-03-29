/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.constant

import io.verik.compiler.ast.common.Type
import io.verik.compiler.core.common.Cardinal
import io.verik.compiler.core.common.Core
import java.lang.Integer.max
import java.math.BigInteger

/**
 * A four-state bit constant encoded with [kind] and [value]. The encoding is given by 00 = 0, 01 = 1, 10 = x, 11 = z.
 */
class BitConstant(
    val kind: BitComponent,
    val value: BitComponent,
    val signed: Boolean,
    val width: Int
) {

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

    fun plus(bitConstant: BitConstant): BitConstant {
        val width = max(width, bitConstant.width)
        val signed = signed && bitConstant.signed
        if (!kind.allZeroes() || !bitConstant.kind.allZeroes()) {
            return BitConstant(BitComponent.ones(width), BitComponent.zeroes(width), signed, width)
        }
        val bigInteger = value.toBigIntegerUnsigned() + bitConstant.value.toBigIntegerUnsigned()
        val value = BitComponent(bigInteger, width)
        return BitConstant(BitComponent.zeroes(width), value, signed, width)
    }

    fun minus(bitConstant: BitConstant): BitConstant {
        val width = max(width, bitConstant.width)
        val signed = signed && bitConstant.signed
        if (!kind.allZeroes() || !bitConstant.kind.allZeroes()) {
            return BitConstant(BitComponent.ones(width), BitComponent.zeroes(width), signed, width)
        }
        val bigInteger = value.toBigIntegerUnsigned() - bitConstant.value.toBigIntegerUnsigned()
        val value = BitComponent(bigInteger, width)
        return BitConstant(BitComponent.zeroes(width), value, signed, width)
    }
}
