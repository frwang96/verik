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
import verikc.al.AlTerminal
import verikc.al.AlTree
import verikc.base.ast.LineException

private class KtAnnotationParser {

    companion object {

        fun getSimpleIdentifier(annotation: AlTree): String {
            val userType = annotation
                .find(AlRule.SINGLE_ANNOTATION)
                .find(AlRule.UNESCAPED_ANNOTATION)
                .unwrap()
            if (userType.index != AlRule.USER_TYPE) {
                throw LineException("illegal annotation expected user type", annotation.line)
            }
            if (userType.children.size != 1) {
                throw LineException("illegal annotation expected simple user type", annotation.line)
            }
            val simpleUserType = userType.find(AlRule.SIMPLE_USER_TYPE)
            if (simpleUserType.contains(AlRule.TYPE_ARGUMENTS)) {
                throw LineException("illegal annotation", annotation.line)
            }
            return simpleUserType
                .find(AlRule.SIMPLE_IDENTIFIER)
                .find(AlTerminal.IDENTIFIER).text!!
        }
    }
}

enum class KtAnnotationType {
    TOP;

    companion object {

        operator fun invoke(annotation: AlTree): KtAnnotationType {
            return when (val simpleIdentifier = KtAnnotationParser.getSimpleIdentifier(annotation)) {
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
    COM,
    SEQ,
    RUN,
    TASK;

    companion object {

        operator fun invoke(annotation: AlTree): KtAnnotationFunction {
            return when (val simpleIdentifier = KtAnnotationParser.getSimpleIdentifier(annotation)) {
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
    INPUT,
    OUTPUT,
    INOUT,
    BUS,
    BUSPORT,
    MAKE;

    companion object {

        operator fun invoke(annotation: AlTree): KtAnnotationProperty {
            return when (val simpleIdentifier = KtAnnotationParser.getSimpleIdentifier(annotation)) {
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
