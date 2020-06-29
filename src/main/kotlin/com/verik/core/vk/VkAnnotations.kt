package com.verik.core.vk

import com.verik.core.kt.KtRuleType
import com.verik.core.kt.KtToken
import com.verik.core.kt.KtTokenType
import com.verik.core.kt.KtTree

// Copyright (c) 2020 Francis Wang

enum class VkPropertyAnnotation {
    INPUT,
    OUTPUT,
    INOUTPUT,
    INTF,
    PORT,
    COMP,
    WIRE,
    RAND;

    companion object {
        operator fun invoke(annotation: KtTree, exception: Exception): VkPropertyAnnotation {
            val unescapedAnnotation = annotation
                    .getChildAs(KtRuleType.SINGLE_ANNOTATION, exception)
                    .getChildAs(KtRuleType.UNESCAPED_ANNOTATION, exception)
            val identifier = unescapedAnnotation.getDirectDescendantAs(KtTokenType.IDENTIFIER, exception)

            return when ((identifier.node as KtToken).text) {
                "input" -> INPUT
                "output" -> OUTPUT
                "inoutput" -> INOUTPUT
                "intf" -> INTF
                "port" -> PORT
                "comp" -> COMP
                "wire" -> WIRE
                "rand" -> RAND
                else -> throw exception
            }
        }
    }
}