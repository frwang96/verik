package com.verik.core.vk

import com.verik.core.kt.KtRule
import com.verik.core.kt.KtTokenType

// Copyright (c) 2020 Francis Wang

enum class VkClassModifier {
    ENUM,
    ABSTRACT,
    OPEN;

    companion object {

        operator fun invoke(modifier: KtRule): VkClassModifier {
            return when (modifier.getFirstAsRule(VkGrammarException()).getFirstAsTokenText(VkGrammarException())) {
                "enum" -> ENUM
                "abstract" -> ABSTRACT
                "open" -> OPEN
                else -> throw VkParseException("illegal class modifier", modifier.linePos)
            }
        }
    }
}

enum class VkFunctionModifier {
    OVERRIDE,
    ABSTRACT,
    OPEN;

    companion object {

        operator fun invoke(modifier: KtRule): VkFunctionModifier {
            return when (modifier.getFirstAsRule(VkGrammarException()).getFirstAsTokenText(VkGrammarException())) {
                "override" -> OVERRIDE
                "abstract" -> ABSTRACT
                "open" -> OPEN
                else -> throw VkParseException("illegal function modifier", modifier.linePos)
            }
        }
    }
}

enum class VkPropertyModifier {
    CONST;

    companion object {

        operator fun invoke(modifier: KtRule): VkPropertyModifier {
            modifier.getDirectDescendantAs(KtTokenType.CONST, VkParseException("illegal property modifier", modifier.linePos))
            return CONST
        }
    }
}
