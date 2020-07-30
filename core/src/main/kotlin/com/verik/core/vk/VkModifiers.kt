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
import com.verik.core.kt.KtRule
import com.verik.core.kt.KtTokenType

enum class VkClassModifier {
    ENUM,
    OPEN;

    companion object {

        operator fun invoke(modifier: KtRule): VkClassModifier {
            return when (modifier.firstAsRule().firstAsTokenType()) {
                KtTokenType.ENUM -> ENUM
                KtTokenType.OPEN -> OPEN
                else -> throw LinePosException("illegal class modifier", modifier.linePos)
            }
        }
    }
}

enum class VkFunctionModifier {
    OVERRIDE,
    OPEN;

    companion object {

        operator fun invoke(modifier: KtRule): VkFunctionModifier {
            return when (modifier.firstAsRule().firstAsTokenType()) {
                KtTokenType.OVERRIDE -> OVERRIDE
                KtTokenType.OPEN -> OPEN
                else -> throw LinePosException("illegal function modifier", modifier.linePos)
            }
        }
    }
}
