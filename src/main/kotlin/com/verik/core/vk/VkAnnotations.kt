package com.verik.core.vk

import com.verik.core.kt.KtRuleType
import com.verik.core.kt.KtToken
import com.verik.core.kt.KtTree

// Copyright (c) 2020 Francis Wang

enum class VkClassAnnotation {
    TOP,
    EXTERN;

    companion object {
        operator fun invoke(annotation: KtTree): VkClassAnnotation {
            val unescapedAnnotation = annotation
                    .getChildAs(KtRuleType.SINGLE_ANNOTATION, VkGrammarException())
                    .getChildAs(KtRuleType.UNESCAPED_ANNOTATION, VkGrammarException())
            val simpleIdentifier = unescapedAnnotation.getDirectDescendantAs(KtRuleType.SIMPLE_IDENTIFIER,
                    VkParseException(annotation.linePos, "illegal class annotation"))

            return when ((simpleIdentifier.first().node as KtToken).text) {
                "top" -> TOP
                "extern" -> EXTERN
                else -> throw VkParseException(annotation.linePos, "illegal class annotation")
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
        operator fun invoke(annotation: KtTree): VkFunctionAnnotation {
            val unescapedAnnotation = annotation
                    .getChildAs(KtRuleType.SINGLE_ANNOTATION, VkGrammarException())
                    .getChildAs(KtRuleType.UNESCAPED_ANNOTATION, VkGrammarException())
            val simpleIdentifier = unescapedAnnotation.getDirectDescendantAs(KtRuleType.SIMPLE_IDENTIFIER,
                    VkParseException(annotation.linePos, "illegal function annotation"))

            return when ((simpleIdentifier.first().node as KtToken).text) {
                "put" -> PUT
                "reg" -> REG
                "drive" -> DRIVE
                "initial" -> INITIAL
                "task" -> TASK
                else -> throw VkParseException(annotation.linePos, "illegal function annotation")
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
        operator fun invoke(annotation: KtTree): VkPropertyAnnotation {
            val unescapedAnnotation = annotation
                    .getChildAs(KtRuleType.SINGLE_ANNOTATION, VkGrammarException())
                    .getChildAs(KtRuleType.UNESCAPED_ANNOTATION, VkGrammarException())
            val simpleIdentifier = unescapedAnnotation.getDirectDescendantAs(KtRuleType.SIMPLE_IDENTIFIER,
                    VkParseException(annotation.linePos, "illegal property annotation"))

            return when ((simpleIdentifier.first().node as KtToken).text) {
                "input" -> INPUT
                "output" -> OUTPUT
                "inoutput" -> INOUTPUT
                "intf" -> INTF
                "port" -> PORT
                "comp" -> COMP
                "wire" -> WIRE
                "rand" -> RAND
                else -> throw VkParseException(annotation.linePos, "illegal property annotation")
            }
        }
    }
}
