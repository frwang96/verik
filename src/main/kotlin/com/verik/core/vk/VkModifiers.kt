package com.verik.core.vk

import com.verik.core.kt.KtToken
import com.verik.core.kt.KtTokenType
import com.verik.core.kt.KtTree

// Copyright (c) 2020 Francis Wang

enum class VkPropertyModifier {
    CONST;

    companion object {
        operator fun invoke(modifier: KtTree): VkPropertyModifier {
            modifier.getDirectDescendantAs(KtTokenType.CONST, VkParseException(modifier.linePos, "illegal property modifier"))
            return CONST
        }
    }
}

enum class VkClassModifier {
    ENUM,
    ABSTRACT,
    OPEN;

    companion object {
        operator fun invoke(modifier: KtTree): VkClassModifier {
            return when ((modifier.first().first().node as KtToken).text) {
                "enum" -> ENUM
                "abstract" -> ABSTRACT
                "open" -> OPEN
                else -> throw VkParseException(modifier.linePos, "illegal class modifier")
            }
        }
    }
}
