package com.verik.core.vk

import com.verik.core.LinePosException
import com.verik.core.kt.KtRule
import com.verik.core.kt.KtTokenType

// Copyright (c) 2020 Francis Wang

enum class VkClassModifier {
    ENUM,
    OPEN;

    companion object {

        operator fun invoke(modifier: KtRule): VkClassModifier {
            return when (modifier.firstAsRule().firstAsTokenType()) {
                KtTokenType.ENUM -> ENUM
                KtTokenType.OPEN -> OPEN
                else -> throw LinePosException("illegal class modifier", modifier.linePos)
            }
        }
    }
}

enum class VkFunctionModifier {
    OVERRIDE,
    OPEN;

    companion object {

        operator fun invoke(modifier: KtRule): VkFunctionModifier {
            return when (modifier.firstAsRule().firstAsTokenType()) {
                KtTokenType.OVERRIDE -> OVERRIDE
                KtTokenType.OPEN -> OPEN
                else -> throw LinePosException("illegal function modifier", modifier.linePos)
            }
        }
    }
}
