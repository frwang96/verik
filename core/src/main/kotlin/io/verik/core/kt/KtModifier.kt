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
import io.verik.core.al.AlTokenType

enum class KtModifier {
    ENUM;

    companion object {

        operator fun invoke(modifier: AlRule): KtModifier? {
            return when (val type = modifier.firstAsRule().firstAsTokenType()) {
                AlTokenType.ENUM -> ENUM
                AlTokenType.OVERRIDE -> null
                AlTokenType.PUBLIC -> null
                AlTokenType.PRIVATE -> null
                AlTokenType.INTERNAL -> null
                AlTokenType.PROTECTED -> null
                AlTokenType.FINAL -> null
                AlTokenType.OPEN -> null
                else -> throw LineException("modifier $type not supported", modifier)
            }
        }
    }
}