/*
 * Copyright (c) 2021 Francis Wang
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

package verikc.kt.parse

import verikc.al.ast.AlRule
import verikc.al.ast.AlTree
import verikc.base.ast.AnnotationFunction
import verikc.base.ast.AnnotationProperty
import verikc.base.ast.AnnotationType
import verikc.base.ast.LineException

object KtParserAnnotation {

    fun parseAnnotationsType(modifiers: AlTree): List<AnnotationType> {
        val unescapedAnnotations = modifiers.findAll(AlRule.UNESCAPED_ANNOTATION)
        return unescapedAnnotations.map {
            when (val simpleIdentifier = getSimpleIdentifier(it)) {
                "top" -> AnnotationType.TOP
                else -> throw LineException(
                    "annotation $simpleIdentifier not supported for type declarations",
                    it.line
                )
            }
        }
    }

    fun parseAnnotationsFunction(modifiers: AlTree): List<AnnotationFunction> {
        val unescapedAnnotations = modifiers.findAll(AlRule.UNESCAPED_ANNOTATION)
        return unescapedAnnotations.map {
            when (val simpleIdentifier = getSimpleIdentifier(it)) {
                "com" -> AnnotationFunction.COM
                "seq" -> AnnotationFunction.SEQ
                "run" -> AnnotationFunction.RUN
                "task" -> AnnotationFunction.TASK
                else -> throw LineException(
                    "annotation $simpleIdentifier not supported for function declarations",
                    it.line
                )
            }
        }
    }

    fun parseAnnotationsProperty(modifiers: AlTree): List<AnnotationProperty> {
        val unescapedAnnotations = modifiers.findAll(AlRule.UNESCAPED_ANNOTATION)
        return unescapedAnnotations.map {
            when (val simpleIdentifier = getSimpleIdentifier(it)) {
                "input" -> AnnotationProperty.INPUT
                "output" -> AnnotationProperty.OUTPUT
                "inout" -> AnnotationProperty.INOUT
                "bus" -> AnnotationProperty.BUS
                "bport" -> AnnotationProperty.BPORT
                "cport" -> AnnotationProperty.CPORT
                "make" -> AnnotationProperty.MAKE
                else -> throw LineException(
                    "annotation $simpleIdentifier not supported for property declaration",
                    it.line
                )
            }
        }
    }

    private fun getSimpleIdentifier(unescapedAnnotation: AlTree): String {
        val userType = unescapedAnnotation.unwrap()
        if (userType.index != AlRule.USER_TYPE) {
            throw LineException("illegal annotation expected user type", unescapedAnnotation.line)
        }
        if (userType.children.size != 1) {
            throw LineException("illegal annotation expected simple user type", unescapedAnnotation.line)
        }
        val simpleUserType = userType.find(AlRule.SIMPLE_USER_TYPE)
        if (simpleUserType.contains(AlRule.TYPE_ARGUMENTS)) {
            throw LineException("illegal annotation", unescapedAnnotation.line)
        }
        return simpleUserType
            .find(AlRule.SIMPLE_IDENTIFIER)
            .unwrap().text
    }
}