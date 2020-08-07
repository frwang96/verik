/*
 * Copyright 2020 Francis Wang
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

package io.verik.core.vk

import io.verik.core.FileLineException
import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType

enum class VkClassAnnotation {
    TOP,
    EXPORT,
    ABSTRACT;

    companion object {
        operator fun invoke(annotation: AlRule): VkClassAnnotation {
            val unescapedAnnotation = annotation
                    .childAs(AlRuleType.SINGLE_ANNOTATION)
                    .childAs(AlRuleType.UNESCAPED_ANNOTATION)
            val simpleIdentifier = unescapedAnnotation.directDescendantAs(AlRuleType.SIMPLE_IDENTIFIER,
                    FileLineException("illegal class annotation", annotation.fileLine))

            return when (simpleIdentifier.firstAsTokenText()) {
                "top" -> TOP
                "export" -> EXPORT
                "abstract" -> ABSTRACT
                else -> throw FileLineException("illegal class annotation", annotation.fileLine)
            }
        }
    }
}

enum class VkFunctionAnnotation {
    ABSTRACT,
    PUT,
    REG,
    DRIVE,
    INITIAL,
    TASK;

    companion object {
        operator fun invoke(annotation: AlRule): VkFunctionAnnotation {
            val unescapedAnnotation = annotation
                    .childAs(AlRuleType.SINGLE_ANNOTATION)
                    .childAs(AlRuleType.UNESCAPED_ANNOTATION)
            val simpleIdentifier = unescapedAnnotation.directDescendantAs(AlRuleType.SIMPLE_IDENTIFIER,
                    FileLineException("illegal function annotation", annotation.fileLine))

            return when (simpleIdentifier.firstAsTokenText()) {
                "abstract" -> ABSTRACT
                "put" -> PUT
                "reg" -> REG
                "drive" -> DRIVE
                "initial" -> INITIAL
                "task" -> TASK
                else -> throw FileLineException("illegal function annotation", annotation.fileLine)
            }
        }
    }
}

enum class VkPropertyAnnotation {
    INPUT,
    OUTPUT,
    INOUT,
    INTERF,
    MODPORT,
    COMP,
    WIRE,
    RAND;

    companion object {
        operator fun invoke(annotation: AlRule): VkPropertyAnnotation {
            val unescapedAnnotation = annotation
                    .childAs(AlRuleType.SINGLE_ANNOTATION)
                    .childAs(AlRuleType.UNESCAPED_ANNOTATION)
            val simpleIdentifier = unescapedAnnotation.directDescendantAs(AlRuleType.SIMPLE_IDENTIFIER,
                    FileLineException("illegal property annotation", annotation.fileLine))

            return when (simpleIdentifier.firstAsTokenText()) {
                "input" -> INPUT
                "output" -> OUTPUT
                "inout" -> INOUT
                "interf" -> INTERF
                "modport" -> MODPORT
                "comp" -> COMP
                "wire" -> WIRE
                "rand" -> RAND
                else -> throw FileLineException("illegal property annotation", annotation.fileLine)
            }
        }
    }
}
