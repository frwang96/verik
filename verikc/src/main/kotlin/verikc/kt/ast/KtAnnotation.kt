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

package verikc.kt.ast

import verikc.al.AlRule
import verikc.al.AlRuleType
import verikc.base.ast.LineException

private class KtAnnotationParser {

    companion object {

        fun getSimpleIdentifier(annotation: AlRule): String {
            val userType = annotation
                    .childAs(AlRuleType.SINGLE_ANNOTATION)
                    .childAs(AlRuleType.UNESCAPED_ANNOTATION)
                    .firstAsRule()
            if (userType.type != AlRuleType.USER_TYPE) {
                throw LineException("illegal annotation expected user type", annotation.line)
            }
            if (userType.children.size != 1) {
                throw LineException("illegal annotation expected simple user type", annotation.line)
            }
            val simpleUserType = userType.childAs(AlRuleType.SIMPLE_USER_TYPE)
            if (simpleUserType.containsType(AlRuleType.TYPE_ARGUMENTS)) {
                throw LineException("illegal annotation", annotation.line)
            }
            return simpleUserType.childAs(AlRuleType.SIMPLE_IDENTIFIER).firstAsTokenText()
        }
    }
}

enum class KtAnnotationType {
    STATIC,
    TOP;

    companion object {

        operator fun invoke(annotation: AlRule): KtAnnotationType {
            return when(val simpleIdentifier = KtAnnotationParser.getSimpleIdentifier(annotation)) {
                "static" -> STATIC
                "top" -> TOP
                else -> throw LineException(
                    "annotation $simpleIdentifier not supported for type declarations",
                    annotation.line
                )
            }
        }
    }
}

enum class KtAnnotationFunction {
    STATIC,
    COM,
    SEQ,
    RUN,
    TASK;

    companion object {

        operator fun invoke(annotation: AlRule): KtAnnotationFunction {
            return when(val simpleIdentifier = KtAnnotationParser.getSimpleIdentifier(annotation)) {
                "static" -> STATIC
                "com" -> COM
                "seq" -> SEQ
                "run" -> RUN
                "task" -> TASK
                else -> throw LineException(
                    "annotation $simpleIdentifier not supported for function declarations",
                    annotation.line
                )
            }
        }
    }
}

enum class KtAnnotationProperty {
    STATIC,
    INPUT,
    OUTPUT,
    INOUT,
    BUS,
    BUSPORT,
    MAKE;

    companion object {

        operator fun invoke(annotation: AlRule): KtAnnotationProperty {
            return when(val simpleIdentifier = KtAnnotationParser.getSimpleIdentifier(annotation)) {
                "static" -> STATIC
                "input" -> INPUT
                "output" -> OUTPUT
                "inout" -> INOUT
                "bus" -> BUS
                "busport" -> BUSPORT
                "make" -> MAKE
                else -> throw LineException(
                    "annotation $simpleIdentifier not supported for property declaration",
                    annotation.line
                )
            }
        }
    }
}
