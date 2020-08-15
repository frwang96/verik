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

package verik.core.vk

import verik.core.al.AlRule
import verik.core.kt.KtUtil

object VkUtil {

    fun parseFile(rule: AlRule): VkFile {
        return VkFile(KtUtil.resolveFile(rule))
    }

    fun parseModule(rule: AlRule): VkModule {
        return VkModule(KtUtil.resolveDeclaration(rule))
    }

    fun parsePort(rule: AlRule): VkPort {
        return VkPort(KtUtil.resolveDeclaration(rule))
    }

    fun parseActionBlock(rule: AlRule): VkActionBlock {
        return VkActionBlock(KtUtil.resolveDeclaration(rule))
    }

    fun parseProperty(rule: AlRule): VkProperty {
        return VkProperty(KtUtil.resolveDeclaration(rule))
    }

    fun parseExpression(rule: AlRule): VkExpression {
        return VkExpression(KtUtil.resolveExpression(rule))
    }
}
