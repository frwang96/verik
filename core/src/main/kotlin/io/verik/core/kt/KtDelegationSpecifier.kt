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

import io.verik.core.FileLine
import io.verik.core.FileLineException
import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType

data class KtDelegationSpecifier(
        val typeIdentifier: String,
        val args: List<KtExpression>,
        val fileLine: FileLine
) {

    companion object {

        operator fun invoke(delegationSpecifier: AlRule): KtDelegationSpecifier {
            val child = delegationSpecifier.firstAsRule()
            return when (child.type) {
                AlRuleType.CONSTRUCTOR_INVOCATION -> {
                    val typeIdentifier = KtType.identifier(child.childAs(AlRuleType.USER_TYPE))
                    val args = child
                            .childAs(AlRuleType.VALUE_ARGUMENTS)
                            .childrenAs(AlRuleType.VALUE_ARGUMENT)
                            .map { it.childAs(AlRuleType.EXPRESSION) }
                            .map { KtExpression(it) }
                    KtDelegationSpecifier(typeIdentifier, args, delegationSpecifier.fileLine)
                }
                AlRuleType.USER_TYPE -> {
                    KtDelegationSpecifier(KtType.identifier(child), listOf(), delegationSpecifier.fileLine)
                }
                else -> throw FileLineException("constructor invocation or user type expected", delegationSpecifier.fileLine)
            }
        }
    }
}