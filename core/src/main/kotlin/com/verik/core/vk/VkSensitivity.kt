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

package com.verik.core.vk

import com.verik.core.LinePosException
import com.verik.core.sv.SvSensitivityEntry
import com.verik.core.sv.SvSensitivityType

enum class VkSensitivityType {
    POSEDGE,
    NEGEDGE;

    fun extract(): SvSensitivityType {
        return when (this) {
            POSEDGE -> SvSensitivityType.POSEDGE
            NEGEDGE -> SvSensitivityType.NEGEDGE
        }
    }
}

data class VkSensitivityEntry(
        val type: VkSensitivityType,
        val identifier: String
) {

    fun extract(): SvSensitivityEntry {
        return SvSensitivityEntry(type.extract(), identifier)
    }

    companion object {

        operator fun invoke(expression: VkExpression): VkSensitivityEntry {
            return if (expression is VkCallableExpression && expression.target is VkIdentifierExpression) {
                val type = when (expression.target.identifier) {
                    "posedge" -> VkSensitivityType.POSEDGE
                    "negedge" -> VkSensitivityType.NEGEDGE
                    else -> throw LinePosException("posedge or negedge expression expected", expression.linePos)
                }
                if (expression.args.size != 1) {
                    throw LinePosException("identifier expected", expression.linePos)
                }
                val identifier = expression.args[0].let {
                    if (it is VkIdentifierExpression) it.identifier
                    else throw LinePosException("identifier expected", expression.linePos)
                }
                VkSensitivityEntry(type, identifier)
            } else throw LinePosException("posedge or negedge expression expected", expression.linePos)
        }
    }
}
