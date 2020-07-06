package com.verik.core.vk

import com.verik.core.kt.KtRule
import com.verik.core.kt.KtRuleType

// Copyright (c) 2020 Francis Wang

enum class VkClassAnnotation {
    TOP,
    EXTERN;

    companion object {
        operator fun invoke(annotation: KtRule): VkClassAnnotation {
            val unescapedAnnotation = annotation
                    .getChildAs(KtRuleType.SINGLE_ANNOTATION)
                    .getChildAs(KtRuleType.UNESCAPED_ANNOTATION)
            val simpleIdentifier = unescapedAnnotation.getDirectDescendantAs(KtRuleType.SIMPLE_IDENTIFIER,
                    VkParseException("illegal class annotation", annotation.linePos))

            return when (simpleIdentifier.getFirstAsTokenText()) {
                "top" -> TOP
                "extern" -> EXTERN
                else -> throw VkParseException("illegal class annotation", annotation.linePos)
            }
        }
    }
}

enum class VkFunctionAnnotation {
    PUT,
    REG,
    DRIVE,
    INITIAL,
    TASK;

    companion object {
        operator fun invoke(annotation: KtRule): VkFunctionAnnotation {
            val unescapedAnnotation = annotation
                    .getChildAs(KtRuleType.SINGLE_ANNOTATION)
                    .getChildAs(KtRuleType.UNESCAPED_ANNOTATION)
            val simpleIdentifier = unescapedAnnotation.getDirectDescendantAs(KtRuleType.SIMPLE_IDENTIFIER,
                    VkParseException("illegal function annotation", annotation.linePos))

            return when (simpleIdentifier.getFirstAsTokenText()) {
                "put" -> PUT
                "reg" -> REG
                "drive" -> DRIVE
                "initial" -> INITIAL
                "task" -> TASK
                else -> throw VkParseException("illegal function annotation", annotation.linePos)
            }
        }
    }
}

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
        operator fun invoke(annotation: KtRule): VkPropertyAnnotation {
            val unescapedAnnotation = annotation
                    .getChildAs(KtRuleType.SINGLE_ANNOTATION)
                    .getChildAs(KtRuleType.UNESCAPED_ANNOTATION)
            val simpleIdentifier = unescapedAnnotation.getDirectDescendantAs(KtRuleType.SIMPLE_IDENTIFIER,
                    VkParseException("illegal property annotation", annotation.linePos))

            return when (simpleIdentifier.getFirstAsTokenText()) {
                "input" -> INPUT
                "output" -> OUTPUT
                "inoutput" -> INOUTPUT
                "intf" -> INTF
                "port" -> PORT
                "comp" -> COMP
                "wire" -> WIRE
                "rand" -> RAND
                else -> throw VkParseException("illegal property annotation", annotation.linePos)
            }
        }
    }
}
