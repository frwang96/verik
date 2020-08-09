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

import io.verik.core.LineException
import io.verik.core.al.AlRule
import io.verik.core.al.AlTokenType

enum class VkClassModifier {
    ENUM,
    OPEN;

    companion object {

        operator fun invoke(modifier: AlRule): VkClassModifier {
            return when (modifier.firstAsRule().firstAsTokenType()) {
                AlTokenType.ENUM -> ENUM
                AlTokenType.OPEN -> OPEN
                else -> throw LineException("illegal class modifier", modifier)
            }
        }
    }
}

enum class VkFunctionModifier {
    OVERRIDE,
    OPEN;

    companion object {

        operator fun invoke(modifier: AlRule): VkFunctionModifier {
            return when (modifier.firstAsRule().firstAsTokenType()) {
                AlTokenType.OVERRIDE -> OVERRIDE
                AlTokenType.OPEN -> OPEN
                else -> throw LineException("illegal function modifier", modifier)
            }
        }
    }
}
