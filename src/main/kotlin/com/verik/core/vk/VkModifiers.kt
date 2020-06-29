package com.verik.core.vk

import com.verik.core.kt.KtTokenType
import com.verik.core.kt.KtTree

// Copyright (c) 2020 Francis Wang

enum class VkPropertyModifier {
    CONST;

    companion object {
        operator fun invoke(modifier: KtTree, exception: Exception): VkPropertyModifier {
            modifier.getDirectDescendantAs(KtTokenType.CONST, exception)
            return CONST
        }
    }
}