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

package io.verik.core.kt

import io.verik.core.LineException
import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType

private class KtAnnotationParser {

    companion object {

        fun getSimpleIdentifier(annotation: AlRule): String {
            val userType = annotation
                    .childAs(AlRuleType.SINGLE_ANNOTATION)
                    .childAs(AlRuleType.UNESCAPED_ANNOTATION)
                    .firstAsRule()
            if (userType.type != AlRuleType.USER_TYPE) {
                throw LineException("illegal annotation expected user type", annotation)
            }
            if (userType.children.size != 1) {
                throw LineException("illegal annotation expected simple user type", annotation)
            }
            val simpleUserType = userType.childAs(AlRuleType.SIMPLE_USER_TYPE)
            if (simpleUserType.containsType(AlRuleType.TYPE_ARGUMENTS)) {
                throw LineException("illegal annotation", annotation)
            }
            return simpleUserType.childAs(AlRuleType.SIMPLE_IDENTIFIER).firstAsTokenText()
        }
    }
}

enum class KtAnnotationType {
    TOP,
    EXPORT,
    ABSTRACT;

    companion object {

        operator fun invoke(annotation: AlRule): KtAnnotationType {
            return when(val simpleIdentifier = KtAnnotationParser.getSimpleIdentifier(annotation)) {
                "top" -> TOP
                "export" -> EXPORT
                "abstract" -> ABSTRACT
                else -> throw LineException("annotation $simpleIdentifier not supported for type declarations", annotation)
            }
        }
    }
}

enum class KtAnnotationFunction {
    ABSTRACT,
    PUT,
    REG,
    DRIVE,
    INITIAL,
    TASK;

    companion object {

        operator fun invoke(annotation: AlRule): KtAnnotationFunction {
            return when(val simpleIdentifier = KtAnnotationParser.getSimpleIdentifier(annotation)) {
                "abstract" -> ABSTRACT
                "put" -> PUT
                "reg" -> REG
                "drive" -> DRIVE
                "initial" -> INITIAL
                "task" -> TASK
                else -> throw LineException("annotation $simpleIdentifier not supported for function declarations", annotation)
            }
        }
    }
}

enum class KtAnnotationProperty {
    INPUT,
    OUTPUT,
    INOUT,
    INTERF,
    MODPORT,
    COMP,
    WIRE,
    RAND;

    companion object {

        operator fun invoke(annotation: AlRule): KtAnnotationProperty {
            return when(val simpleIdentifier = KtAnnotationParser.getSimpleIdentifier(annotation)) {
                "input" -> INPUT
                "output" -> OUTPUT
                "inout" -> INOUT
                "interf" -> INTERF
                "modport" -> MODPORT
                "comp" -> COMP
                "wire" -> WIRE
                "rand" -> RAND
                else -> throw LineException("annotation $simpleIdentifier not supported for property declaration", annotation)
            }
        }
    }
}
