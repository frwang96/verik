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
                    .childAs(KtRuleType.SINGLE_ANNOTATION)
                    .childAs(KtRuleType.UNESCAPED_ANNOTATION)
            val simpleIdentifier = unescapedAnnotation.directDescendantAs(KtRuleType.SIMPLE_IDENTIFIER,
                    VkParseException("illegal class annotation", annotation.linePos))

            return when (simpleIdentifier.firstAsTokenText()) {
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
                    .childAs(KtRuleType.SINGLE_ANNOTATION)
                    .childAs(KtRuleType.UNESCAPED_ANNOTATION)
            val simpleIdentifier = unescapedAnnotation.directDescendantAs(KtRuleType.SIMPLE_IDENTIFIER,
                    VkParseException("illegal function annotation", annotation.linePos))

            return when (simpleIdentifier.firstAsTokenText()) {
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
    INOUT,
    INTF,
    IPORT,
    COMP,
    WIRE,
    RAND;

    companion object {
        operator fun invoke(annotation: KtRule): VkPropertyAnnotation {
            val unescapedAnnotation = annotation
                    .childAs(KtRuleType.SINGLE_ANNOTATION)
                    .childAs(KtRuleType.UNESCAPED_ANNOTATION)
            val simpleIdentifier = unescapedAnnotation.directDescendantAs(KtRuleType.SIMPLE_IDENTIFIER,
                    VkParseException("illegal property annotation", annotation.linePos))

            return when (simpleIdentifier.firstAsTokenText()) {
                "input" -> INPUT
                "output" -> OUTPUT
                "inout" -> INOUT
                "intf" -> INTF
                "iport" -> IPORT
                "comp" -> COMP
                "wire" -> WIRE
                "rand" -> RAND
                else -> throw VkParseException("illegal property annotation", annotation.linePos)
            }
        }
    }
}
