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
import io.verik.core.al.AlTokenType

enum class KtModifier {
    ENUM,
    TOP,
    EXPORT,
    ABSTRACT,
    INPUT,
    OUTPUT,
    INOUT,
    INTERF,
    MODPORT,
    COMP,
    WIRE,
    RAND,
    PUT,
    REG,
    DRIVE,
    INITIAL,
    TASK;

    companion object {

        operator fun invoke(modifierOrAnnotation: AlRule): KtModifier? {
            when (modifierOrAnnotation.type) {
                AlRuleType.MODIFIER -> {
                    return when (val type = modifierOrAnnotation.firstAsRule().firstAsTokenType()) {
                        AlTokenType.ENUM -> ENUM
                        AlTokenType.OVERRIDE -> null
                        AlTokenType.PUBLIC -> null
                        AlTokenType.PRIVATE -> null
                        AlTokenType.INTERNAL -> null
                        AlTokenType.PROTECTED -> null
                        AlTokenType.FINAL -> null
                        AlTokenType.OPEN -> null
                        else -> throw LineException("modifier $type not supported", modifierOrAnnotation)
                    }
                }
                AlRuleType.ANNOTATION -> {
                    val userType = modifierOrAnnotation
                            .childAs(AlRuleType.SINGLE_ANNOTATION)
                            .childAs(AlRuleType.UNESCAPED_ANNOTATION)
                            .firstAsRule()
                    if (userType.type != AlRuleType.USER_TYPE) {
                        throw LineException("illegal annotation expected user type", modifierOrAnnotation)
                    }
                    if (userType.children.size != 1) {
                        throw LineException("illegal annotation expected simple user type", modifierOrAnnotation)
                    }
                    val simpleUserType = userType.childAs(AlRuleType.SIMPLE_USER_TYPE)
                    if (simpleUserType.containsType(AlRuleType.TYPE_ARGUMENTS)) {
                        throw LineException("illegal annotation", modifierOrAnnotation)
                    }
                    val simpleIdentifier = simpleUserType.childAs(AlRuleType.SIMPLE_IDENTIFIER)
                    return when(val type = simpleIdentifier.firstAsTokenText()) {
                        "top" -> TOP
                        "export" -> EXPORT
                        "abstract" -> ABSTRACT
                        "input" -> INPUT
                        "output" -> OUTPUT
                        "inout" -> INOUT
                        "interf" -> INTERF
                        "modport" -> MODPORT
                        "comp" -> COMP
                        "wire" -> WIRE
                        "rand" -> RAND
                        "put" -> PUT
                        "reg" -> REG
                        "drive" -> DRIVE
                        "initial" -> INITIAL
                        "task" -> TASK
                        else -> throw LineException("annotation $type not supported", modifierOrAnnotation)
                    }
                }
                else -> throw LineException("expected modifier or annotation", modifierOrAnnotation)
            }
        }
    }
}